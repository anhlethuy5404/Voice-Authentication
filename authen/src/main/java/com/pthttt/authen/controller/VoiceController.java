package com.pthttt.authen.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/voice")
public class VoiceController {

    @Value("${ai.server.url}")
    private String aiServerUrl; // ví dụ: http://localhost:8000

    @GetMapping("/verify")
    public String showForm() {
        return "voice_verify";
    }

    @PostMapping("/verify")
    public String verifyVoice(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("modelName") String modelName,
            @RequestParam("ckptPath") String ckptPath,
            Model model
    ) {
        try {
            // 1️⃣ Lưu file tạm
            File tempFile = File.createTempFile("voice_", ".wav");
            audioFile.transferTo(tempFile);

            // 2️⃣ Chuẩn bị request JSON
            RestTemplate restTemplate = new RestTemplate();
            String url = aiServerUrl + "/voice/verify_voice/";

            Map<String, String> request = new HashMap<>();
            request.put("model_name", modelName);
            request.put("ckpt_path", ckptPath);
            request.put("file_path", tempFile.getAbsolutePath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            // 3️⃣ Gọi FastAPI
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class
            );

            // 4️⃣ Xử lý phản hồi
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                model.addAttribute("result", response.getBody());
            } else {
                model.addAttribute("error", "Lỗi từ server AI: " + response.getStatusCode());
            }

            // 5️⃣ Xoá file tạm
            tempFile.delete();

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "voice_verify";
    }
}

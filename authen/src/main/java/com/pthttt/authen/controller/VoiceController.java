package com.pthttt.authen.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.pthttt.authen.model.Model;
import com.pthttt.authen.model.TrainRun;
import com.pthttt.authen.repository.ModelRepository;
import com.pthttt.authen.repository.TrainRunRepository;

@Controller
@RequestMapping("/user/voice")
public class VoiceController {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private TrainRunRepository trainRunRepository;

    /**
     * GET: Hiển thị form xác thực giọng nói, chỉ hiển thị các trainRun có versionType = "deepfake"
     */
    @GetMapping("/verify")
    public String showForm(ModelMap model) {
        List<Model> models = modelRepository.findAll();
        model.addAttribute("models", models);

        return "verify_voice";
    }

    @GetMapping("/trainruns/{modelId}")
    @ResponseBody
    public List<Map<String, Object>> getTrainRunsByModel(@PathVariable int modelId) {
        return trainRunRepository.findAll()
                .stream()
                .filter(run -> "deepfake".equalsIgnoreCase(run.getType()))
                .filter(run -> run.getModel().getId() == modelId)
                .map(run -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", run.getId());
                    map.put("version", run.getVersion());
                    map.put("modelId", run.getModel().getId());
                    return map;
                })
                .collect(Collectors.toList());
    }


    /**
     * POST: Gửi yêu cầu xác thực tới server AI
     */
    @PostMapping("/verify")
    public String verifyVoice(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("modelId") Integer selectedModelId,
            @RequestParam("trainRunId") Integer trainRunId,
            ModelMap model
    ) {
        try {
            // Tạo file tạm để gửi sang FastAPI
            File tempFile = File.createTempFile("voice_", ".wav");
            audioFile.transferTo(tempFile);

            // Lấy thông tin model và trainRun tương ứng
            Optional<Model> selectedModelOpt = modelRepository.findById(selectedModelId);
            Optional<TrainRun> selectedRunOpt = trainRunRepository.findById(trainRunId);

            if (selectedModelOpt.isEmpty() || selectedRunOpt.isEmpty()) {
                model.addAttribute("error", "Model hoặc TrainRun không hợp lệ!");
                return showForm(model);
            }

            Model selectedModel = selectedModelOpt.get();
            TrainRun selectedRun = selectedRunOpt.get();

            // Chuẩn bị dữ liệu JSON gửi sang AI server
            RestTemplate restTemplate = new RestTemplate();
            String url = aiServerUrl + "/voice/verify_voice/";

            Map<String, Object> request = new HashMap<>();
            request.put("model_name", selectedModel.getName());
            request.put("ckpt_path", selectedRun.getFilePath());
            request.put("file_path", tempFile.getAbsolutePath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            // Gửi POST request sang server AI
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class
            );

            // Xử lý phản hồi
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                model.addAttribute("result", response.getBody());
            } else {
                model.addAttribute("error", "Lỗi từ server AI: " + response.getStatusCode());
            }

            // Xóa file tạm
            tempFile.delete();

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi xác thực: " + e.getMessage());
        }

        // Nạp lại danh sách model & trainRun sau khi xử lý
        List<Model> models = modelRepository.findAll();
        model.addAttribute("models", models);

        List<Map<String, Object>> trainRunData = trainRunRepository.findAll()
                .stream()
                .filter(run -> "deepfake".equalsIgnoreCase(run.getType()))
                .map(run -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", run.getId());
                    map.put("version", run.getVersion());
                    map.put("modelId", run.getModel().getId());
                    return map;
                })
                .collect(Collectors.toList());
        model.addAttribute("trainRuns", trainRunData);

        return "verify_voice";
    }
}

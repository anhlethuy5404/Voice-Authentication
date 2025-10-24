package com.pthttt.authen.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pthttt.authen.model.User;
import com.pthttt.authen.model.Voice;
import com.pthttt.authen.repository.UserRepository;
import com.pthttt.authen.repository.VoiceRepository;

@Service
public class AddVoiceService {

    @Autowired
    private VoiceRepository voiceRepository;

    @Autowired
    private UserRepository userRepository;

    private final String uploadDirName = "uploads";
    private final Path rootPath;

    public AddVoiceService() {
        this.rootPath = Paths.get(new File("").getAbsolutePath());
    }

    public void saveVoice(MultipartFile file, String username) throws IOException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String userDirId = String.format("id%05d", user.getId());
        
        // Đặt tên file xxxxx.wav
        long voiceCount = voiceRepository.countByUser(user);
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = String.format("%05d", voiceCount) + extension;

        // Tạo dẫn tương đối lưu DB
        Path absoluteUploadPath = rootPath.resolve(uploadDirName);
        Path absoluteUserDirPath = absoluteUploadPath.resolve(userDirId);
        if (!Files.exists(absoluteUserDirPath)) {
            Files.createDirectories(absoluteUserDirPath);
        }
        Path absoluteFilePath = absoluteUserDirPath.resolve(newFileName);
        String relativePath = Paths.get(uploadDirName, userDirId, newFileName).toString().replace('\\', '/');

        // Lưu file
        Files.copy(file.getInputStream(), absoluteFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Lưu Voice
        Voice voice = new Voice();
        voice.setFilePath(relativePath);
        voice.setUser(user);
        voice.setCreatedAt(new Date());
        voiceRepository.save(voice);
    }
}

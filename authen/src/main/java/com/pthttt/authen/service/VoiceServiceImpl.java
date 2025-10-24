package com.pthttt.authen.service;

import com.pthttt.authen.model.User;
import com.pthttt.authen.model.Voice;
import com.pthttt.authen.repository.UserRepository;
import com.pthttt.authen.repository.VoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Service
public class VoiceServiceImpl implements VoiceService {

    private final Path rootLocation = Paths.get("uploads");

    @Autowired
    private VoiceRepository voiceRepository;

    @Autowired
    private UserRepository userRepository;

    public VoiceServiceImpl() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public void saveVoice(MultipartFile file, String username) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IOException("User not found.");
        }

        String filename = user.getId() + "_" + new Date().getTime() + "_" + file.getOriginalFilename();
        Path destinationFile = this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();

        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        Voice voice = new Voice();
        voice.setUser(user);
        voice.setFilePath(destinationFile.toString());
        voice.setCreatedAt(new Date());

        voiceRepository.save(voice);
    }
}

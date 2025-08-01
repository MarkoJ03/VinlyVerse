package server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/slike")
public class SlikaController {

    @Value("${upload.folder}")
    private String uploadFolder;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSlika(@RequestParam("file") MultipartFile file) {
        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

            if (originalFileName == null || originalFileName.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ime fajla je prazno.");
            }

            Path uploadPath = Paths.get(uploadFolder);
            System.out.println("Upload folder: " + uploadPath.toAbsolutePath());

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Kreiran folder: " + uploadPath.toAbsolutePath());
            }

            Path filePath = uploadPath.resolve(originalFileName);
            System.out.println("Upisujem fajl u: " + filePath.toAbsolutePath());

            file.transferTo(filePath.toFile());

            return ResponseEntity.ok("uploads/" + originalFileName);

        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Greška pri uploadu slike: " + e.getMessage());
        }
    }


    @GetMapping("/{filename:.+}")
    public ResponseEntity<byte[]> getSlika(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadFolder).resolve(filename);
        byte[] slika = Files.readAllBytes(filePath);
        return ResponseEntity.ok().body(slika);
    }
}

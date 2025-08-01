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
            Path uploadPath = Paths.get(uploadFolder);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(originalFileName);
            file.transferTo(filePath.toFile());

            return ResponseEntity.ok("/api/slike/" + originalFileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Greška pri uploadu slike");
        }
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<byte[]> getSlika(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadFolder).resolve(filename);
        byte[] slika = Files.readAllBytes(filePath);
        return ResponseEntity.ok().body(slika);
    }
}

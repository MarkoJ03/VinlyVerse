package server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;

@RestController
@RequestMapping("/api/slike")
public class SlikaController {

    @Value("${upload.folder}")
    private String uploadFolder;

    private static final Set<String> ALLOWED_EXT = Set.of("png","jpg","jpeg","webp");
    private static final Set<String> ALLOWED_MIME = Set.of(
            "image/png","image/jpeg","image/jpg","image/webp","image/pjpeg"
    );

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSlika(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fajl je prazan.");
            }

            // 1) Sanitizuj i uzmi samo ime fajla (bez putanje)
            String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
            if (originalName.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ime fajla je prazno.");
            }
            originalName = Paths.get(originalName).getFileName().toString(); // sprečava path traversal u imenu

            // 2) Validacija ekstenzije/MIME
            String ext = "";
            int dot = originalName.lastIndexOf('.');
            if (dot > 0 && dot < originalName.length() - 1) {
                ext = originalName.substring(dot + 1).toLowerCase();
            }
            String ct = file.getContentType();
            boolean okExt  = ALLOWED_EXT.contains(ext);
            boolean okMime = (ct != null && ALLOWED_MIME.contains(ct.toLowerCase()));
            if (!okExt && !okMime) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nedozvoljen tip fajla.");
            }

            // 3) Upis (prepisuje ako već postoji — isto ponašanje kao ranije)
            Path baseDir = Paths.get(uploadFolder).toAbsolutePath().normalize();
            Files.createDirectories(baseDir);
            Path target = baseDir.resolve(originalName).normalize();

            if (!target.startsWith(baseDir)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nevažeća putanja.");
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("uploads/" + originalName);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Greška pri uploadu slike.");
        }
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<?> getSlika(@PathVariable String filename) {
        try {
            if (filename.contains("..")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nevažeće ime fajla.");
            }
            Path baseDir = Paths.get(uploadFolder).toAbsolutePath().normalize();
            Path filePath = baseDir.resolve(filename).normalize();

            if (!filePath.startsWith(baseDir)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nevažeća putanja.");
            }
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fajl nije pronađen.");
            }

            String ct = Files.probeContentType(filePath);
            if (ct == null) ct = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            byte[] bytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, ct)
                    .cacheControl(CacheControl.noCache())
                    .body(bytes);

        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greška pri čitanju fajla.");
        }
    }
}

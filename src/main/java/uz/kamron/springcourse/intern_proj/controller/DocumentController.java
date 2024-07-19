package uz.kamron.springcourse.intern_proj.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.kamron.springcourse.intern_proj.api.CrptApi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v3/lk/documents")
public class DocumentController {

    private final CrptApi crptApi;

    public DocumentController() {
        this.crptApi = new CrptApi(TimeUnit.SECONDS, 5);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDocument(@RequestBody CrptApi.Document document, @RequestHeader("Signature") String signature) {
        try {
            crptApi.createDocument(document, signature);
            return ResponseEntity.ok("Документ создан успешно! ");
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(500).body("Ошибка создания документа! ");
        }
    }
}

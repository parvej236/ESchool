package com.parvej.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/translate")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TranslateController {

    private final RestTemplate restTemplate;

    /**
     * POST /api/translate
     * Body: { "text": "Hello", "from": "en", "to": "bn" }
     * Returns: { "success": true, "translatedText": "হ্যালো" }
     *
     * Uses MyMemory free translation API — no API key needed
     */
    @PostMapping
    public ResponseEntity<?> translate(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            String from = request.get("from");
            String to   = request.get("to");

            if (text == null || text.isBlank())
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Text is required"));

            if (from == null || to == null)
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "from and to language codes are required"));

            // ✅ Manually URL-encode the text — fixes the fromHttpUrl issue
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String langPair    = from + "|" + to;
            String encodedPair = URLEncoder.encode(langPair, StandardCharsets.UTF_8);

            String url = "https://api.mymemory.translated.net/get"
                    + "?q="        + encodedText
                    + "&langpair=" + encodedPair;

            // Call MyMemory API
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null)
                return ResponseEntity.status(500)
                        .body(Map.of("error", "No response from translation service"));

            // Check response status
            Integer responseStatus = (Integer) response.get("responseStatus");
            if (responseStatus == null || responseStatus != 200) {
                String details = String.valueOf(response.get("responseDetails"));
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Translation failed: " + details));
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> responseData =
                    (Map<String, Object>) response.get("responseData");

            String translated = (String) responseData.get("translatedText");

            return ResponseEntity.ok(Map.of(
                    "success",        true,
                    "translatedText", translated,
                    "from",           from,
                    "to",             to
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Translation service unavailable: " + e.getMessage()));
        }
    }
}
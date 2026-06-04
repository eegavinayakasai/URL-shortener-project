    package com.evs.UrlShortenerProject.controller;

    import com.evs.UrlShortenerProject.dto.ShortenRequest;
    import com.evs.UrlShortenerProject.dto.ShortenResponse;
    import com.evs.UrlShortenerProject.service.UrlShortenerService;
    import jakarta.validation.Valid;
    import jakarta.validation.constraints.NotBlank;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;

    import java.net.URI;
    import java.util.List;

    @RestController
    @RequiredArgsConstructor
    @Validated
    @RequestMapping("/api")
    public class UrlShortenerController
    {
        private final UrlShortenerService urlShortenerService;

        @PostMapping("/shorten")
        public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest shortenRequest)
        {
            ShortenResponse shortenResponse = urlShortenerService.shorten(shortenRequest);
            return ResponseEntity.ok(shortenResponse);
        }

        @PostMapping("/generateQr")
        public ResponseEntity<byte[]> getQr(@RequestBody @Valid ShortenRequest shortenRequest)
        {
            byte[] qrImage = urlShortenerService.generateQRCode(shortenRequest);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrImage);
        }

        @GetMapping("/{code}")
        public ResponseEntity<Void> redirect(@PathVariable String code)
        {
            String originalUrl = urlShortenerService.getOriginalUrl(code);
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        }

        @GetMapping("/stats")
        public ResponseEntity<ShortenResponse> getStats(@RequestParam @NotBlank String shortUrl)
        {
            String shortCode = shortUrl.contains("/")
                    ? shortUrl.substring(shortUrl.lastIndexOf("/") + 1)
                    : shortUrl;
            ShortenResponse stats = urlShortenerService.getStats(shortCode);
            return ResponseEntity.ok(stats);
        }

        @GetMapping("/my-urls")
        public ResponseEntity<List<ShortenResponse>> getUrls()
        {
            return ResponseEntity.ok(urlShortenerService.getMyUrls());
        }
    }

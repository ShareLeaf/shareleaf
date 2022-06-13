package co.shareleaf.controller;

import co.shareleaf.model.*;
import co.shareleaf.service.content.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Slf4j
@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @PostMapping("/generate-uid")
    public Mono<ResponseEntity<SLContentId>> generateContentId(SLContentUrl url) {
        return contentService.generateContentId(url).map(ResponseEntity::ok);
    }

    @GetMapping("/metadata")
    public Mono<ResponseEntity<SLContentMetadata>> getMetadata(String uid) {
        return contentService.getMetadata(uid).map(ResponseEntity::ok);
    }

    @PostMapping("/shares")
    public Mono<ResponseEntity<SLGenericResponse>> incrementShareCount(SLContentId contentId) {
        return contentService.incrementShareCount(contentId).map(ResponseEntity::ok);
    }
}

package co.shareleaf.controller;

import co.shareleaf.api.ContentApi;
import co.shareleaf.model.SLContentId;
import co.shareleaf.model.SLContentMetadata;
import co.shareleaf.model.SLContentUrl;
import co.shareleaf.model.SLGenericResponse;
import co.shareleaf.service.content.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ContentController implements ContentApi {
    private final ContentService contentService;

    @Override
    public ResponseEntity<SLContentMetadata> generateContentId(SLContentUrl slContentUrl) {
        return ResponseEntity.ok(contentService.generateContentId(slContentUrl));
    }

    @Override
    public ResponseEntity<SLContentMetadata> getMetadata(String uid) {
       return ResponseEntity.ok(contentService.getMetadata(uid));
    }

    @Override
    public ResponseEntity<SLGenericResponse> incrementShareCount(SLContentId slContentId) {
        return ResponseEntity.ok(contentService.incrementShareCount(slContentId));
    }
}

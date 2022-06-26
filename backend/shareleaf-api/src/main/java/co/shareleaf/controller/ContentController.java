package co.shareleaf.controller;

import co.shareleaf.api.ApiUtil;
import co.shareleaf.api.ContentApi;
import co.shareleaf.model.*;
import co.shareleaf.service.content.ContentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    /**
     * POST /content/generate-uid : Generate content id
     * Generate a unique content id
     *
     * @param slContentUrl Content URL (required)
     * @return Generate id response (status code 200)
     */
    @ApiOperation(value = "Generate content id", nickname = "generateContentId", notes = "Generate a unique content id", response = SLContentId.class, tags={ "content", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Generate id response", response = SLContentId.class) })
    @PostMapping(
            value = "/content/generate-uid",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public Mono<ResponseEntity<SLContentMetadata>> generateContentId(
            @ApiParam(value = "Content URL" ,required=true )  @Valid @RequestBody SLContentUrl slContentUrl) {
        return contentService.generateContentId(slContentUrl).map(ResponseEntity::ok);
    }


    /**
     * GET /content/metadata
     * Get content metadata
     *
     * @param uid  (required)
     * @return Metadata response (status code 200)
     */
    @ApiOperation(value = "", nickname = "getMetadata", notes = "Get content metadata", response = SLContentMetadata.class, tags={ "content", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Metadata response", response = SLContentMetadata.class) })
    @GetMapping(
            value = "/content/metadata",
            produces = { "application/json" }
    )
    public Mono<ResponseEntity<SLContentMetadata>> getMetadata(
            @NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "uid", required = true) String uid) {
        return contentService.getMetadata(uid).map(ResponseEntity::ok);
    }


    /**
     * POST /content/shares : Increment share count
     * Increment share count
     *
     * @param slContentId Content URL (required)
     * @return Generic response (status code 200)
     */
    @ApiOperation(value = "Increment share count", nickname = "incrementShareCount", notes = "Increment share count", response = SLGenericResponse.class, tags={ "content", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Generic response", response = SLGenericResponse.class) })
    @PostMapping(
            value = "/content/shares",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public Mono<ResponseEntity<SLGenericResponse>> incrementShareCount
    (@ApiParam(value = "Content URL" ,required=true )  @Valid @RequestBody SLContentId slContentId) {
        return contentService.incrementShareCount(slContentId).map(ResponseEntity::ok);
    }
}

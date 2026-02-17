package org.bit.bitgram.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bit.bitgram.domain.post.dto.PostCreateRequest;
import org.bit.bitgram.domain.post.service.PostService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Tag(name = "Post API", description = "게시물 관련 API")
@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시물 생성", description = "이미지와 내용을 업로드하여 게시물을 만듭니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createPost(
            @RequestPart(value = "data") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    )   {
        log.info("게시물 생성 요청 - 내용: {}, 위치: {}", request.getContents(), request.getLocationName());

        Long postId = postService.create(request, images);
        return ResponseEntity.ok(postId);
    }
}

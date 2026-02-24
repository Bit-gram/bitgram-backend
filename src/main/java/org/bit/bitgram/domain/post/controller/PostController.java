package org.bit.bitgram.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bit.bitgram.domain.post.dto.PostCreateRequest;
import org.bit.bitgram.domain.post.dto.PostResponse;
import org.bit.bitgram.domain.post.dto.PostUpdateRequest;
import org.bit.bitgram.domain.post.service.PostService;
import org.bit.bitgram.global.common.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Tag(name = "Post API", description = "게시물 관련 API")
@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    /**
     * Creates a new post from the provided content and optional image files.
     *
     * @param request DTO containing post content, location, and other metadata
     * @param images  optional list of image files to attach to the post
     * @return the ID of the newly created post wrapped in an ApiResponse
     */
    @Operation(summary = "게시물 생성", description = "이미지와 내용을 업로드하여 게시물을 만듭니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> createPost(
            @RequestPart(value = "data") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    )   {
        log.info("게시물 생성 요청 - 내용: {}, 위치: {}", request.getContent(), request.getLocationName());

        Long postId = postService.create(request, images);
        return ResponseEntity.ok(ApiResponse.success(postId));
    }

    /**
     * Retrieve a single post by its ID.
     *
     * @param postId the ID of the post to retrieve
     * @return an ApiResponse containing the post details for the given ID
     */
    @Operation(summary = "게시물 단건 조회", description = "ID로 게시물 하나를 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieve a paginated list of posts ordered by most recent.
     *
     * @param pageable paging and sorting information; defaults to 5 items per page sorted by `createdAt` descending
     * @return an ApiResponse wrapping a Page of PostResponse for the requested page
     */
    @Operation(summary = "게시물 전체 조회", description = "최신순으로 게시물을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllPosts(
        // size=5: 한 번에 5개씩 가져와라
        // sort=createdAt,DESC: 최신순(내림차순)으로 정렬해라
        @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PostResponse> response = postService.getAllPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Updates an existing post's text and, if new images are supplied, replaces its images.
     *
     * @param postId the identifier of the post to update
     * @param request the update payload containing editable post fields (e.g., title, content)
     * @param images optional list of image files; when present these images replace the post's existing images
     * @return the ID of the updated post
     */
    @Operation(summary = "게시물 수정", description = "텍스트를 수정하고, 새 이미지를 업로드하면 기존 이미지를 대체합니다.")
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> updatePost(
            @PathVariable Long postId,
            @RequestPart(value = "data")PostUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        Long updatedPostId = postService.update(postId, request, images);
        return ResponseEntity.ok(ApiResponse.success(updatedPostId));
    }

    /**
     * Soft-delete the post with the given identifier.
     *
     * @param postId the identifier of the post to soft-delete
     * @return an ApiResponse<Void> indicating the deletion succeeded
     */
    @Operation(summary = "게시물 삭제", description = "게시물을 삭제 처리(Soft Delete) 합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}

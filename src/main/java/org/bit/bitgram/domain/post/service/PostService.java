package org.bit.bitgram.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bit.bitgram.domain.post.dto.PostCreateRequest;
import org.bit.bitgram.domain.post.dto.PostResponse;
import org.bit.bitgram.domain.post.dto.PostUpdateRequest;
import org.bit.bitgram.domain.post.entity.Post;
import org.bit.bitgram.domain.post.entity.PostImage;
import org.bit.bitgram.domain.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * Creates a new post from the given request and attaches any provided images.
     *
     * @param request DTO containing the post content, locationName, and status
     * @param images  optional list of image files to associate with the created post; may be null or empty
     * @return        the generated database ID of the created post
     */
    @Transactional
    public Long create(PostCreateRequest request, List<MultipartFile> images) {
        // 게시물 엔티티 생성
        Post post = Post.builder()
                .userId(1L)
                .content(request.getContent())
                .locationName(request.getLocationName())
                .status(request.getStatus())
                .build();

        // 이미지가 있다면 하나씩 처리
        if (images != null && !images.isEmpty()) {
            savePostImage(images, post);
        }

        // DB 저장
        return postRepository.save(post).getId();
    }

    /**
     * Retrieve a post by its identifier and convert it to a PostResponse.
     *
     * @param postId the identifier of the post to retrieve
     * @return the PostResponse representation of the found post
     * @throws IllegalArgumentException if no post exists with the given id
     */
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id = " + postId));
        return PostResponse.from(post);
    }

    /**
     * Retrieve a page of posts as PostResponse DTOs.
     *
     * @param pageable pagination and sorting information for the query
     * @return a Page of PostResponse objects for the requested page
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostResponse::from);
    }

    /**
     * Update an existing post and optionally replace its images.
     *
     * @param postId the identifier of the post to update
     * @param request DTO carrying new post data (content, locationName, status)
     * @param images optional list of image files; if non-null and non-empty the post's existing images are cleared and replaced with these
     * @return the id of the updated post
     * @throws IllegalArgumentException if no post exists with the given id
     */
    @Transactional
    public Long update(Long postId, PostUpdateRequest request, List<MultipartFile> images) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id = " + postId));

        post.update(request.getContent(), request.getLocationName(), request.getStatus());

        if (images != null && !images.isEmpty()) {
            // 기존 이미지 삭제
            post.getImages().clear();

            savePostImage(images, post);
        }
        return post.getId();
    }

    /**
     * Performs a soft delete of the post identified by the given ID.
     *
     * @param postId the identifier of the post to soft-delete
     * @throws IllegalArgumentException if no post exists with the given ID
     */
    @Transactional
    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id=" + postId));
        post.softDelete();
    }

    /**
     * Creates PostImage entities for the supplied image files, uploads each file, and attaches them to the given post.
     *
     * Each created PostImage is associated with the post, uses the uploaded image URL, has a zero-based display order
     * corresponding to the file list index, and an aspect ratio of "1:1".
     *
     * @param images the image files to upload and attach to the post; may be empty but must not be null
     * @param post   the post to which the created PostImage entities will be added
     */
    private void savePostImage(List<MultipartFile> images, Post post) {
        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);

            // 이미지 업로드
            String imageUrl = uploadImage(file);

            // 이미지 엔티티 생성
            PostImage postImage = PostImage.builder()
                    .post(post)
                    .imageUrl(imageUrl)
                    .displayOrder(i)
                    .aspectRatio("1:1")
                    .build();

            // 게시물에 이미지 추가
            post.addImage(postImage);
        }
    }

    /**
     * Constructs and returns a dummy public URL for the provided image using its original filename.
     *
     * @param image the uploaded file whose original filename will be used to build the URL
     * @return the dummy URL formed as "https://dummy-s3-url.com/{originalFilename}"
     */
    private String uploadImage(MultipartFile image) {
        log.info("이미지 업로드: {}", image.getOriginalFilename());
        return "https://dummy-s3-url.com/" + image.getOriginalFilename();

    }
}

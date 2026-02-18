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

    // 게시물 업로드
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

    // 단건 조회
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다. id = " + postId));
        return PostResponse.from(post);
    }

    // 전체 조회 (페이징 처리)
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostResponse::from);
    }

    // 게시물 수정
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

    private String uploadImage(MultipartFile image) {
        log.info("이미지 업로드: {}", image.getOriginalFilename());
        return "https://dummy-s3-url.com/" + image.getOriginalFilename();

    }
}

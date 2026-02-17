package org.bit.bitgram.domain.post.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bit.bitgram.domain.post.dto.PostCreateRequest;
import org.bit.bitgram.domain.post.entity.Post;
import org.bit.bitgram.domain.post.entity.PostImage;
import org.bit.bitgram.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Long create(PostCreateRequest request, List<MultipartFile> images) {
        // 게시물 엔티티 생성
        Post post = Post.builder()
                .userId(1L)
                .content(request.getContents())
                .locationName(request.getLocationName())
                .status(request.getStatus())
                .build();

        // 이미지가 있다면 하나씩 처리
        if (images != null && !images.isEmpty()) {
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

        // DB 저장
        return postRepository.save(post).getId();
    }

    private String uploadImage(MultipartFile image) {
        log.info("이미지 업로드: {}", image.getOriginalFilename());
        return "https://example.com/image.jpg";

    }
}

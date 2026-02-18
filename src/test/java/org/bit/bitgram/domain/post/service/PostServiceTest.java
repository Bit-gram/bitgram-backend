package org.bit.bitgram.domain.post.service;

import org.bit.bitgram.domain.post.dto.PostCreateRequest;
import org.bit.bitgram.domain.post.entity.Post;
import org.bit.bitgram.domain.post.entity.PostStatus;
import org.bit.bitgram.domain.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Mockito 사용 설정
class PostServiceTest {

    @Mock
    private PostRepository postRepository; // 가짜 DB

    @InjectMocks
    private PostService postService; // 테스트할 대상 (가짜 DB가 주입됨)

    @Test
    @DisplayName("게시물과 이미지 2장이 정상적으로 저장되어야 한다")
    void createPostWithImagesSuccess() {
        // 1. Given (준비)
        // 가짜 요청 데이터 만들기
        PostCreateRequest request = new PostCreateRequest();
        // DTO 필드에 값을 넣으려는데 Setter가 없다면 ReflectionTestUtils 사용 (또는 DTO에 생성자/Builder 추가)
        ReflectionTestUtils.setField(request, "content", "테스트 게시물입니다.");
        ReflectionTestUtils.setField(request, "locationName", "서울");
        ReflectionTestUtils.setField(request, "status", PostStatus.PUBLIC);

        // 가짜 이미지 파일 2개 만들기
        List<MultipartFile> images = List.of(
                new MockMultipartFile("image", "photo1.jpg", "image/jpeg", "fake-image-1".getBytes()),
                new MockMultipartFile("image", "photo2.jpg", "image/jpeg", "fake-image-2".getBytes())
        );

        // repository.save()가 호출되면, 임의의 Post 객체(ID=1)를 리턴하라고 가짜 행동 정의
        Post savedPost = Post.builder().userId(1L).build();
        ReflectionTestUtils.setField(savedPost, "id", 1L); // ID 강제 주입
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // 2. When (실행)
        Long resultId = postService.create(request, images);

        // 3. Then (검증) - 여기가 핵심! (White-box Test)
        assertThat(resultId).isEqualTo(1L);

        // 실제로 save() 메서드에 어떤 Post 객체가 넘어갔는지 낚아채서 확인 (ArgumentCaptor)
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post capturedPost = postCaptor.getValue();

        // 검증 1: 내용이 잘 들어갔나?
        assertThat(capturedPost.getContent()).isEqualTo("테스트 게시물입니다.");
        assertThat(capturedPost.getUserId()).isEqualTo(1L);

        // 검증 2: 이미지가 2장 잘 연결됐나?
        assertThat(capturedPost.getImages()).hasSize(2);

        // 검증 3: 이미지 순서(displayOrder)가 0, 1로 잘 들어갔나? (중요 로직)
        assertThat(capturedPost.getImages().get(0).getDisplayOrder()).isEqualTo(0);
        assertThat(capturedPost.getImages().get(0).getImageUrl()).contains("photo1.jpg");

        assertThat(capturedPost.getImages().get(1).getDisplayOrder()).isEqualTo(1);
        assertThat(capturedPost.getImages().get(1).getImageUrl()).contains("photo2.jpg");
    }
}
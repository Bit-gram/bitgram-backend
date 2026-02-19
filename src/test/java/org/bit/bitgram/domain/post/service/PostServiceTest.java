package org.bit.bitgram.domain.post.service;

import org.bit.bitgram.domain.post.dto.PostCreateRequest;
import org.bit.bitgram.domain.post.dto.PostResponse;
import org.bit.bitgram.domain.post.dto.PostUpdateRequest;
import org.bit.bitgram.domain.post.entity.Post;
import org.bit.bitgram.domain.post.entity.PostImage;
import org.bit.bitgram.domain.post.entity.PostStatus;
import org.bit.bitgram.domain.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Mockito 사용 설정
class PostServiceTest {

    @Mock
    private PostRepository postRepository; // 가짜 DB

    @InjectMocks
    private PostService postService; // 테스트할 대상 (가짜 DB가 주입됨)
    private PostResponse postResponse;

    // =========================================================================
    // 1. CREATE (생성 테스트)
    // =========================================================================
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

    // =========================================================================
    // 2. READ (조회 테스트)
    // =========================================================================
    @Test
    @DisplayName("ID로 게시물 단건 조회를 성공해야 한다")
    void getPost_Success() {
        // Given
        Post post = Post.builder().userId(1L).content("조회 테스트").status(PostStatus.PUBLIC).build();
        ReflectionTestUtils.setField(post, "id", 100L);
        // 테스트용 이미지 추가
        post.addImage(PostImage.builder().imageUrl("http://test.com/img.jpg").build());

        // 가짜 DB가 100번을 찾으면 위에서 만든 post를 리턴하도록 설정
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));

        // When
        PostResponse response = postService.getPost(100L);

        // Then
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getContent()).isEqualTo("조회 테스트");
        assertThat(response.getImageUrls()).hasSize(1);
        assertThat(response.getImageUrls().get(0)).isEqualTo("http://test.com/img.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 게시물 ID로 조회 시 예외가 발생해야 한다")
    void getPost_Fail_NotFound() {
        // Given
        when(postRepository.findById(999L)).thenReturn(Optional.empty()); // DB에 없음

        // When & Then
        // 에러가 터지는지(Throw) 확인
        assertThatThrownBy(() -> postService.getPost(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 게시물이 없습니다");
    }

    @Test
    @DisplayName("게시물 피드(목록) 페이징 조회를 성공해야 한다")
    void getAllPosts_Success() {
        // Given
        Post post1 = Post.builder().content("첫번째 글").build();
        Post post2 = Post.builder().content("두번째 글").build();
        Pageable pageable = PageRequest.of(0, 5);
        Page<Post> mockPage = new PageImpl<>(List.of(post1, post2), pageable, 2);

        when(postRepository.findAll(pageable)).thenReturn(mockPage);

        // When
        Page<PostResponse> result = postService.getAllPosts(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2); // 데이터가 2개인지
        assertThat(result.getContent().get(0).getContent()).isEqualTo("첫번째 글"); // 내용이 맞는지
        assertThat(result.getTotalElements()).isEqualTo(2); // 총 데이터 개수
    }

    // =========================================================================
    // 3. UPDATE (수정 테스트)
    // =========================================================================
    @Test
    @DisplayName("게시물 내용 수정 및 이미지 교체가 정상적으로 이루어져야 한다")
    void updatePost_Success() {
        // Given
        // 1. 기존 게시물 (이미지 1장 있음)
        Post post = Post.builder().content("수정 전 내용").status(PostStatus.PUBLIC).build();
        ReflectionTestUtils.setField(post, "id", 1L);
        post.addImage(PostImage.builder().imageUrl("old-image.jpg").build());

        // 2. 수정 요청 데이터 (내용 바꿈)
        PostUpdateRequest request = new PostUpdateRequest();
        ReflectionTestUtils.setField(request, "content", "수정 후 내용");
        ReflectionTestUtils.setField(request, "locationName", "제주도");
        ReflectionTestUtils.setField(request, "status", PostStatus.PRIVATE);

        // 3. 새로운 이미지 (기존 1장을 지우고 새 이미지 2장으로 교체)
        List<MultipartFile> newImages = List.of(
                new MockMultipartFile("image", "new1.jpg", "image/jpeg", "new1".getBytes()),
                new MockMultipartFile("image", "new2.jpg", "image/jpeg", "new2".getBytes())
        );

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When
        postService.update(1L, request, newImages);

        // Then
        // 내용이 잘 바뀌었는지 확인
        assertThat(post.getContent()).isEqualTo("수정 후 내용");
        assertThat(post.getLocationName()).isEqualTo("제주도");
        assertThat(post.getStatus()).isEqualTo(PostStatus.PRIVATE);

        // 이미지가 기존꺼 지워지고 새걸로 2장 채워졌는지 확인 (orphanRemoval 로직 검증)
        assertThat(post.getImages()).hasSize(2);
        assertThat(post.getImages().get(0).getImageUrl()).contains("new1.jpg");
    }

    // =========================================================================
    // 4. DELETE (삭제 테스트)
    // =========================================================================
    @Test
    @DisplayName("게시물 삭제(Soft Delete) 시 상태가 DELETED로 변경되어야 한다")
    void deletePost_Success() {
        // Given
        Post post = Post.builder().content("삭제될 글").status(PostStatus.PUBLIC).build();
        ReflectionTestUtils.setField(post, "id", 1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When
        postService.delete(1L);

        // Then
        // 상태가 DELETED 로 잘 바뀌었는지 확인
        assertThat(post.getStatus()).isEqualTo(PostStatus.DELETED);
    }
}
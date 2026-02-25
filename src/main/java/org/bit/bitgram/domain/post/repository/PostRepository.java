package org.bit.bitgram.domain.post.repository;

import org.bit.bitgram.domain.post.entity.Post;
import org.bit.bitgram.domain.post.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 게시글 상태(PostStatus)를 기준으로 페이징 조회
     *
     * @param status   조회할 게시글 상태 값
     * @param pageable 페이징 정보
     * @return 상태에 해당하는 게시글 목록 (Page)
     */
    Page<Post> findByStatus(PostStatus status, Pageable pageable);
}

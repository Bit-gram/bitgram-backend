package org.bit.bitgram.domain.post.repository;

import org.bit.bitgram.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 특정 사용자가 좋아요를 누른 기록을 최신순으로 조회
    List<PostLike> findPostLikesByUserIdOrderByCreatedAtDesc(Long userId);

}

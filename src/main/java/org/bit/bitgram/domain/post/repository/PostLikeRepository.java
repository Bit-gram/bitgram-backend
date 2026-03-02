package org.bit.bitgram.domain.post.repository;

import org.bit.bitgram.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 특정 사용자가 좋아요를 누른 기록을 최신순으로 조회
     *
     * @param userId 좋아요 기록을 조회할 대상 사용자의 식별자
     * @return 해당 사용자의 좋아요 기록 목록 (생성일자 내림차순 정렬)
     */
    List<PostLike> findPostLikesByUserIdOrderByCreatedAtDesc(Long userId);

}

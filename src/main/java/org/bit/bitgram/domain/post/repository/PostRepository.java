package org.bit.bitgram.domain.post.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.bit.bitgram.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * TODO: 내가 팔로우한 계정의 글도 제외 필요
     */
    // 취향을 모를 때 (콜드 스타트, 눈팅 && 내 글 제외)
    // +) 인기순, 최신순 정렬 && 좋아요가 커서보다 작거나, 좋아요가 커서와 같으면서 ID가 커서보다 작은 게시글
    @Query("SELECT p FROM Post p " +
            "WHERE p.userId <> :userId " +
            "AND (:cursorLikeCount IS NULL OR " +
            "   (p.likeCount < :cursorLikeCount OR (p.likeCount = :cursorLikeCount AND p.postId < :cursorId))" +
            ") " +
            "ORDER BY p.likeCount DESC, p.postId DESC")
    List<Post> findExploreFallback(
            @Param("userId") Long userId,
            @Param("cursorLikeCount") Long cursorLikeCount,
            @Param("cursorId") Long cursorId,
            Pageable pageable);

    // 백필링 (내 글 제외 && 추천된 글과 중복 방지)
    // +) 인기순, 최신순 정렬 && 좋아요가 커서보다 작거나, 좋아요가 커서와 같으면서 ID가 커서보다 작은 게시글
    @Query("SELECT p FROM Post p " +
            "WHERE p.userId <> :userId " +
            "AND p.id NOT IN :excludeIds " +
            "AND (:cursorLikeCount IS NULL OR " +
            "   (p.likeCount < :cursorLikeCount OR (p.likeCount = :cursorLikeCount AND p.postId < :cursorId))" +
            ") " +
            "ORDER BY p.likeCount DESC, p.postId DESC")
    List<Post> findExploreBackfill(
            @Param("userId") Long userId,
            @Param("excludeIds") List<Long> excludeIds,
            @Param("cursorLikeCount") Long cursorLikeCount,
            @Param("cursorId") Long cursorId,
            Pageable pageable);

}

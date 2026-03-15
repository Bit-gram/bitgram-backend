package org.bit.bitgram.domain.hashtag.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.bit.bitgram.domain.hashtag.entity.PostHashtag;
import org.bit.bitgram.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    // 게시글 id 목록에 해당하는 게시글-해시태그 매핑 정보를 한 번에 조회
    @Query("SELECT ph FROM PostHashtag ph JOIN FETCH ph.hashtag WHERE ph.post.postId IN :postIds")
    List<PostHashtag> findByPostIdIn(@Param("postIds") List<Long> postIds);

    // 해시태그 기반으로 후보 게시글(중복 제외, 내 글 제외) 조회
    // +) 최신순 정렬 && ID가 커서보다 작은 게시글 (커서: 마지막으로 본 게시글 ID)
    @Query("SELECT DISTINCT ph.post FROM PostHashtag ph " +
            "WHERE ph.hashtag.name IN :hashtags " +
            "AND ph.post.userId <> :userId " +
            "AND (:cursorId IS NULL OR :cursorId > ph.post.postId) " +
            "ORDER BY ph.post.postId DESC") // 최신순 정렬
    List<Post> findExploreCandidates(
            @Param("userId") Long userId,
            @Param("hashtags") List<String> hashtags,
            @Param("cursorId") Long cursorId,
            Pageable pageable);

}

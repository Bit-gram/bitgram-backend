package org.bit.bitgram.domain.post.repository;

import org.bit.bitgram.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 취향을 모를 때 보여줄 인기글 (내 글 제외)
    List<Post> findTop20ByUserIdNotOrderByLikeCountDesc(Long userId);

    // 데이터가 부족할 때 중복 없이 채워 넣을 인기글
    List<Post> findByUserIdNotAndIdNotInOrderByLikeCountDesc(Long userId, List<Long> excludeIds, Pageable pageable);

}

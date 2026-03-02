package org.bit.bitgram.domain.hashtag.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.bit.bitgram.domain.hashtag.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    /**
     * 게시글 id 목록에 해당하는 게시글-해시태그 매핑 정보를 한 번에 조회
     *
     * @param postIds 조회할 대상 게시글들의 id 목록
     * @return 게시글-해시태그 매핑 정보 목록
     */
    @Query("SELECT ph FROM PostHashtag ph JOIN FETCH ph.hashtag WHERE ph.post.id in :postIds")
    List<PostHashtag> findByPostIdIn(@Param("postIds") List<Long> postIds);

}

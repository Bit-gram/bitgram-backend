package org.bit.bitgram.domain.hashtag.repository;

import org.bit.bitgram.domain.hashtag.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}

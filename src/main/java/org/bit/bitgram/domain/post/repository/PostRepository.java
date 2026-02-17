package org.bit.bitgram.domain.post.repository;

import org.bit.bitgram.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}

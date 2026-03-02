package org.bit.bitgram.domain.hashtag.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bit.bitgram.domain.post.entity.Post;
import org.bit.bitgram.global.common.CreatedTimeEntity;

@Getter
@Entity
@Table(name = "post_hashtags", uniqueConstraints = {
        // 하나의 게시글에는 똑같은 해시태그가 두 번 달릴 수 없다.
        @UniqueConstraint(
                name = "uk_post_hashtag",
                columnNames = {"post_id", "hashtag_id"}
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostHashtag extends CreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_hashtag_id")
    private Long postHashtagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

}

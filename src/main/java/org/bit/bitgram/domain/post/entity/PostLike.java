package org.bit.bitgram.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bit.bitgram.domain.user.entity.User;
import org.bit.bitgram.global.common.CreatedTimeEntity;

@Getter
@Entity
@Table(name = "likes", uniqueConstraints = {
        // 한 명의 사용자는 같은 게시글에 좋아요를 두 번 누를 수 없다.
        @UniqueConstraint(
                name = "uk_likes_post_user",
                columnNames = {"post_id", "user_id"}
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends CreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}

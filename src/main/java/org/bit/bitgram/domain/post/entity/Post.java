package org.bit.bitgram.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bit.bitgram.global.common.ModifiedTimeEntity; // 아까 만든 공통 엔티티

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class Post extends ModifiedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    // @Column(name = "location_point", columnDefinition = "geometry")
    // private Point locationPoint;

    @Column(name = "location_name", length = 255)
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    /**
     * Creates a Post entity with the given owner, content, location, and status.
     *
     * @param userId       the identifier of the user who created the post
     * @param content      the textual content of the post
     * @param locationName the human-readable location name associated with the post, or null if unspecified
     * @param status       the visibility/status of the post; if null, defaults to {@code PostStatus.PUBLIC}
     */
    @Builder
    public Post(Long userId, String content, String locationName, PostStatus status) {
        this.userId = userId;
        this.content = content;
        this.locationName = locationName;
        this.status = (status != null) ? status : PostStatus.PUBLIC;
        this.likeCount = 0L;
        this.commentCount = 0L;
    }

    /**
     * Updates the post's content, location name, and visibility status.
     *
     * @param content      the new text content for the post
     * @param locationName the new display name of the post's location (may be null)
     * @param status       the new post status (e.g., PUBLIC, PRIVATE, DELETED); if null the status will be set accordingly by caller
     */
    public  void update(String content, String locationName, PostStatus status) {
        this.content = content;
        this.locationName = locationName;
        this.status = status;
    }

    /**
     * Mark the post as deleted.
     *
     * Sets the post's status to {@code PostStatus.DELETED} and records the current time in {@code deletedAt}.
     */
    public void softDelete() {
        this.status = PostStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Adds the given image to this post's images collection.
     *
     * @param image the PostImage to add to the post
     */
    public void addImage(PostImage image) {
        this.images.add(image);
    }
}
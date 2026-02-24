package org.bit.bitgram.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "images")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "aspect_ratio")
    private String aspectRatio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * Creates a PostImage with the specified URL, display order, aspect ratio, and associated post.
     *
     * @param imageUrl    the image URL or path (must not be null)
     * @param displayOrder the ordering index used to sequence images for display
     * @param aspectRatio an optional aspect ratio string (e.g., "16:9"); may be null
     * @param post        the associated Post entity (expected to be non-null)
     */
    @Builder
    public PostImage(String imageUrl, Integer displayOrder, String aspectRatio, Post post) {
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.aspectRatio = aspectRatio;
        this.post = post;
    }
}

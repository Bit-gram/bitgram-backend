package org.bit.bitgram.domain.post.dto;
import lombok.Builder;
import lombok.Getter;
import org.bit.bitgram.domain.post.entity.Post;
import org.bit.bitgram.domain.post.entity.PostImage;
import org.bit.bitgram.domain.post.entity.PostStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PostResponse {
    private Long id;
    private Long userId;
    private String content;
    private String locationName;
    private List<String> imageUrls;
//    private Long likeCount;
//    private Long commentCount;
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .locationName(post.getLocationName())
                .imageUrls(post.getImages().stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList()))
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}

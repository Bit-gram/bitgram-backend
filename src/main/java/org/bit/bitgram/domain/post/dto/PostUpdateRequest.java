package org.bit.bitgram.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bit.bitgram.domain.post.entity.PostStatus;

@Getter
@Setter
@NoArgsConstructor
public class PostUpdateRequest {
    private String content;
    private String locationName;
    private PostStatus status;
}

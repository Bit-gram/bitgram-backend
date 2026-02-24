package org.bit.bitgram.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bit.bitgram.domain.post.entity.PostStatus;

@Getter
@NoArgsConstructor
public class PostCreateRequest {
    private String content;
    private String locationName;
    private PostStatus status;
}

package org.bit.bitgram.domain.explore.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityWeight {

    SEARCH(1, "검색"),
    LIKE(2, "좋아요"),
    COMMENT(3, "댓글"),
    BOOKMARK(4, "북마크"),
    SHARE(5, "공유");

    private final int score;
    private final String description;

}

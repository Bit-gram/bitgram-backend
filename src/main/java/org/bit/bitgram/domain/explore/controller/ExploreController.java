package org.bit.bitgram.domain.explore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bit.bitgram.domain.explore.service.ExploreService;
import org.bit.bitgram.domain.post.dto.PostResponse;
import org.bit.bitgram.global.common.ApiResponse;
import org.bit.bitgram.global.security.user.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Explore API", description = "탐색 피드 API")
@RestController
@RequestMapping("/api/explore")
@RequiredArgsConstructor
public class ExploreController {

    private final ExploreService exploreService;

    @Operation(summary = "탐색 피드 조회", description = "사용자의 관심사 기반으로 탐색 피드를 무한 스크롤로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getExploreFeed(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long cursorLikeCount,
            @RequestParam(required = false) Long cursorId
    ) {
        Long userId = userDetails.getUser().getUserId();

        List<PostResponse> response = exploreService.getExploreFeed(userId, cursorLikeCount, cursorId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}

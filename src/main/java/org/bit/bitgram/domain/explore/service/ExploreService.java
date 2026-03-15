package org.bit.bitgram.domain.explore.service;

import lombok.RequiredArgsConstructor;
import org.bit.bitgram.domain.explore.enums.ActivityWeight;
import org.bit.bitgram.domain.hashtag.entity.PostHashtag;
import org.bit.bitgram.domain.hashtag.repository.PostHashtagRepository;
import org.bit.bitgram.domain.post.dto.PostResponse;
import org.bit.bitgram.domain.post.entity.Post;
import org.bit.bitgram.domain.post.entity.PostLike;
import org.bit.bitgram.domain.post.repository.PostLikeRepository;
import org.bit.bitgram.domain.post.repository.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExploreService {

    private static final int HASH_LIMIT = 5;
    private static final int POST_LIMIT = 20;

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostHashtagRepository postHashtagRepository;

    /**
     * 탐색 피드 제공
     *
     * @param cursorId 최신순 페이징을 위한 게시글 ID 커서 (첫 요청 시 NULL)
     * @param cursorLikeCount 인기순 페이징을 위한 좋아요 개수 커서 (첫 요청 시 NULL)
     */
    public List<PostResponse> getExploreFeed(Long userId, Long cursorId, Long cursorLikeCount) {
        // 1. 사용자의 관심사 추출 (가중치 적용)
        Map<String, Integer> interestScores = extractUserInterests(userId);

        // 2. [콜드 스타트] 관심사 데이터가 전혀 없는 신규/눈팅 사용자인 경우
        if (interestScores.isEmpty()) {
            List<Post> fallbackPosts = postRepository.findExploreFallback(
                    userId, cursorLikeCount, cursorId, PageRequest.of(0, POST_LIMIT));
            return convertToDto(fallbackPosts);
        }

        // 3. 상위 해시태그 5개만 추출하기
        List<String> topHashtags = getTopHashtagNames(interestScores);

        // 4. 추출한 해시태그로 취향 후보군 조회 (내 글 제외 + 커서 페이징 적용)
        List<Post> candidates = postHashtagRepository
                // 쿼리 조건문에 의해 잘려진 데이터 중 0번째부터 20개 얻기
                .findExploreCandidates(userId, topHashtags, cursorId, PageRequest.of(0, POST_LIMIT));

        // 5. [백필링] 추천된 게시물이 요청한 개수보다 부족한 경우
        if (candidates.size() < POST_LIMIT) {
            int shortage = POST_LIMIT - candidates.size();

            // 중복 방지를 위해 이미 추천 목록에 들어간 게시글 ID 목록 추출
            List<Long> excludePostIds = candidates.stream().map(Post::getId).toList();

            List<Post> backfillPosts;
            // 제외할 ID가 없는 경우 SQL 문법 에러 발생
            if (excludePostIds.isEmpty()) {
                backfillPosts = postRepository.findExploreFallback(
                        userId, cursorLikeCount, cursorId, PageRequest.of(0, POST_LIMIT));
            } else {
                backfillPosts = postRepository.findExploreBackfill(
                        userId, excludePostIds, cursorLikeCount, cursorId, PageRequest.of(0, shortage));// 부족한 개수만큼 추출
            }

            candidates.addAll(backfillPosts);
        }

        // 6. 최종 리스트를 DTO로 변환하여 반환
        return convertToDto(candidates);
    }

    /**
     * Helper Methods
     */

    // 사용자의 관심사별 누적 점수(활동별 가중치 부여) 계산
    private Map<String, Integer> extractUserInterests(Long userId) {
        Map<String, Integer> finalScores = new HashMap<>();

        // TODO: 1. 검색 기록 기반 점수 합산 (가중치 1점)

        // 2. 좋아요 기록 기반 점수 합산 (가중치 2점)
        List<Long> likedPostIds = getLikedPostIds(userId);
        accumulateScores(likedPostIds, finalScores, ActivityWeight.LIKE.getScore());

        // TODO: 3. 댓글 기록 기반 점수 합산 (가중치 3점)

        // TODO: 4. 북마크 기록 기반 점수 합산 (가중치 4점)

        // TODO: 5. 공유 기록 기반 점수 합산 (가중치 5점)

        return finalScores;
    }

    // 사용자가 누른 좋아요 기록에서 게시글 ID만 추출하기
    private List<Long> getLikedPostIds(Long userId) {
        return postLikeRepository
                .findPostLikesByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(like -> like.getPost().getId())
                .toList();
    }

    /**
     * TODO: getBookmarkedPostIds, getCommentedPostIds, ... 추가
     */

    // [공통 처리] 특정 활동이 일어난 게시글의 해시태그를 조회하여 점수 누적
    private void accumulateScores(List<Long> postIds, Map<String, Integer> scores, int weight) {
        // 1. 게시글 ID 리스트가 없으면 종료
        if (postIds.isEmpty()) return;

        // 2. 게시글 ID로 해시태그 정보를 한 번에 조회
        List<PostHashtag> postHashtags = postHashtagRepository.findByPostIdIn(postIds);

        // 3. 해시태그별로 점수 누적하기
        for (PostHashtag ph : postHashtags) {
            String tagName = ph.getHashtag().getName();
            // 해시태그가 있으면 기존 점수 + 가중치, 없으면 가중치만)
            scores.put(tagName, scores.getOrDefault(tagName, 0) + weight);
        }
    }

    private List<String> getTopHashtagNames(Map<String, Integer> interestCounts) {
        // Value(빈도수) 기준으로 내림차순 정렬 후 Key(이름)만 추출
        return interestCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(HASH_LIMIT)
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<PostResponse> convertToDto(List<Post> posts) {
        return posts.stream()
                .map(PostResponse::from)
                .toList();
    }

}

package org.bit.bitgram.domain.explore.service;

import lombok.RequiredArgsConstructor;
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

    public List<PostResponse> getExploreFeed(Long userId) {
        // 1. 사용자의 관심사 추출 (해시태그 통계)
        Map<String, Integer> interestScores = extractUserInterests(userId);

        // 2. [콜드 스타트] 관심사 데이터가 전혀 없는 신규/눈팅 사용자인 경우
        if (interestScores.isEmpty()) {
            List<Post> fallbackPosts = postRepository.findTop20ByUserIdNotOrderByLikeCountDesc(userId);
            return convertToDto(fallbackPosts);
        }

        // 3. 상위 해시태그 5개만 추출하기
        List<String> topHashtags = getTopHashtagNames(interestScores);

        // 4. 추출한 해시태그로 취향 후보군 조회 (내 글 제외)
        List<Post> candidates = postHashtagRepository.findExploreCandidates(userId, topHashtags);

        // 5. [백필링] 추천된 게시물이 요청한 개수보다 부족한 경우
        if (candidates.size() < POST_LIMIT) {
            int shortage = POST_LIMIT - candidates.size();

            // 중복 방지를 위해 이미 추천 목록에 들어간 게시글 ID 목록 추출
            List<Long> excludePostIds = candidates.stream().map(Post::getId).toList();

            List<Post> backfillPosts;
            // 제외할 ID가 없는 경우에 findByUserIdNotAndIdNotInOrderByLikeCountDesc를 사용하면 SQL 문법 에러 발생
            if (excludePostIds.isEmpty()) {
                backfillPosts = postRepository.findTop20ByUserIdNotOrderByLikeCountDesc(userId)
                        .stream().limit(shortage).toList();
            } else {
                backfillPosts = postRepository.findByUserIdNotAndIdNotInOrderByLikeCountDesc(
                        userId, excludePostIds, PageRequest.of(0, shortage));// 부족한 개수만큼 추출
            }

            candidates.addAll(backfillPosts);
        } else {
            // 게시물이 너무 많으면 limit 개수만큼 자르기
            candidates = candidates.subList(0, POST_LIMIT);
        }

        // 6. 최종 리스트를 DTO로 변환하여 반환
        return convertToDto(candidates);
    }

    private Map<String, Integer> extractUserInterests(Long userId) {
        /**
         * 좋아요 기록 기반
         * TODO: 북마크, 댓글, 공유 기록도 합산 예정 - 메소드 분리 필요!
         */
        // 1. 사용자가 누른 좋아요 기록 가져오기
        List<PostLike> likes = postLikeRepository.findPostLikesByUserIdOrderByCreatedAtDesc(userId);

        // 2. 좋아요 기록에서 게시글 ID만 추출하기
        List<Long> postIds = likes.stream().map(like -> like.getPost().getId()).toList();

        // 3. 만약 좋아요 누른 글이 하나도 없다면 비어있는 맵 반환
        if (postIds.isEmpty()) {
            return new HashMap<>();
        }

        // 4. 추출한 ID 목록으로 해시태그 정보 가져오기
        List<PostHashtag> postHashtags = postHashtagRepository.findByPostIdIn(postIds);

        // 5. HashMap(해시태그, 빈도수)을 이용해 통계 내기
        Map<String, Integer> interestScores = new HashMap<>();
        for (PostHashtag ph : postHashtags) {
            String tagName = ph.getHashtag().getName();
            // 해시태그가 있으면 +1, 없으면 1(default)
            interestScores.put(tagName, interestScores.getOrDefault(tagName, 0) + 1);
        }

        return interestScores;
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

package com.tricode.READLY.domain.chat.service;

import com.tricode.READLY.domain.book.entity.BookClub;
import com.tricode.READLY.domain.book.repository.BookClubRepository;
import com.tricode.READLY.domain.book.repository.MemberBookClubRepository;
import com.tricode.READLY.domain.chat.dto.ChatDto;
import com.tricode.READLY.domain.chat.entity.ChatMessage;
import com.tricode.READLY.domain.chat.repository.ChatMessageRepository;
import com.tricode.READLY.domain.member.entity.Member;
import com.tricode.READLY.domain.member.repository.MemberRepository;
import com.tricode.READLY.global.exception.AiServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    // 채팅에 참여하는 AI 에이전트의 예약된 회원 ID
    public static final Long AI_MEMBER_ID = 999L;

    private final ChatProducer chatProducer;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberBookClubRepository memberBookClubRepository;
    private final BookClubRepository bookClubRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.base-url}")
    private String aiBaseUrl;

    private static final int RECENT_MESSAGE_LIMIT = 50; // AI에게 넘길 최근 대화 개수 제한

    public void sendMessage(Long clubId, Long memberId, String content) {
        validateClubMember(clubId, memberId);

        // Redis에 들어갈 객체 조립 (ID는 UUID로 생성)
        ChatMessage message = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .clubId(clubId)
                .memberId(memberId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        // Kafka로 메시지 발행
        chatProducer.sendMessage(message);
    }

    /**
     * 채팅방 재입장 시 보여줄 지난 대화 조회.
     * STOMP 구독은 구독 이후의 메시지만 전달하므로, 과거 내역은 이 API로 따로 받아야 한다.
     * Redis TTL이 7일이라 남아 있는 것이 곧 최근 7일치다.
     */
    public List<ChatDto.HistoryItem> getChatHistory(Long clubId, Long memberId) {
        validateClubMember(clubId, memberId);

        List<ChatMessage> messages = getSortedMessages(clubId);
        Map<Long, String> memberNames = resolveMemberNames(messages);

        return messages.stream()
                .map(message -> new ChatDto.HistoryItem(
                        message.getId(),
                        message.getMemberId(),
                        resolveSpeaker(message.getMemberId(), memberNames),
                        message.getContent(),
                        message.getCreatedAt()))
                .toList();
    }

    /**
     * 해당 북클럽의 최근 대화(Redis)를 모아 AI 서버에 개입(assist)을 요청
     */
    public void requestMeetingAssist(Long clubId, Long memberId) {
        validateClubMember(clubId, memberId);

        List<ChatMessage> messages = getRecentMessages(clubId);
        List<ChatDto.ChatLog> recentLogs = messages.stream()
                .map(message -> new ChatDto.ChatLog(message.getMemberId(), message.getContent(), message.getCreatedAt()))
                .toList();

        ChatDto.MeetingAssistRequest requestBody = new ChatDto.MeetingAssistRequest(clubId, recentLogs);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChatDto.MeetingAssistRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        // 사용자가 직접 요청한 동기 경로다. 실패를 삼키고 200을 주면
        // 프론트가 성공한 것으로 표시하므로, 예외로 올려 503으로 응답한다.
        try {
            restTemplate.postForEntity(aiBaseUrl + "/api/meeting/assist", requestEntity, String.class);
            log.info("AI 에이전트에게 대화 개입 요청 성공 (clubId: {}, messageCount: {})", clubId, recentLogs.size());
        } catch (RestClientException e) {
            throw new AiServerException("대화 개입 요청 실패 (clubId: " + clubId + ")", e);
        }
    }

    /**
     * 모임장 전용: AI 진행자 개입 버튼.
     * 최근 대화(Redis)와 책 제목을 AI 서버에 보내고, 받은 응답을 AI 이름으로 채팅방에 바로 발행한다.
     */
    public void requestAiAssist(Long clubId, Long memberId, String mode) {
        BookClub bookClub = bookClubRepository.findByIdWithBook(clubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 독서모임입니다."));
        validateClubHost(bookClub, memberId);

        String bookTitle = bookClub.getBook() != null ? bookClub.getBook().getName() : null;

        List<ChatMessage> messages = getRecentMessages(clubId);
        Map<Long, String> memberNames = resolveMemberNames(messages);

        List<ChatDto.AiChatHistoryItem> chatHistory = messages.stream()
                .map(message -> new ChatDto.AiChatHistoryItem(
                        resolveSpeaker(message.getMemberId(), memberNames),
                        message.getContent()))
                .toList();

        ChatDto.MeetingAssistApiRequest requestBody = new ChatDto.MeetingAssistApiRequest(bookTitle, chatHistory, mode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChatDto.MeetingAssistApiRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        // requestMeetingAssist와 동일하게, 실패는 조용히 넘기지 않고 503으로 알린다
        ChatDto.MeetingAssistApiResponse response;
        try {
            response = restTemplate.postForObject(
                    aiBaseUrl + "/api/meeting/assist", requestEntity, ChatDto.MeetingAssistApiResponse.class);
        } catch (RestClientException e) {
            throw new AiServerException("AI 진행자 개입 요청 실패 (clubId: " + clubId + ")", e);
        }

        // 호출은 됐지만 쓸 내용이 없는 경우도 사용자 입장에서는 실패다
        if (response == null || response.result() == null) {
            throw new AiServerException("AI 진행자 개입 응답이 비어 있습니다 (clubId: " + clubId + ")");
        }

        // 기존 채팅 전송(sendMessage)과 동일한 방식으로 AI 응답을 채팅방에 발행
        sendMessage(clubId, AI_MEMBER_ID, response.result());
    }

    // createdAt이 비어 있는 과거 데이터가 섞여 있어도 정렬이 깨지지 않도록 nullsLast 사용
    private List<ChatMessage> getSortedMessages(Long clubId) {
        return chatMessageRepository.findByClubId(clubId).stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private List<ChatMessage> getRecentMessages(Long clubId) {
        List<ChatMessage> messages = getSortedMessages(clubId);

        return messages.stream()
                .skip(Math.max(0, messages.size() - RECENT_MESSAGE_LIMIT))
                .toList();
    }

    // 메시지에 등장하는 회원들의 표시 이름을 한 번에 조회한다 (닉네임이 없으면 로그인 ID)
    private Map<Long, String> resolveMemberNames(List<ChatMessage> messages) {
        return memberRepository.findAllById(
                        messages.stream().map(ChatMessage::getMemberId).distinct().toList()).stream()
                .collect(Collectors.toMap(Member::getId,
                        m -> m.getNickname() != null ? m.getNickname() : m.getLoginId()));
    }

    // AI는 회원 테이블에 없으므로 고정 이름을 쓴다. 탈퇴 등으로 조회되지 않는 회원도 빈 값으로 두지 않는다.
    private String resolveSpeaker(Long memberId, Map<Long, String> memberNames) {
        if (AI_MEMBER_ID.equals(memberId)) {
            return "AI";
        }
        return memberNames.getOrDefault(memberId, "알 수 없는 사용자");
    }

    // 가입하지 않은 북클럽의 채팅방은 이용할 수 없다 (AI 에이전트는 예외)
    private void validateClubMember(Long clubId, Long memberId) {
        if (AI_MEMBER_ID.equals(memberId)) {
            return;
        }
        if (!memberBookClubRepository.existsByMemberIdAndBookClubId(memberId, clubId)) {
            throw new IllegalStateException("가입하지 않은 독서모임입니다.");
        }
    }

    // AI 진행자 개입은 방장만 요청할 수 있다. 방장은 book_club.host_id에 명시돼 있다.
    // host는 LAZY 프록시지만 getId()는 추가 쿼리 없이 읽힌다.
    private void validateClubHost(BookClub bookClub, Long memberId) {
        if (bookClub.getHost() == null) {
            // host_id 백필(db/2026-08-17-add-bookclub-host.sql) 이전에 만들어진 모임
            throw new IllegalStateException("방장이 지정되지 않은 독서모임입니다.");
        }
        if (!bookClub.getHost().getId().equals(memberId)) {
            throw new IllegalStateException("방장만 AI 진행자를 호출할 수 있습니다.");
        }
    }
}

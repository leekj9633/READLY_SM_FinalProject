package com.tricode.READLY.domain.book.service;

import com.tricode.READLY.domain.book.dto.BookNoteDto;
import com.tricode.READLY.domain.book.entity.AINote;
import com.tricode.READLY.domain.book.entity.Book;
import com.tricode.READLY.domain.book.entity.BookNote;
import com.tricode.READLY.domain.book.repository.AINoteRepository;
import com.tricode.READLY.domain.book.repository.BookRepository;
import com.tricode.READLY.domain.book.repository.BookNoteRepository;
import com.tricode.READLY.domain.member.entity.Member;
import com.tricode.READLY.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookNoteService {

    private final BookNoteRepository booknoteRepository;
    private final AINoteRepository aiNoteRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.base-url}")
    private String aiBaseUrl;

    /**
     * 각 책에다 가볍게 독서록 남기기 (카메라 텍스트 인식 혹은 직접 입력)
     *      프론트엔드에서 OCR 처리된 텍스트를 phrase 인자로 넘겨준다고 가정
     */
    @Transactional
    public Long createBookNote(Long bookId, Long memberId, String phrase, String feeling) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        BookNote bookNote = BookNote.builder()
                .book(book)
                .member(member)
                .phrase(phrase)
                .feeling(feeling)
                .build();

        booknoteRepository.save(bookNote);
        return bookNote.getId();
    }

    /**
     * 기존 독서록들을 기반으로 AI에게 하나의 통합 독서록(AINote) 써달라고 하기
     *      책+회원당 AINote는 하나이므로, 이미 있으면 내용을 갱신한다.
     */
    @Transactional
    public Long generateAiBookNote(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 사용자가 해당 책에 대해 쓴 기존 독서록 목록을 가져오기
        List<BookNote> existingNotes = booknoteRepository.findAllByBookIdAndMemberId(bookId, memberId);
        if (existingNotes.isEmpty()) {
            throw new IllegalStateException("AI 독서록을 만들려면 독서록이 최소 1개 필요합니다.");
        }

        // 구절과 느낀 점을 AI 프롬프트용으로 하나의 문자열로 취합
        String aggregatedContent = existingNotes.stream()
                .map(note -> "구절: " + note.getPhrase() + " / 느낀점: " + note.getFeeling())
                .collect(Collectors.joining("\n"));

        // 외부 AI API 호출 (가정)
        // String aiGeneratedContent = aiClientService.generateSummary(aggregatedContent);
        String aiGeneratedContent = "이 부분에 AI API 응답값이 들어갑니다.";

        AINote aiNote = aiNoteRepository.findByBookIdAndMemberId(bookId, memberId)
                .orElseGet(() -> aiNoteRepository.save(AINote.builder()
                        .book(book)
                        .member(member)
                        .build()));

        aiNote.applyAiContent(aiGeneratedContent); // 기존 AINote면 더티 체킹으로 갱신
        aiNote.applyTags(analyzeTags(aiGeneratedContent));
        return aiNote.getId();
    }

    // 완성된 독서록 내용을 AI 서버에 보내 성향 태그를 분석받기
    private String analyzeTags(String review) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookNoteDto.TagAnalyzeRequest> requestEntity =
                new HttpEntity<>(new BookNoteDto.TagAnalyzeRequest(review), headers);

        try {
            BookNoteDto.TagAnalyzeResponse response = restTemplate.postForObject(
                    aiBaseUrl + "/api/preference/analyze", requestEntity, BookNoteDto.TagAnalyzeResponse.class);
            return response != null ? String.join(",", response.tags()) : null;
        } catch (Exception e) {
            log.error("AI 성향 태그 분석 요청 실패: ", e);
            return null;
        }
    }

    /**
     * AI가 쓴 독서록 수정하기
     */
    @Transactional
    public void updateAiBookNote(Long aiNoteId, String newAiContent) {
        AINote aiNote = aiNoteRepository.findById(aiNoteId)
                .orElseThrow(() -> new IllegalArgumentException("AI 독서록을 찾을 수 없습니다."));

        aiNote.editContent(newAiContent); // 더티 체킹으로 반영
    }
}

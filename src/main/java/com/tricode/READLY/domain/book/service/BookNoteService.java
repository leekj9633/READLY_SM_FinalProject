package com.tricode.READLY.domain.book.service;

import com.tricode.READLY.domain.book.entity.Book;
import com.tricode.READLY.domain.book.entity.BookNote;
import com.tricode.READLY.domain.book.repository.BookRepository;
import com.tricode.READLY.domain.book.repository.BookNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookNoteService {

    private final BookNoteRepository booknoteRepository;
    private final BookRepository bookRepository;
    // private final AiClientService aiClientService; // (외부 AI API 호출용 클래스 가정)

    /**
     * 각 책에다 가볍게 독서록 남기기 (카메라 텍스트 인식 혹은 직접 입력)
     *      프론트엔드에서 OCR 처리된 텍스트를 phrase 인자로 넘겨준다고 가정
     */
    @Transactional
    public Long createBookNote(Long bookId, String phrase, String feeling) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        BookNote bookNote = BookNote.builder()
                .name(book) // 엔티티 필드명 'name'에 Book 세팅
                .phrase(phrase)
                .feeling(feeling)
                .isAiGenerated(false)
                .build();

        booknoteRepository.save(bookNote);
        return bookNote.getId();
    }

    /**
     * 기존 독서록들을 기반으로 AI에게 하나의 독서록 써달라고 하기
     */
    @Transactional
    public Long generateAiBookNote(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        // 사용자가 해당 책에 대해 쓴 기존 독서록 목록을 가져오기
        List<BookNote> existingNotes = booknoteRepository.findAllByBookIdAndMemberId(bookId, memberId);

        // 구절과 느낀 점을 AI 프롬프트용으로 하나의 문자열로 취합
        String aggregatedContent = existingNotes.stream()
                .map(note -> "구절: " + note.getPhrase() + " / 느낀점: " + note.getFeeling())
                .collect(Collectors.joining("\n"));

        // 외부 AI API 호출 (가정)
        // String aiGeneratedContent = aiClientService.generateSummary(aggregatedContent);
        String aiGeneratedContent = "이 부분에 AI API 응답값이 들어갑니다.";

        BookNote aiNote = BookNote.builder()
                .name(book)
                .isAiGenerated(true)
                .aiContent(aiGeneratedContent)
                .build();

        booknoteRepository.save(aiNote);
        return aiNote.getId();
    }

    /**
     * AI가 쓴 독서록 수정하기
     */
    @Transactional
    public void updateAiBookNote(Long noteId, String newAiContent) {
        BookNote bookNote = booknoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("독서록을 찾을 수 없습니다."));

        if (!bookNote.isAiGenerated()) {
            throw new IllegalStateException("AI가 생성한 독서록만 수정할 수 있습니다.");
        }

        // 엔티티에 updateAiContent(String content) 메서드가 있다고 가정 (더티 체킹)
        // bookNote.updateAiContent(newAiContent);
    }
}

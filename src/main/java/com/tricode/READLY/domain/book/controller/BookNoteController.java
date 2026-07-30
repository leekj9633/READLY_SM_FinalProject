package com.tricode.READLY.domain.book.controller;

import com.tricode.READLY.domain.book.service.BookNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class BookNoteController {

    private final BookNoteService bookNoteService;

    /**
     * 기능: 책을 읽을 때마다 가볍게 독서록 여러 개 남기기
     * 프론트엔드에서 OCR로 추출된 텍스트나 직접 입력한 텍스트를 DTO로 받기
     */
    @PostMapping("/books/{bookId}")   // 수정 필요
    public ResponseEntity<Long> createBookNote(
            @PathVariable Long bookId,
            @RequestBody CreateNoteRequest request) {

        Long noteId = bookNoteService.createBookNote(bookId, request.phrase(), request.feeling());
        return ResponseEntity.ok(noteId);
    }

    /**
     * 기능: 하나의 책에 대한 여러 독서록들을 기반으로 AI에게 독서록 써달라고 요청
     */
    @PostMapping("/books/{bookId}/ai-generate")   // 수정 필요
    public ResponseEntity<Long> generateAiBookNote(
            @PathVariable Long bookId,
            @RequestParam Long memberId) { // 실제는 세션/토큰에서 가져옵니다

        Long aiNoteId = bookNoteService.generateAiBookNote(bookId, memberId);
        return ResponseEntity.ok(aiNoteId);
    }

    /**
     * 기능: AI가 쓴 독서록 수정하기
     */
    @PatchMapping("/{noteId}/ai-content")   // 수정 필요
    public ResponseEntity<Void> updateAiBookNote(
            @PathVariable Long noteId,
            @RequestBody UpdateAiNoteRequest request) {

        bookNoteService.updateAiBookNote(noteId, request.newAiContent());
        return ResponseEntity.ok().build();
    }

    // --- DTO ---
    public record CreateNoteRequest(String phrase, String feeling) {}
    public record UpdateAiNoteRequest(String newAiContent) {}
}

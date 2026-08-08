package com.tricode.READLY.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 서비스 계층에서 던지는 예외를 HTTP 상태 코드로 변환한다.
// (이 핸들러가 없으면 잘못된 요청도 전부 500 Internal Server Error로 나간다)
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 잘못된 입력값 (없는 회원/책, 중복된 이메일 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("잘못된 요청: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    // 현재 상태에서 허용되지 않는 요청 (이미 팔로우 중, 독서록 없이 AI 생성 요청 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        log.warn("처리할 수 없는 요청: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }

    // 그 외 예상하지 못한 예외는 내부 메시지를 노출하지 않는다
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("예상하지 못한 오류: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버 내부 오류가 발생했습니다."));
    }

    public record ErrorResponse(String message) {}
}

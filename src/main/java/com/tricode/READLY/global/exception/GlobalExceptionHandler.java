package com.tricode.READLY.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.util.stream.Collectors;

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

    // @Valid 검증 실패 (필수값 누락 등). 이 핸들러가 없으면 마지막 Exception 핸들러로 떨어져 500이 된다.
    // 어떤 필드가 왜 틀렸는지 그대로 알려줘야 프론트가 사용자에게 띄울 수 있다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("입력값 검증 실패: {}", message);
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    // 현재 상태에서 허용되지 않는 요청 (이미 팔로우 중, 독서록 없이 AI 생성 요청 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        log.warn("처리할 수 없는 요청: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }

    // 로그인은 했지만 남의 리소스를 건드리려 할 때
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e) {
        log.warn("권한 없는 요청: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
    }

    // AI 서버를 호출하지 못했을 때 (연결 거부, 타임아웃, 빈 응답 등)
    // 우리 서버 문제가 아니라 외부 의존 서비스 장애이므로 503으로 구분해서 알린다
    @ExceptionHandler(AiServerException.class)
    public ResponseEntity<ErrorResponse> handleAiServer(AiServerException e) {
        log.error("AI 서버 호출 실패: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("AI 서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요."));
    }

    // 외부 API 호출 자체가 실패한 경우 (알라딘 연결 거부·타임아웃 등).
    // AI 서버 호출은 AiServerException으로 감싸 던지므로 더 구체적인 위 핸들러가 먼저 잡는다.
    // 이 핸들러가 없으면 우리 서버 잘못이 아닌데도 500으로 나간다.
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleRestClient(RestClientException e) {
        log.error("외부 API 호출 실패: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("외부 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요."));
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

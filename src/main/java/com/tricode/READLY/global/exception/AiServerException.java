package com.tricode.READLY.global.exception;

// AI 서버(외부 에이전트) 호출이 실패했을 때 던진다.
// 우리 서버의 버그가 아니라 업스트림 장애이므로 500이 아니라 503으로 내보낸다.
public class AiServerException extends RuntimeException {

    public AiServerException(String message) {
        super(message);
    }

    public AiServerException(String message, Throwable cause) {
        super(message, cause);
    }
}

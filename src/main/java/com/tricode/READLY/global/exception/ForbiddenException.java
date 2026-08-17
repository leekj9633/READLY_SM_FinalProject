package com.tricode.READLY.global.exception;

// 로그인은 했지만 그 리소스를 다룰 권한이 없을 때 던진다 (남의 독서록 수정 등).
// 인증 자체가 없는 경우(401)나 상태 충돌(409)과 구분해 403으로 내보낸다.
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}

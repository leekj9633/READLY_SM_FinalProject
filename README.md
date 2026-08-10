# readly-ai-server

READLY 프로젝트의 AI 서버입니다. 프론트/스프링부트 백엔드와는 별도 저장소로 관리합니다.

- 프론트: readly-frontend (React)
- 백엔드: readly-backend (Spring Boot)
- **AI 서버(이 저장소): readly-ai-server (FastAPI)**

스프링부트 백엔드가 이 서버의 API를 호출해서 GPT 응답을 받아가는 구조입니다.

## 1. 실행 준비

```bash
python -m venv venv
source venv/bin/activate      # Windows는 venv\Scripts\activate
pip install -r requirements.txt
cp .env.example .env          # .env 안의 OPENAI_API_KEY를 실제 키로 교체
```

## 2. 서버 실행

```bash
uvicorn app.main:app --reload --port 8001
```

- 헬스체크: http://localhost:8001/health → `{"status": "ok"}`
- API 문서(Swagger): http://localhost:8001/docs

## 3. 엔드포인트

| 기능 | Method | URL |
|---|---|---|
| 독후감 생성 | POST | /api/review/generate |
| 독서 성향 분석 | POST | /api/preference/analyze |
| AI 독서모임 진행자 | POST | /api/meeting/assist |

요청/응답 형식은 `/docs`에서 바로 확인 및 테스트 가능합니다.

## 4. 프로젝트 구조

```
app/
├── main.py              # FastAPI 진입점
├── core/config.py       # 환경설정 (.env 로드)
├── api/                 # 라우터 (엔드포인트)
├── services/            # GPT 호출 로직
├── schemas/             # 요청/응답 모델
└── db/                  # (예정) pgvector 연동, 유사 사용자 검색
```

## 5. 다음 단계

- [ ] pgvector로 유사 성향 사용자 매칭 (`app/db/`)
- [ ] 스프링부트 쪽과 인증/토큰 검증 방식 협의 후 적용

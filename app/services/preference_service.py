"""
2. 독서 성향 분석
사용자가 쓴 독후감들을 모아 GPT로 취향 태그를 뽑아냅니다.
"""

import json

from app.schemas.preference import PreferenceAnalyzeRequest
from app.services.openai_client import ask_gpt

SYSTEM_PROMPT = (
    "당신은 독서 취향 분석가입니다. 사용자가 쓴 여러 독후감을 읽고 "
    "이 사람의 독서 성향을 태그 3~6개와 한 줄 요약으로 정리합니다. "
    "반드시 아래 JSON 형식으로만 답하세요.\n"
    '{"tags": ["태그1", "태그2"], "summary": "한 줄 요약"}'
)


async def analyze_preference(req: PreferenceAnalyzeRequest) -> dict:
    reviews_text = "\n\n".join(
        f"[독후감 {i+1}]\n{r}" for i, r in enumerate(req.reviews)
    )

    raw = await ask_gpt(SYSTEM_PROMPT, reviews_text, temperature=0.3)

    try:
        data = json.loads(raw)
    except (json.JSONDecodeError, TypeError):
        # GPT가 JSON 형식을 안 지켰을 때를 대비한 안전장치
        data = {"tags": [], "summary": raw or ""}

    return data

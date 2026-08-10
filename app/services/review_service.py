"""
1. 독후감 생성
사용자가 책을 읽으면서 남긴 (구절 phrase + 느낀점 feeling) 노트를
여러 개 받아서, GPT가 이를 종합해 하나의 독후감으로 작성합니다.
(백엔드 BookNote 구조: 노트 여러 건 → /ai-generate 요청 시 한 번에 전달)

사용자가 쓰지 않은 줄거리나 감상을 새로 지어내지 않고, 주어진
구절/느낀점 안에서만 내용을 구성합니다.
"""

from app.schemas.review import ReviewGenerateRequest
from app.services.openai_client import ask_gpt

SYSTEM_PROMPT = (
    "당신은 독서 애플리케이션의 독후감 작성 도우미입니다. "
    "사용자가 책을 읽으며 남긴 '구절(phrase)'과 그에 대한 '느낀점(feeling)' "
    "쌍을 여러 개 받아서, 이를 자연스럽게 종합한 한국어 독후감을 작성합니다. "
    "각 느낀점의 뉘앙스와 사용자의 원래 어투를 최대한 살리되, 문장은 "
    "매끄럽게 다듬고 문단으로 자연스럽게 이어줍니다. 사용자가 언급하지 "
    "않은 줄거리나 감상을 새로 지어내지 않습니다."
)


async def generate_review(req: ReviewGenerateRequest) -> str:
    notes_text = "\n\n".join(
        f"- 구절: {n.phrase}\n  느낀점: {n.feeling}" for n in req.notes
    )

    user_prompt = f"""
책 제목: {req.book_title}
저자: {req.author or "미상"}
원하는 문체: {req.tone}

[사용자가 남긴 구절 + 느낀점]
{notes_text}

위 노트들을 바탕으로 자연스럽게 이어지는 독후감을 작성해줘.
""".strip()

    return await ask_gpt(SYSTEM_PROMPT, user_prompt)
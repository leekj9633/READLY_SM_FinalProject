"""
OpenAI(GPT) 호출을 담당하는 공통 클라이언트.
다른 서비스(review_service, preference_service, meeting_service)들은
이 모듈의 ask_gpt() 함수만 가져다 쓰면 됩니다.
"""

from openai import AsyncOpenAI

from app.core.config import settings

client = AsyncOpenAI(api_key=settings.OPENAI_API_KEY)


async def ask_gpt(
    system_prompt: str,
    user_prompt: str,
    model: str | None = None,
    temperature: float = 0.7,
) -> str:
    """
    GPT에게 system/user 프롬프트를 보내고 답변 텍스트만 돌려주는 헬퍼 함수.
    """
    response = await client.chat.completions.create(
        model=model or settings.OPENAI_MODEL,
        temperature=temperature,
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt},
        ],
    )
    return response.choices[0].message.content

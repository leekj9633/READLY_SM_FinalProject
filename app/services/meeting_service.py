"""
3. AI 독서모임 진행자
모임 채팅 내용을 받아서 토론 질문을 제안하거나 대화를 요약해줍니다.
"""

from app.schemas.meeting import MeetingAssistRequest

QUESTION_PROMPT = (
    "당신은 독서모임 진행자입니다. 지금까지의 대화 흐름을 보고, "
    "참여자들이 더 깊게 이야기할 수 있는 토론 질문을 2~3개 제안하세요. "
    "질문은 번호 목록으로 간결하게 작성하세요."
)

SUMMARY_PROMPT = (
    "당신은 독서모임 진행자입니다. 지금까지의 대화 내용을 3~5줄로 "
    "요약하세요. 누가 어떤 의견을 냈는지 핵심만 정리하세요."
)

from app.services.openai_client import ask_gpt


async def assist_meeting(req: MeetingAssistRequest) -> str:
    chat_text = "\n".join(f"{m.speaker}: {m.text}" for m in req.chat_history)
    user_prompt = f"책 제목: {req.book_title}\n\n[대화 내용]\n{chat_text}"

    system_prompt = QUESTION_PROMPT if req.mode == "question" else SUMMARY_PROMPT

    return await ask_gpt(system_prompt, user_prompt)

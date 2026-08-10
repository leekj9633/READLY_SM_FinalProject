from pydantic import BaseModel


class ChatMessage(BaseModel):
    speaker: str
    text: str


class MeetingAssistRequest(BaseModel):
    book_title: str
    chat_history: list[ChatMessage]
    mode: str = "question"  # "question"(토론 질문 제안) | "summary"(대화 요약)


class MeetingAssistResponse(BaseModel):
    mode: str
    result: str

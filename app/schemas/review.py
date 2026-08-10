from pydantic import BaseModel, Field


class BookNoteItem(BaseModel):
    """
    백엔드 BookNote 엔티티 1건에 대응.
    phrase = 구절(인상 깊었던 문장), feeling = 느낀점(그 문장에 대한 짧은 감상)
    """
    phrase: str = Field(..., description="인상 깊었던 구절")
    feeling: str = Field(..., description="그 구절에 대한 느낀점")


class ReviewGenerateRequest(BaseModel):
    book_title: str = Field(..., description="책 제목")
    author: str | None = Field(None, description="저자 (선택)")
    notes: list[BookNoteItem] = Field(..., description="사용자가 남긴 구절+느낀점 목록")
    tone: str = Field("담백하고 진솔한", description="원하는 문체/톤")


class ReviewGenerateResponse(BaseModel):
    review: str
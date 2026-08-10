from pydantic import BaseModel


class PreferenceAnalyzeRequest(BaseModel):
    reviews: list[str]  # 이 사용자가 이전에 쓴 독후감들


class PreferenceAnalyzeResponse(BaseModel):
    tags: list[str]        # 예: ["잔잔한 성장서사", "SF보다는 에세이", "짧은 호흡 선호"]
    summary: str            # 성향에 대한 한 줄 요약

"""
AI 서버 진입점.

실행:
    uvicorn app.main:app --reload --port 8001
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.core.config import settings
from app.api import meeting, preference, review

app = FastAPI(title="READLY AI Server")

app.add_middleware(
    CORSMiddleware,
    allow_origins=[settings.BACKEND_ALLOWED_ORIGIN],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(review.router)
app.include_router(preference.router)
app.include_router(meeting.router)


@app.get("/health")
async def health():
    return {"status": "ok"}

from fastapi import APIRouter

from app.schemas.review import ReviewGenerateRequest, ReviewGenerateResponse
from app.services.review_service import generate_review

router = APIRouter(prefix="/api/review", tags=["review"])


@router.post("/generate", response_model=ReviewGenerateResponse)
async def generate_review_endpoint(req: ReviewGenerateRequest):
    review_text = await generate_review(req)
    return ReviewGenerateResponse(review=review_text)
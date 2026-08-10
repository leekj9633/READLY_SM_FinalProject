from fastapi import APIRouter

from app.schemas.preference import PreferenceAnalyzeRequest, PreferenceAnalyzeResponse
from app.services.preference_service import analyze_preference

router = APIRouter(prefix="/api/preference", tags=["preference"])


@router.post("/analyze", response_model=PreferenceAnalyzeResponse)
async def analyze_preference_endpoint(req: PreferenceAnalyzeRequest):
    data = await analyze_preference(req)
    return PreferenceAnalyzeResponse(**data)

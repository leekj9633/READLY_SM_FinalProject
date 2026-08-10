from fastapi import APIRouter

from app.schemas.meeting import MeetingAssistRequest, MeetingAssistResponse
from app.services.meeting_service import assist_meeting

router = APIRouter(prefix="/api/meeting", tags=["meeting"])


@router.post("/assist", response_model=MeetingAssistResponse)
async def assist_meeting_endpoint(req: MeetingAssistRequest):
    result = await assist_meeting(req)
    return MeetingAssistResponse(mode=req.mode, result=result)

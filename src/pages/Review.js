// src/pages/Review.js

import { useLocation } from "react-router-dom";

function Review() {
  const location = useLocation();

  const book =
    location.state || {};

  return (
    <div className="page">
      {/* 상단 */}

      <div className="top-bar">
        <div className="logo">
          READLY
        </div>
      </div>

      {/* 책 */}

      <div className="review-hero">
        <div className="review-cover">
          📘
        </div>

        <div>
          <div className="review-book">
            {book.title}
          </div>

          <div className="review-author">
            AI 독후감 기록
          </div>
        </div>
      </div>

      {/* 리뷰 */}

      <div className="review-card">
        <div className="review-title">
          ✨ 나의 독후감
        </div>

        <div className="review-content">
          {book.review}
        </div>
      </div>

      {/* 감정 카드 */}

      <div className="emotion-card">
        <div className="emotion-title">
          오늘의 감정
        </div>

        <div className="emotion-tags">
          <span>🌿 여운있는</span>
          <span>📖 몰입감</span>
          <span>✨ 감성적</span>
        </div>
      </div>
    </div>
  );
}

export default Review;
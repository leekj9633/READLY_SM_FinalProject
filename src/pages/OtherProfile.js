// src/pages/OtherProfile.js
// 백엔드 연결 시: location.state?.userId 로 userId 받아서
// useEffect 안에서 GET /api/users/:userId 호출 후 setProfile() 하면 됩니다.

import { useState, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const heights = [180, 220, 160, 200, 175, 215, 165, 190, 205];
const colors  = ["#7bc142","#a8d84e","#5aab35","#c5e87a","#68b83e","#b2de5f","#4e9e2f","#d4f09a","#89c94f"];

// ── 임시 더미 데이터 (백엔드 연결 시 삭제) ──────────────────────────
const DUMMY_USERS = {
  민지: {
    name: "민지", img: "/images/민지프로필.png",
    desc: "함께 책 읽고 이야기하는 걸 좋아해요 📖",
    followers: 23, following: 15,
    books: [
      { title: "노르웨이의 숲", review: "상실 이후에도 사람은 계속 살아간다는 점이 가장 인상 깊었다." },
      { title: "데미안",       review: "자기 자신을 찾아가는 과정이 정말 인상 깊었다." },
      { title: "어린왕자",     review: "순수함에 대해 다시 생각하게 만든 책이었다." },
    ],
  },
  현우: {
    name: "현우", img: "/images/현우프로필.png",
    desc: "SF랑 철학 좋아합니다 🚀",
    followers: 31, following: 20,
    books: [
      { title: "1984",   review: "감시 사회의 무서움을 현실적으로 보여줬다." },
      { title: "데미안", review: "성장이란 결국 자신을 마주하는 과정이었다." },
    ],
  },
  서연: {
    name: "서연", img: "/images/서연프로필.png",
    desc: "소설과 에세이 사이 어딘가에 있어요 ✨",
    followers: 18, following: 9,
    books: [
      { title: "채식주의자",   review: "강렬하면서도 오래 기억에 남는 작품이었다." },
      { title: "해변의 카프카", review: "현실과 환상이 섞인 분위기가 좋았다." },
    ],
  },
};
// ───────────────────────────────────────────────────────────────────

function OtherProfile() {
  const navigate = useNavigate();
  const location = useLocation();
  const userName = location.state?.user || "민지";

  // 백엔드 연결 시: 아래 줄 대신 useEffect + fetch 로 교체
  const profile = DUMMY_USERS[userName] || DUMMY_USERS["민지"];

  const [isFollowing, setIsFollowing] = useState(false);
  const [followers,   setFollowers]   = useState(profile.followers);

  const handleFollow = () => {
    setIsFollowing(f => !f);
    setFollowers(n => isFollowing ? n - 1 : n + 1);
    // 백엔드 연결 시: POST /api/users/:userId/follow 호출
  };

  // 드래그 슬라이드 — Profile.js와 동일
  const shelfRef   = useRef(null);
  const isDragging = useRef(false);
  const startX     = useRef(0);
  const scrollLeft = useRef(0);

  const onMouseDown = (e) => {
    isDragging.current = true;
    startX.current = e.pageX - shelfRef.current.offsetLeft;
    scrollLeft.current = shelfRef.current.scrollLeft;
    shelfRef.current.style.cursor = "grabbing";
  };
  const onMouseMove = (e) => {
    if (!isDragging.current) return;
    e.preventDefault();
    const x = e.pageX - shelfRef.current.offsetLeft;
    const walk = (x - startX.current) * 1.2;
    shelfRef.current.scrollLeft = scrollLeft.current - walk;
  };
  const onMouseUp = () => {
    isDragging.current = false;
    if (shelfRef.current) shelfRef.current.style.cursor = "grab";
  };

  return (
    <>
      <style>{`
        /* ── Profile.js 스타일 그대로 ── */
        .profile-page {
          display: flex;
          flex-direction: column;
          height: 100vh;
          padding-bottom: 72px;
          box-sizing: border-box;
          overflow: hidden;
          background: #fff;
        }
        .profile-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 16px 20px 8px;
          flex-shrink: 0;
        }
        .profile-image {
          width: 75px; height: 75px;
          border-radius: 50%;
          overflow: hidden;
          flex-shrink: 0;
          border: 2px solid #eee;
          margin-left: 12px;
        }
        .profile-name {
          font-size: 18px; font-weight: 700;
          display: flex; align-items: center; gap: 6px;
          margin-bottom: 8px;
        }
        .follow-wrap { display: flex; gap: 20px; }
        .follow-box { text-align: center; }
        .follow-num { font-size: 15px; font-weight: 700; }
        .follow-text { font-size: 12px; color: #888; }
        .profile-desc {
          padding: 10px 20px 10px;
          font-size: 13px; color: #555; line-height: 1.6;
          flex-shrink: 0;
          white-space: pre-line;
        }
        .divider {
          height: 1px; background: #eee;
          margin: 0 20px;
          flex-shrink: 0;
        }

        /* 팔로우 버튼 (OtherProfile 전용) */
        .follow-btn-wrap {
          margin-top: 8px;
        }
        .follow-action-btn {
          padding: 6px 22px;
          border-radius: 20px;
          font-size: 13px; font-weight: 700;
          border: none; cursor: pointer;
          transition: all 0.18s;
        }
        .follow-action-btn.off {
          background: linear-gradient(135deg, #7bc142, #5aab35);
          color: #fff;
          box-shadow: 0 3px 10px rgba(90,171,53,0.28);
        }
        .follow-action-btn.on {
          background: #f0f0f0; color: #666;
        }

        /* 책장 영역 */
        .bookshelf-area {
          flex: 1;
          background:
            radial-gradient(ellipse at 80% 0%, rgba(255,220,120,0.22) 0%, transparent 55%),
            radial-gradient(ellipse at 10% 80%, rgba(200,160,80,0.1) 0%, transparent 50%),
            repeating-linear-gradient(90deg, transparent, transparent 39px, rgba(180,140,90,0.07) 39px, rgba(180,140,90,0.07) 40px),
            repeating-linear-gradient(180deg, transparent, transparent 39px, rgba(180,140,90,0.07) 39px, rgba(180,140,90,0.07) 40px),
            #f5ede0;
          position: relative;
          display: flex;
          flex-direction: column;
          justify-content: flex-end;
          overflow: hidden;
          min-height: 0;
        }
        .lamp-wrap {
          position: absolute;
          top: 20px; right: 40px;
          display: flex; flex-direction: column; align-items: center;
        }
        .lamp-rod { width: 2px; height: 28px; background: #b89060; }
        .lamp-head {
          width: 44px; height: 20px;
          background: #c9a055;
          border-radius: 0 0 22px 22px;
          position: relative;
        }
        .lamp-glow {
          position: absolute; bottom: -50px; left: 50%;
          transform: translateX(-50%);
          width: 140px; height: 80px;
          background: radial-gradient(ellipse, rgba(255,210,80,0.28) 0%, transparent 70%);
          pointer-events: none;
        }
        .book-scroll {
          display: flex;
          align-items: flex-end;
          gap: 0px;
          overflow-x: auto;
          overflow-y: visible;
          padding: 0 16px;
          cursor: grab;
          scroll-behavior: smooth;
          -webkit-overflow-scrolling: touch;
          scrollbar-width: none;
          -ms-overflow-style: none;
          padding-top: 60px;
        }
        .book-scroll::-webkit-scrollbar { display: none; }
        .book-item {
          flex-shrink: 0;
          cursor: pointer;
          transition: transform 0.22s cubic-bezier(.34,1.56,.64,1), filter 0.2s ease;
        }
        .book-item:hover {
          transform: translateY(-12px) scale(1.05);
          filter: brightness(1.1) drop-shadow(0 10px 14px rgba(80,60,20,0.25));
          z-index: 10;
        }
        .book-item:active { transform: translateY(-4px) scale(0.97); }
        .book-spine {
          border-radius: 5px 5px 0 0;
          display: flex;
          justify-content: center;
          align-items: center;
          color: rgba(60,50,30,0.82);
          font-size: 11px;
          font-weight: 700;
          writing-mode: vertical-rl;
          text-orientation: mixed;
          overflow: hidden;
          padding: 10px 0;
          box-shadow: inset -3px 0 6px rgba(0,0,0,0.1), inset 2px 0 4px rgba(255,255,255,0.25);
        }
        .shelf-board {
          height: 14px;
          background: linear-gradient(to bottom, #c8974a, #a87840);
          border-radius: 3px;
          box-shadow: 0 4px 10px rgba(120,80,30,0.22);
          flex-shrink: 0;
        }
      `}</style>

      <div className="profile-page">

        {/* 헤더 — ← 뒤로가기 + READLY 가운데 */}
        <div className="profile-header">
          <button
            onClick={() => navigate(-1)}
            style={{ background:"none", border:"none", fontSize:"20px", cursor:"pointer", padding:0, color:"#333" }}
          >←</button>
          <div className="logo">READLY</div>
          <div style={{ width: 24 }} />
        </div>

        {/* 프로필 — Profile.js .profile-top과 동일 구조 */}
        <div className="profile-top">
          <div className="profile-image">
            <img
              src={profile.img}
              alt="프로필"
              style={{ width:"100%", height:"100%", objectFit:"cover" }}
              onError={e => { e.target.style.display = "none"; }}
            />
          </div>
          <div className="profile-info">
            <div className="profile-name">{profile.name}</div>
            <div className="follow-wrap">
              <div className="follow-box">
                <div className="follow-num">{followers}</div>
                <div className="follow-text">팔로워</div>
              </div>
              <div className="follow-box">
                <div className="follow-num">{profile.following}</div>
                <div className="follow-text">팔로잉</div>
              </div>
            </div>
            <div className="follow-btn-wrap">
              <button
                className={`follow-action-btn ${isFollowing ? "on" : "off"}`}
                onClick={handleFollow}
              >
                {isFollowing ? "팔로잉 ✓" : "팔로우"}
              </button>
            </div>
          </div>
        </div>

        {/* 소개 */}
        <div className="profile-desc">{profile.desc}</div>
        <div className="divider" />

        {/* 책장 */}
        <div className="bookshelf-area">
          <div className="lamp-wrap">
            <div className="lamp-rod" />
            <div className="lamp-head">
              <div className="lamp-glow" />
            </div>
          </div>

          <div
            ref={shelfRef}
            className="book-scroll"
            onMouseDown={onMouseDown}
            onMouseMove={onMouseMove}
            onMouseUp={onMouseUp}
            onMouseLeave={onMouseUp}
          >
            {profile.books.map((book, index) => (
              <div
                key={index}
                className="book-item"
                onClick={() => navigate("/review", { state: { title: book.title, review: book.review } })}
              >
                <div
                  className="book-spine"
                  style={{
                    width: "48px",
                    height: `${heights[index % heights.length]}px`,
                    background: colors[index % colors.length],
                  }}
                >
                  {book.title}
                </div>
              </div>
            ))}
          </div>

          <div className="shelf-board" />
        </div>

      </div>
    </>
  );
}

export default OtherProfile;
// src/pages/Profile.js

import { useState, useRef } from "react";
import { FiSettings } from "react-icons/fi";
import { useNavigate } from "react-router-dom";

const initialBooks = [
  { title: "노르웨이의 숲", review: "상실 이후에도 사람은 계속 살아간다는 점이 가장 인상 깊었다." },
  { title: "데미안", review: "자기 자신을 찾아가는 과정이 정말 인상 깊었다." },
  { title: "어린왕자", review: "어른이 된다는 건 무엇일까 다시 생각하게 만든 책이었다." },
  { title: "1984", review: "감시 사회의 무서움을 현실적으로 보여줘서 몰입감이 강했다." },
  { title: "해변의 카프카", review: "현실과 환상이 섞이는 분위기가 매력적이었다." },
  { title: "채식주의자", review: "인물의 심리 변화가 굉장히 강렬했다." },
  { title: "82년생 김지영", review: "평범한 일상 속 차별이 얼마나 깊은지 느꼈다." },
  { title: "파친코", review: "세대를 넘는 이야기가 깊은 여운을 남겼다." },
  { title: "아몬드", review: "감정을 느끼지 못하는 주인공이 오히려 많은 감정을 불러일으켰다." },
];

const heights = [180, 220, 160, 200, 175, 215, 165, 190, 205];
const colors = ["#7bc142", "#a8d84e", "#5aab35", "#c5e87a", "#68b83e", "#b2de5f", "#4e9e2f", "#d4f09a", "#89c94f"];

function Profile() {
  const navigate = useNavigate();
  const currentUser = JSON.parse(localStorage.getItem("currentUser"));

  // 프로필 상태
  const [userName, setUserName] = useState(currentUser?.name || "김수연");
  const [profileDesc, setProfileDesc] = useState("에세이를 좋아하는 책린이 입니다!\ninstagram: xx_123");
  const [profileImg, setProfileImg] = useState("/images/프로필사진.png");

  // 모달 상태
  const [showModal, setShowModal] = useState(false);
  const [editName, setEditName] = useState(userName);
  const [editDesc, setEditDesc] = useState(profileDesc);
  const [editImgPreview, setEditImgPreview] = useState(profileImg);
  const [editImgFile, setEditImgFile] = useState(null);

  const fileInputRef = useRef(null);

  // 드래그 슬라이드
  const shelfRef = useRef(null);
  const isDragging = useRef(false);
  const startX = useRef(0);
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

  // 이미지 선택
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setEditImgFile(file);
    const reader = new FileReader();
    reader.onload = (ev) => setEditImgPreview(ev.target.result);
    reader.readAsDataURL(file);
  };

  // 저장
  const handleSave = () => {
    setUserName(editName);
    setProfileDesc(editDesc);
    setProfileImg(editImgPreview);
    // localStorage 업데이트
    const user = JSON.parse(localStorage.getItem("currentUser")) || {};
    localStorage.setItem("currentUser", JSON.stringify({ ...user, name: editName }));
    setShowModal(false);
  };

  // 모달 열기
  const openModal = () => {
    setEditName(userName);
    setEditDesc(profileDesc);
    setEditImgPreview(profileImg);
    setEditImgFile(null);
    setShowModal(true);
  };

  return (
    <>
      <style>{`
        .profile-page {
          display: flex;
          flex-direction: column;
          height: 100vh;
          padding-bottom: 72px; /* 네비바 높이만큼 확보 */
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
        .pencil-btn {
          background: none; border: none; padding: 0;
          cursor: pointer; font-size: 14px; line-height: 1;
          display: flex; align-items: center;
          transition: transform 0.15s;
        }
        .pencil-btn:hover { transform: rotate(-15deg) scale(1.2); }
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

        /* 조명 */
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

        /* 책 슬라이드 */
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
          /* 위쪽 여유 */
          padding-top: 60px;
        }
        .book-scroll::-webkit-scrollbar { display: none; }

        .book-item {
          flex-shrink: 0;
          cursor: pointer;
          transition: transform 0.22s cubic-bezier(.34,1.56,.64,1),
                      filter 0.2s ease;
        }
        .book-item:hover {
          transform: translateY(-12px) scale(1.05);
          filter: brightness(1.1) drop-shadow(0 10px 14px rgba(80,60,20,0.25));
          z-index: 10;
        }
        .book-item:active {
          transform: translateY(-4px) scale(0.97);
        }
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

        /* 선반 */
        .shelf-board {
          height: 14px;
          background: linear-gradient(to bottom, #c8974a, #a87840);
          border-radius: 3px;
          box-shadow: 0 4px 10px rgba(120,80,30,0.22);
          flex-shrink: 0;
        }

        /* ── 모달 ── */
        .modal-backdrop {
          position: fixed; inset: 0;
          background: rgba(0,0,0,0.45);
          z-index: 200;
          display: flex; align-items: flex-end; justify-content: center;
        }
        .modal-sheet {
          background: #fff;
          width: 100%; max-width: 430px;
          border-radius: 24px 24px 0 0;
          padding: 28px 24px 40px;
          animation: slideUp 0.28s cubic-bezier(.34,1.2,.64,1);
        }
        @keyframes slideUp {
          from { transform: translateY(100%); opacity: 0; }
          to   { transform: translateY(0);    opacity: 1; }
        }
        .modal-title {
          font-size: 17px; font-weight: 800;
          margin-bottom: 24px; text-align: center;
        }
        .modal-avatar-wrap {
          display: flex; justify-content: center;
          margin-bottom: 24px;
        }
        .modal-avatar {
          width: 88px; height: 88px;
          border-radius: 50%; overflow: hidden;
          border: 3px solid #e8e8e8;
          position: relative; cursor: pointer;
        }
        .modal-avatar img {
          width: 100%; height: 100%; object-fit: cover;
        }
        .avatar-overlay {
          position: absolute; inset: 0;
          background: rgba(0,0,0,0.32);
          display: flex; align-items: center; justify-content: center;
          border-radius: 50%;
          font-size: 22px;
          opacity: 0;
          transition: opacity 0.18s;
        }
        .modal-avatar:hover .avatar-overlay { opacity: 1; }
        .modal-label {
          font-size: 12px; color: #999; font-weight: 600;
          margin-bottom: 6px; margin-top: 14px;
          letter-spacing: 0.5px;
        }
        .modal-input {
          width: 100%; box-sizing: border-box;
          border: 1.5px solid #e5e5e5;
          border-radius: 12px;
          padding: 10px 14px;
          font-size: 14px;
          outline: none;
          transition: border-color 0.15s;
          font-family: inherit;
          resize: none;
        }
        .modal-input:focus { border-color: #7bc142; }
        .modal-actions {
          display: flex; gap: 10px;
          margin-top: 24px;
        }
        .btn-cancel {
          flex: 1; padding: 13px 0;
          border: 1.5px solid #e5e5e5;
          border-radius: 14px;
          background: #fff;
          font-size: 15px; font-weight: 600;
          color: #666; cursor: pointer;
        }
        .btn-save {
          flex: 2; padding: 13px 0;
          border: none; border-radius: 14px;
          background: linear-gradient(135deg, #7bc142, #5aab35);
          font-size: 15px; font-weight: 700;
          color: #fff; cursor: pointer;
          box-shadow: 0 4px 12px rgba(90,171,53,0.3);
          transition: opacity 0.15s;
        }
        .btn-save:hover { opacity: 0.88; }
      `}</style>

      <div className="profile-page">

        {/* 상단 헤더 */}
        <div className="profile-header">
          <div className="logo">READLY</div>
          <FiSettings className="setting-icon" />
        </div>

        {/* 프로필 */}
        <div className="profile-top">
          <div className="profile-image">
            <img src={profileImg} alt="프로필" style={{ width: "100%", height: "100%", objectFit: "cover" }} />
          </div>
          <div className="profile-info">
            <div className="profile-name">
              {userName}
              <button className="pencil-btn" onClick={openModal} aria-label="프로필 수정">✏️</button>
            </div>
            <div className="follow-wrap">
              <div className="follow-box">
                <div className="follow-num">100</div>
                <div className="follow-text">팔로워</div>
              </div>
              <div className="follow-box">
                <div className="follow-num">100</div>
                <div className="follow-text">팔로잉</div>
              </div>
            </div>
          </div>
        </div>

        {/* 소개 */}
        <div className="profile-desc">{profileDesc}</div>

        <div className="divider" />

        {/* 책장 */}
        <div className="bookshelf-area">

          {/* 조명 */}
          <div className="lamp-wrap">
            <div className="lamp-rod" />
            <div className="lamp-head">
              <div className="lamp-glow" />
            </div>
          </div>

          {/* 책 스크롤 */}
          <div
            ref={shelfRef}
            className="book-scroll"
            onMouseDown={onMouseDown}
            onMouseMove={onMouseMove}
            onMouseUp={onMouseUp}
            onMouseLeave={onMouseUp}
          >
            {initialBooks.map((book, index) => {
              const h = heights[index % heights.length];
              const bg = colors[index % colors.length];
              return (
                <div
                  key={index}
                  className="book-item"
                  onClick={() => navigate("/review", { state: { title: book.title, review: book.review } })}
                >
                  <div
                    className="book-spine"
                    style={{ width: "48px", height: `${h}px`, background: bg }}
                  >
                    {book.title}
                  </div>
                </div>
              );
            })}
          </div>

          {/* 나무 선반 */}
          <div className="shelf-board" />
        </div>
      </div>

      {/* 프로필 수정 모달 */}
      {showModal && (
        <div className="modal-backdrop" onClick={(e) => e.target === e.currentTarget && setShowModal(false)}>
          <div className="modal-sheet">
            <div className="modal-title">프로필 수정</div>

            {/* 프로필 사진 */}
            <div className="modal-avatar-wrap">
              <div className="modal-avatar" onClick={() => fileInputRef.current.click()}>
                <img src={editImgPreview} alt="프로필 미리보기" />
                <div className="avatar-overlay">📷</div>
              </div>
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                style={{ display: "none" }}
                onChange={handleImageChange}
              />
            </div>

            {/* 이름 */}
            <div className="modal-label">이름</div>
            <input
              className="modal-input"
              type="text"
              value={editName}
              onChange={(e) => setEditName(e.target.value)}
              placeholder="이름을 입력하세요"
            />

            {/* 소개글 */}
            <div className="modal-label">소개글</div>
            <textarea
              className="modal-input"
              rows={3}
              value={editDesc}
              onChange={(e) => setEditDesc(e.target.value)}
              placeholder="소개글을 입력하세요"
            />

            {/* 버튼 */}
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowModal(false)}>취소</button>
              <button className="btn-save" onClick={handleSave}>저장하기</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default Profile;
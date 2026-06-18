// src/pages/Community.js

import { useState } from "react";

import { useNavigate } from "react-router-dom";

function Community() {
  const navigate = useNavigate();

  const books = [
    {
      title: "노르웨이의 숲",
      author: "무라카미 하루키",
    },

    {
      title: "노르웨이의 숲 그리고 이야기",
      author: "무라카미 하루키",
    },

    {
      title: "데미안",
      author: "헤르만 헤세",
    },

    {
      title: "어린왕자",
      author: "생텍쥐페리",
    },

    {
      title: "1984",
      author: "조지 오웰",
    },
  ];

  const [meetings, setMeetings] =
    useState([
      {
        id: 1,
        title: "감성 독서모임",
        book: "노르웨이의 숲",
        time: "오늘 19:00",
        members: "6/8명",
        status: "모임중",
        mood: "badge1",
      },

      {
        id: 2,
        title: "데미안 함께 읽기",
        book: "데미안",
        time: "내일 20:00",
        members: "4/6명",
        status: "시작전",
        mood: "badge2",
      },

      {
        id: 3,
        title: "마음 나누는 모임",
        book: "어린왕자",
        time: "토요일 18:00",
        members: "5/7명",
        status: "시작전",
        mood: "badge3",
      },
    ]);

  const [showModal, setShowModal] =
    useState(false);

  const [search, setSearch] =
    useState("");

  const [newMeeting, setNewMeeting] =
    useState({
      meetingName: "",
      selectedBook: null,
      people: 4,
      date: "",
      time: "",
      mood: "badge1",
    });

  const filteredBooks = books.filter(
    (book) =>
      search &&
      book.title.startsWith(search)
  );

  const addMeeting = () => {
    if (
      !newMeeting.meetingName ||
      !newMeeting.selectedBook ||
      !newMeeting.date ||
      !newMeeting.time
    ) {
      alert("모든 항목을 입력해주세요!");
      return;
    }

    const newData = {
      id: Date.now(),

      title: newMeeting.meetingName,

      book:
        newMeeting.selectedBook.title,

      time:
        `${newMeeting.date} ${newMeeting.time}`,

      members: `1/${newMeeting.people}명`,

      status: "시작전",

      mood: newMeeting.mood,
    };

    setMeetings([
      newData,
      ...meetings,
    ]);

    setShowModal(false);

    setSearch("");

    setNewMeeting({
      meetingName: "",
      selectedBook: null,
      people: 4,
      date: "",
      time: "",
      mood: "badge1",
    });
  };

  return (
    <div className="page">
      {/* 상단 */}

      <div className="top-bar">
        <div className="logo">
          READLY
        </div>

        <button
          className="add-meeting-btn"
          onClick={() =>
            setShowModal(true)
          }
        >
          + 모임 만들기
        </button>
      </div>

      {/* 안내 */}

      <div className="community-banner">
        ⏰ 모임 시작 10분 전까지
        참여 가능해요
      </div>

      {/* 카드 */}

      {meetings.map((meeting) => (
        <div
          className="group-card"
          key={meeting.id}
        >
          <div className="group-header">
            <div>
              <div className="group-title">
                {meeting.title}
              </div>

              <div className="meeting-book">
                📚 {meeting.book}
              </div>

              <div className="group-info">
                {meeting.time}
              </div>
            </div>

            <div className="badge-wrap">
              <div
                className={
                  meeting.status ===
                  "모임중"
                    ? "badge"
                    : "start-badge"
                }
              >
                {meeting.status}
              </div>

              <div className={meeting.mood}>
                {meeting.mood ===
                  "badge1" &&
                  "깊게 토론해요"}

                {meeting.mood ===
                  "badge2" &&
                  "부담없이 참여해요"}

                {meeting.mood ===
                  "badge3" &&
                  "함께 이야기해요"}
              </div>
            </div>
          </div>

          <div className="group-footer">
            <span>
              {meeting.members}
            </span>

            <button
              className="join-btn"
              onClick={() =>
                navigate("/meeting", {
                  state: {
                    title:
                      meeting.title,

                    mood:
                      meeting.mood,
                  },
                })
              }
            >
              참여하기
            </button>
          </div>
        </div>
      ))}

      {/* 모달 */}

      {showModal && (
        <div className="meeting-modal">
          <div className="meeting-modal-box">
            <div className="modal-title">
              독서모임 만들기
            </div>

            {/* 모임 이름 */}

            <input
              placeholder="모임 이름"
              value={
                newMeeting.meetingName
              }
              onChange={(e) =>
                setNewMeeting({
                  ...newMeeting,
                  meetingName:
                    e.target.value,
                })
              }
            />

            {/* 책 검색 */}

            <div className="book-search">
              <input
                placeholder="책 제목 검색"
                value={search}
                onChange={(e) =>
                  setSearch(
                    e.target.value
                  )
                }
              />
            </div>

            {/* 검색 결과 */}
          <div className="search-result">
            {filteredBooks.map(
              (book, index) => (
                <div
                  key={index}
                  className="search-item"
                  onClick={() => {
                    setNewMeeting({
                      ...newMeeting,
                      selectedBook:
                        book,
                    });

                    setSearch("");
                  }}
                >
                  <div className="search-cover">
                    📚
                  </div>

                  <div>
                    <div className="book-name">
                      {book.title}
                    </div>

                    <div className="book-author">
                      {book.author}
                    </div>
                  </div>
                </div>
              )
            )}
          </div>
            {/* 선택한 책 */}

            {newMeeting.selectedBook && (
              <div className="book-result">
                <div className="book-cover">
                  📚
                </div>

                <div>
                  <div className="book-name">
                    {
                      newMeeting
                        .selectedBook
                        .title
                    }
                  </div>

                  <div className="book-author">
                    {
                      newMeeting
                        .selectedBook
                        .author
                    }
                  </div>
                </div>
              </div>
            )}

            {/* 날짜 / 시간 */}

            <div className="date-time-wrap">
              <input
                type="date"
                value={newMeeting.date}
                onChange={(e) =>
                  setNewMeeting({
                    ...newMeeting,
                    date:
                      e.target.value,
                  })
                }
              />

              <input
                type="time"
                value={newMeeting.time}
                onChange={(e) =>
                  setNewMeeting({
                    ...newMeeting,
                    time:
                      e.target.value,
                  })
                }
              />
            </div>

            {/* 인원수 */}

            <div className="people-box">
              <div className="people-label">
                최대 인원
              </div>

              <div className="people-control">
                <button
                  onClick={() =>
                    setNewMeeting({
                      ...newMeeting,
                      people:
                        newMeeting.people >
                        2
                          ? newMeeting.people -
                            1
                          : 2,
                    })
                  }
                >
                  −
                </button>

                <span>
                  {newMeeting.people}명
                </span>

                <button
                  onClick={() =>
                    setNewMeeting({
                      ...newMeeting,
                      people:
                        newMeeting.people +
                        1,
                    })
                  }
                >
                  +
                </button>
              </div>
            </div>

            {/* 스타일 */}

            <select
              value={newMeeting.mood}
              onChange={(e) =>
                setNewMeeting({
                  ...newMeeting,
                  mood:
                    e.target.value,
                })
              }
            >
              <option value="badge1">
                깊게 토론해요
              </option>

              <option value="badge2">
                부담없이 참여해요
              </option>

              <option value="badge3">
                함께 이야기해요
              </option>
            </select>

            <div className="modal-btn-wrap">
              <button
                className="cancel-btn"
                onClick={() =>
                  setShowModal(false)
                }
              >
                취소
              </button>

              <button
                className="create-btn"
                onClick={addMeeting}
              >
                만들기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Community;
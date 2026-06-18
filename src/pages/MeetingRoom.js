// src/pages/MeetingRoom.js

import {
  useLocation,
  useNavigate,
} from "react-router-dom";

import {
  useState,
  useEffect,
  useRef,
} from "react";

function MeetingRoom() {
  const location = useLocation();
  const navigate = useNavigate();

  const roomTitle =
    location.state?.title ||
    "독서모임";

  const [input, setInput] =
    useState("");

  const [messages, setMessages] =
    useState([
      {
        user: "AI",
        type: "ai",
        text:
          "모임원들의 독후감과 기록을 먼저 정리했어요 ✨\n\n• 외로움과 연결감\n• 상실 이후의 성장\n• 잔잔하지만 깊은 감정선\n• 현실적인 인간 관계",
      },

      {
        user: "AI",
        type: "ai",
        text:
          "오늘은 가장 기억에 남았던 문장을 함께 이야기해볼게요 🙂",
      },

      {
        user: "민지",
        type: "other",
        color: "#FFB6C1",
        text:
          "저는 마지막 장면이 가장 슬펐어요.",
      },

      {
        user: "현우",
        type: "other",
        color: "#87CEEB",
        text:
          "문장이 진짜 고요해서 좋았어요.",
      },

      {
        user: "서연",
        type: "other",
        color: "#C3E88D",
        text:
          "와타나베 감정선이 너무 현실적이었어요.",
      },
    ]);

  const chatEndRef = useRef(null);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({
      behavior: "smooth",
    });
  }, [messages]);

  const sendMessage = () => {
    if (!input.trim()) return;

    const myMessage = {
      user: "나",
      type: "me",
      text: input,
    };

    setMessages((prev) => [
      ...prev,
      myMessage,
    ]);

    setInput("");

    setTimeout(() => {
      const aiReply = {
        user: "AI",
        type: "ai",
        text:
          "좋은 의견이에요 🌿\n그 부분에서 어떤 감정을 느꼈는지 조금 더 이야기해볼까요?",
      };

      setMessages((prev) => [
        ...prev,
        aiReply,
      ]);
    }, 1200);
  };

  return (
    <div className="meeting-page">
      <div className="meeting-header">
        <div>
          <div className="meeting-room-name">
            📚 {roomTitle}
          </div>
        </div>
      </div>

      <div className="meeting-chat-area">
        {messages.map((msg, index) => (
          <div
            key={index}
            className={`chat-row ${
              msg.type === "me"
                ? "right"
                : "left"
            }`}
          >
            {msg.type !== "me" && (
              <div
                className="chat-profile"
                style={{
                  background:
                    msg.type === "ai"
                      ? "#9bd44e"
                      : msg.color,

                  cursor:
                    msg.type === "other"
                      ? "pointer"
                      : "default",
                }}
                onClick={() => {
                  if (
                    msg.type === "other"
                  ) {
                    navigate(
                      "/other-profile",
                      {
                        state: {
                          user:
                            msg.user,
                        },
                      }
                    );
                  }
                }}
              >
                {msg.user[0]}
              </div>
            )}

            <div className="chat-content">
              {msg.type !== "me" && (
                <div
                  className="chat-name"
                  style={{
                    cursor:
                      msg.type ===
                      "other"
                        ? "pointer"
                        : "default",
                  }}
                  onClick={() => {
                    if (
                      msg.type ===
                      "other"
                    ) {
                      navigate(
                        "/other-profile",
                        {
                          state: {
                            user:
                              msg.user,
                          },
                        }
                      );
                    }
                  }}
                >
                  {msg.user}
                </div>
              )}

              <div
                className={`chat-bubble ${
                  msg.type === "me"
                    ? "user-chat"
                    : msg.type === "ai"
                    ? "ai-chat"
                    : "other-chat"
                }`}
              >
                {msg.text}
              </div>
            </div>
          </div>
        ))}

        <div ref={chatEndRef}></div>
      </div>

      <div className="meeting-input-wrap">
        <input
          value={input}
          onChange={(e) =>
            setInput(e.target.value)
          }
          placeholder="생각을 입력해보세요"
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              sendMessage();
            }
          }}
        />

        <button onClick={sendMessage}>
          전송
        </button>
      </div>
    </div>
  );
}

export default MeetingRoom;
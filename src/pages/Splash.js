// src/pages/Splash.js

import { useNavigate } from "react-router-dom";

function Splash() {
  const navigate = useNavigate();

  return (
    <div className="app-container">
      <div className="splash-page">
        <div>
          <div className="splash-logo">
            READLY
          </div>

          <div className="splash-sub">
            읽고, 쓰고, 함께 나누다
          </div>
        </div>

        <button
          className="start-btn"
          onClick={() =>
            navigate("/login")
          }
        >
          시작하기
        </button>
      </div>
    </div>
  );
}

export default Splash;
// src/pages/Login.js

import {
  useState,
} from "react";

import {
  useNavigate,
} from "react-router-dom";

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] =
    useState("");

  const [password, setPassword] =
    useState("");

  const handleLogin = () => {
    const savedUser =
      JSON.parse(
        localStorage.getItem(
          "readlyUser"
        )
      );

    if (
      savedUser?.email === email &&
      savedUser?.password ===
        password
    ) {
      localStorage.setItem(
        "currentUser",
        JSON.stringify(savedUser)
      );

      navigate("/home");
    } else {
      alert(
        "이메일 또는 비밀번호가 달라요"
      );
    }
  };

  return (
    <div className="app-container">
      <div className="auth-page">
        <div className="logo">
          READLY
        </div>

        <div className="auth-title">
          로그인
        </div>

        <input
          className="auth-input"
          placeholder="이메일"
          value={email}
          onChange={(e) =>
            setEmail(e.target.value)
          }
        />

        <input
          className="auth-input"
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) =>
            setPassword(
              e.target.value
            )
          }
        />

        <button
          className="auth-btn"
          onClick={handleLogin}
        >
          로그인
        </button>

        <div className="auth-bottom">
          계정이 없나요?

          <span
            onClick={() =>
              navigate("/signup")
            }
          >
            회원가입
          </span>
        </div>
      </div>
    </div>
  );
}

export default Login;
// src/pages/Signup.js

import {
  useState,
} from "react";

import {
  useNavigate,
} from "react-router-dom";

function Signup() {
  const navigate = useNavigate();

  const [name, setName] =
    useState("");

  const [email, setEmail] =
    useState("");

  const [password, setPassword] =
    useState("");

  const handleSignup = () => {
    const user = {
      name,
      email,
      password,
    };

    localStorage.setItem(
      "readlyUser",
      JSON.stringify(user)
    );

    navigate("/login");
  };

  return (
    <div className="app-container">
      <div className="auth-page">
        <div className="logo">
          READLY
        </div>

        <div className="auth-title">
          회원가입
        </div>

        <input
          className="auth-input"
          placeholder="이름"
          value={name}
          onChange={(e) =>
            setName(e.target.value)
          }
        />

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
          onClick={handleSignup}
        >
          가입하기
        </button>
      </div>
    </div>
  );
}

export default Signup;
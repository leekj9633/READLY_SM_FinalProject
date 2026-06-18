// src/pages/AIWrite.js

import { useState } from "react";

import {
  FiSearch,
  FiCamera,
} from "react-icons/fi";

function AIWrite() {
  const [searched, setSearched] =
    useState(false);

  const [selected, setSelected] =
    useState(false);

  const [generated, setGenerated] =
    useState(false);

  return (
    <div className="page">
      {/* 상단 */}

      <div className="top-bar">
        <div className="logo">
          READLY
        </div>
      </div>
      <div className="line"></div>

      {/* 제목 */}

      <div className="write-top">
        <div className="write-title">
          AI 독후감 작성
        </div>

        <div className="write-sub">
          책과 감상을 기록해보세요
        </div>
      </div>

      {/* 책 검색 */}

      <div className="write-card">
        <div className="input-label">
          어떤 책을 읽었나요?
        </div>

        <div className="book-search">
          <FiSearch />

          <input
            placeholder="책 제목 검색하기"
            onChange={() =>
              setSearched(true)
            }
          />
        </div>

        {/* 검색 결과 */}

        {searched && !selected && (
          <div
            className="search-item"
            onClick={() =>
              setSelected(true)
            }
          >
            <div className="search-cover">
              📘
            </div>

            <div>
              <div className="book-name">
                노르웨이의 숲
              </div>

              <div className="book-author">
                무라카미 하루키
              </div>
            </div>
          </div>
        )}

        {/* 선택된 책 */}

        {selected && (
          <div className="book-result">
            <div className="book-cover">
              📘
            </div>

            <div>
              <div className="book-name">
                노르웨이의 숲
              </div>

              <div className="book-author">
                무라카미 하루키
              </div>
            </div>
          </div>
        )}
      </div>

      {/* 인상 깊은 구절 */}

      <div className="write-card">
        <div className="input-label">
          인상 깊은 구절
        </div>

        <textarea
          className="write-textarea"
          placeholder="기억에 남는 문장을 적어보세요"
        ></textarea>

        <button className="photo-btn">
          <FiCamera />

          책 구절 사진 찍기
        </button>
      </div>

      {/* 느낀점 */}

      <div className="write-card">
        <div className="input-label">
          느낀점
        </div>

        <textarea
          className="write-textarea big"
          placeholder="책을 읽고 어떤 생각이 들었나요?"
        ></textarea>
      </div>

      {/* 생성 버튼 */}

      <button
        className="generate-btn"
        onClick={() =>
          setGenerated(true)
        }
      >
        AI 독후감 생성하기
      </button>

      {/* 결과 */}

      {generated && (
        <div className="result-card">
          <div className="result-title">
            ✨ AI 독후감
          </div>

          <div className="result-content">
            “노르웨이의 숲”은 상실과
            외로움 속에서도 사람 사이의
            연결이 얼마나 중요한지를
            보여주는 작품이었다.
          </div>
        </div>
      )}
    </div>
  );
}

export default AIWrite;
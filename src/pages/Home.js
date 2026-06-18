// src/pages/Home.js

import {
  FiBell,
  FiSearch,
  FiPlus,
} from "react-icons/fi";

import {
  Swiper,
  SwiperSlide,
} from "swiper/react";

import { Autoplay } from "swiper/modules";

import "swiper/css";

const books = [
  {
    title: "노르웨이의 숲",
    image:
      "/images/노르웨이의숲.jpeg",
  },
  {
    title: "데미안",
    image:
      "/images/데미안.jpeg",
  },
  {
    title: "어린왕자",
    image:
      "/images/어린왕자.jpeg",
  },
];

function Home() {
  return (
    <div className="page">
      {/* 상단 */}
      <div className="top-bar">
        <div className="logo">
          READLY
        </div>

        <div className="icon-group">
          <FiPlus />
          <FiSearch />

          <div className="bell-wrap">
            <FiBell />
            <div className="bell-dot"></div>
          </div>
        </div>
      </div>

      {/* 슬라이드 */}
      <Swiper
        modules={[Autoplay]}
        autoplay={{
          delay: 2500,
          disableOnInteraction: false,
        }}
        loop={true}
        spaceBetween={14}
        slidesPerView={1}
        className="book-swiper"
      >
        {books.map((book, index) => (
          <SwiperSlide key={index}>
            <div className="hero-card">
              <img
                src={book.image}
                alt={book.title}
                className="hero-image"
              />

              <div className="hero-title">
                {book.title}
              </div>

              <div className="hero-sub">
                지금 가장 인기있는 책이에요
              </div>
            </div>
          </SwiperSlide>
        ))}
      </Swiper>

      {/* 모임 카드 */}

      <div className="group-card">
        <div className="group-header">
          <div>
            <div className="group-title">
              &lt;프로젝트 헤밍웨이&gt;
            </div>

            <div className="group-info">
              12:00 ~ 01:00
            </div>
          </div>

          <div className="badge-wrap">
            <div className="badge">
              모임중
            </div>

            <div className="badge1">
              깊게 토론해요
            </div>
          </div>
        </div>

        <div className="line"></div>

        <div className="group-footer">
          <span>2026.05.30</span>
          <span>5/10명</span>
        </div>
      </div>

      <div className="group-card">
        <div className="group-header">
          <div>
            <div className="group-title">
              &lt;노르웨이의 숲 함께 읽어요!&gt;
            </div>

            <div className="group-info">
              19:00 ~ 20:30
            </div>
          </div>

          <div className="badge-wrap">
            <div className="badge">
              모임중
            </div>

            <div className="badge3">
              함께 이야기해요
            </div>
          </div>
        </div>

        <div className="line"></div>

        <div className="group-footer">
          <span>2026.03.12</span>
          <span>3/8명</span>
        </div>
      </div>

      <div className="group-card">
        <div className="group-header">
          <div>
            <div className="group-title">
              &lt;데미안 독서모임&gt;
            </div>

            <div className="group-info">
              18:00 ~ 19:30
            </div>
          </div>

          <div className="badge-wrap">
            <div className="badge">
              모임중
            </div>

            <div className="badge2">
              부담없이 참여해요
            </div>
          </div>
        </div>

        <div className="line"></div>

        <div className="group-footer">
          <span>2026.04.01</span>
          <span>2/6명</span>
        </div>
      </div>
    </div>
  );
}

export default Home;
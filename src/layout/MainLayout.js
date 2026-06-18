// src/layout/MainLayout.js

import {
  Outlet,
  useNavigate,
  useLocation,
} from "react-router-dom";

import {
  FiHome,
  FiEdit3,
  FiUsers,
  FiUser,
} from "react-icons/fi";

function MainLayout() {
  const navigate = useNavigate();

  const location = useLocation();

  return (
    <div className="app-container">
      <Outlet />

      <div className="bottom-nav">
        <FiHome
          className={
            location.pathname ===
            "/home"
              ? "active-nav"
              : ""
          }
          onClick={() =>
            navigate("/home")
          }
        />

        <FiEdit3
          className={
            location.pathname ===
            "/aiwrite"
              ? "active-nav"
              : ""
          }
          onClick={() =>
            navigate("/aiwrite")
          }
        />

        <FiUsers
          className={
            location.pathname ===
            "/community"
              ? "active-nav"
              : ""
          }
          onClick={() =>
            navigate("/community")
          }
        />

        <FiUser
          className={
            location.pathname ===
            "/profile"
              ? "active-nav"
              : ""
          }
          onClick={() =>
            navigate("/profile")
          }
        />
      </div>
    </div>
  );
}

export default MainLayout;
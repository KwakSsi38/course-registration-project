// src/components/Header.jsx
import {Link, useNavigate, useLocation} from "react-router-dom";
import axios from "axios";
import "../css/Components.css";

function Header() {
  const navigate = useNavigate();
  const location = useLocation();

  // 로그인 페이지에서는 헤더만, 로그아웃 버튼은 숨김
  const isLoginPage = location.pathname === "/";

  const handleLogout = () => {
    localStorage.removeItem("token"); // 토큰 삭제
    delete axios.defaults.headers.common["Authorization"]; // axios 헤더 제거
    navigate("/"); // 로그인 페이지로 이동
  };

  return (
    <header className="global-header">
      <Link to="/" className="logo-link">
        ❄️ Sookmyung DBP
      </Link>

      {!isLoginPage && (
        <button className="logout-button" onClick={handleLogout}>
          로그아웃
        </button>
      )}
    </header>
  );
}

export default Header;

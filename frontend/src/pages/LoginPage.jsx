// src/pages/LoginPage.jsx

import {useState} from "react";
import axios from "axios";
import {useEffect} from "react";
import {isLoggedIn} from "../utils/auth";
import "../css/LoginPage.css";
import "../css/Error.css";
import {useNavigate} from "react-router-dom";

function LoginPage() {
  const [studentId, setStudentId] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (isLoggedIn()) {
      window.location.href = "/mypage";
    }
  }, []);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      // 요청 보내기 - 우선 더미 처리
      const response = await axios.post(
        "http://localhost:8080/api/users/login",
        {
          studentId,
          password,
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      const {token} = response.data;

      localStorage.setItem("token", token); // 로컬 스토리지 저장

      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`; // 전역 Authorization 설정

      navigate("/mypage");
    } catch (err) {
      if (err.response?.status === 401) {
        setError("학번 또는 비밀번호가 일치하지 않습니다.");
      } else if (err.response?.status === 404) {
        setError("페이지를 찾을 수 없습니다.");
      } else {
        setError("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <h2>로그인</h2>
      <form className="login-form" onSubmit={handleLogin}>
        <div>
          <label htmlFor="studentId">아이디</label>
          <input
            type="text"
            id="studentId"
            placeholder="아이디를 입력하세요"
            value={studentId}
            onChange={(e) => setStudentId(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="password">비밀번호</label>
          <input
            type="password"
            id="password"
            placeholder="비밀번호를 입력하세요"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <div className="error-message">{error}</div>}
        <button type="submit" disabled={loading}>
          {loading ? "로그인 중..." : "로그인"}
        </button>
      </form>
    </div>
  );
}

export default LoginPage;

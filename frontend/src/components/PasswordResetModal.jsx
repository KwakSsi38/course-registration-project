import {useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import "../css/Modal.css";
import "../css/Error.css";

function PasswordResetModal({onClose}) {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (newPassword !== confirmPassword) {
      setError("새 비밀번호가 서로 일치하지 않습니다.");
      return;
    }

    try {
      // 요청 보내기
      // await axios.patch("/api/my-page/edit", {
      //   currentPassword,
      //   newPassword,
      // });

      // 성공 시: 로그아웃 처리
      localStorage.removeItem("token");
      delete axios.defaults.headers.common["Authorization"];
      alert("비밀번호가 변경되었습니다. 다시 로그인 해주세요.");
      navigate("/");
    } catch (err) {
      if (err.response?.status === 400) {
        setError("현재 비밀번호가 올바르지 않습니다.");
      } else {
        setError("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      }
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h2>비밀번호 재설정</h2>
        <form onSubmit={handleSubmit}>
          <label>현재 비밀번호</label>
          <input
            type="password"
            placeholder="현재 비밀번호"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            required
          />
          <label>새 비밀번호</label>
          <input
            type="password"
            placeholder="새 비밀번호"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            required
          />
          <label>새 비밀번호 확인</label>
          <input
            type="password"
            placeholder="비밀번호 확인"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />

          {error && <div className="error-message">{error}</div>}

          <div className="modal-buttons">
            <button type="submit">변경</button>
            <button type="button" onClick={onClose}>
              닫기
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default PasswordResetModal;

import {useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import "../css/Modal.css";
import "../css/Error.css";

function PasswordResetModal({onClose}) {
  const [password, setpassword] = useState("");
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
      // 1. 현재 비밀번호 검증 요청
      const verifyResponse = await axios.post(
        "http://localhost:8080/api/users/verify-password",
        {
          password: password, // 현재 입력된 비밀번호
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (verifyResponse.status === 200) {
        const verifiedToken = verifyResponse.data.verifiedToken;
        // 2. 비밀번호 변경 요청
        const changePasswordResponse = await axios.patch(
          "http://localhost:8080/api/my-page/edit",
          {
            newPassword: newPassword,
          },
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${verifiedToken}`,
            },
          }
        );

        if (changePasswordResponse.status === 200) {
          localStorage.removeItem("token");
          delete axios.defaults.headers.common["Authorization"];
          alert("비밀번호가 변경되었습니다. 다시 로그인 해주세요.");
          navigate("/");
        }
      }
    } catch (err) {
      if (err.response) {
        if (err.response.status === 401) {
          setError("현재 비밀번호가 일치하지 않습니다.");
        } else if (err.response.status === 403) {
          setError(err.response.data);
        } else {
          setError(`서버 오류가 발생했습니다.`);
          //: ${err.response.status} - ${err.response.data || err.message}
        }
      } else {
        setError("네트워크 오류가 발생했습니다.");
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
            value={password}
            onChange={(e) => setpassword(e.target.value)}
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

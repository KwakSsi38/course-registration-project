import React, {useState, useEffect} from "react";
import "../css/MyPage.css";
import Sidebar from "../components/Sidebar";
import PasswordResetModal from "../components/PasswordResetModal";
import axios from "axios";

function MyPage() {
  const [year, setYear] = useState("2025");
  const [semester, setSemester] = useState("1");
  const [courses, setCourses] = useState([]);
  const [student, setStudent] = useState({});
  const [isPasswordModalOpen, setPasswordModalOpen] = useState(false);
  const [error, setError] = useState("");


  // 년도 + 학기 계산
  const getCurrentYearAndSemester = () => {
    const today = new Date();
    const month = today.getMonth() + 1; // getMonth는 0부터 시작
    let year = today.getFullYear();
    let semester;

    if (month >= 3 && month <= 8) {
      semester = "1";
    } else {
      semester = "2";
      if (month <= 2) {
        year -= 1; // 1~2월은 전년도 2학기
      }
    }

    return {year: String(year), semester};
  };

  // 마운트 시 학생 정보 + 기본 시간표 불러오기
  useEffect(() => {
    const {year, semester} = getCurrentYearAndSemester();
    setYear(year);
    setSemester(semester);
    fetchStudentInfo();
    fetchSchedule(year, semester);
  }, []);

  const fetchStudentInfo = async () => {
    try {
      const res = await axios.get("/api/my-page");
      setStudent(res.data);
    } catch (err) {
      // setError("학생 정보를 불러오는 데 실패했습니다.");
      console.warn("학생 정보 API 실패. 더미 데이터 사용.");
      setStudent({
        s_id: "20231234",
        s_name: "홍길동",
        s_grade: "2학년",
        s_term: "2025-1",
        s_major1: "컴퓨터공학과",
        s_major2: "경영학과",
        s_status: "재학",
        s_phone: "010-1234-5678",
        s_email: "hong@smu.ac.kr",
        s_address: "서울시 종로구 대학로",
        s_account: "국민은행 123456-78-901234",
      });
    }
  };

  const fetchSchedule = async (y, s) => {
    try {
      const res = await axios.get("/api/my-page/schedule", {
        params: {year: y, semester: s},
      });
      setCourses(res.data);
    } catch (err) {
      // setError("시간표를 불러오는 데 실패했습니다.");
      console.warn("시간표 API 실패. 더미 데이터 사용.");
      setCourses([
        {
          se_id: "CS101",
          se_name: "자료구조",
          se_section: "01",
          se_department: "컴퓨터공학과",
          se_credit: 3,
          se_professor: "김교수",
          se_time: "월 1-3",
          se_room: "IT관 201호",
        },
        {
          se_id: "MATH201",
          se_name: "선형대수",
          se_section: "02",
          se_department: "수학과",
          se_credit: 3,
          se_professor: "이교수",
          se_time: "화 2-4",
          se_room: "수학관 101호",
        },
      ]);
    }
  };

  const handleSearch = () => {
    fetchSchedule(year, semester);
  };

  const totalCredits = courses.reduce((sum, c) => sum + c.se_credit, 0);

  return (
    <div className="mypage-grid">
      <Sidebar />

      <main className="main-content">
        <h2>학적 기본사항 관리</h2>

        {error && <div className="error-message">{error}</div>}

        <div className="info-section">
          <div className="photo-box">{student.s_name ? student.s_name.charAt(0) : ""}</div>

          <div className="info-wrap">
            <table className="info-table">
              <tbody>
                <tr>
                  <th>학번:</th>
                  <td>{student.s_id}</td>
                  <th>상태:</th>
                  <td>{student.s_status}</td>
                </tr>
                <tr>
                  <th>성명:</th>
                  <td>{student.s_name}</td>
                  <th>연락처:</th>
                  <td>{student.s_phone}</td>
                </tr>
                <tr>
                  <th>학년:</th>
                  <td>{student.s_grade}</td>
                  <th>이메일:</th>
                  <td>{student.s_email}</td>
                </tr>
                <tr>
                  <th>학기:</th>
                  <td>{student.s_term}</td>
                  <th>주소:</th>
                  <td>{student.s_address}</td>
                </tr>
                <tr>
                  <th>전공:</th>
                  <td>{student.s_major1 || "-"}</td>
                  <th>계좌:</th>
                  <td colSpan={3}>{student.s_account}</td>
                </tr>
                <tr>
                  <th>복수전공:</th>
                  <td>{student.s_major2 || "-"}</td>
                  <th></th>
                  <td></td>
                </tr>
              </tbody>
            </table>

            <div className="info-buttons-vertical">
              <button onClick={() => setPasswordModalOpen(true)}>비밀번호 재설정</button>
              <button>개인정보 수정</button>
              <button>휴학 신청</button>
            </div>
          </div>
        </div>

        <h3>나의 시간표 조회</h3>
        <div className="timetable-filter">
          <label>
            학년도:
            <select value={year} onChange={(e) => setYear(e.target.value)}>
              <option value="2025">2025</option>
              <option value="2024">2024</option>
              <option value="2023">2023</option>
            </select>
          </label>
          <label>
            학기:
            <select value={semester} onChange={(e) => setSemester(e.target.value)}>
              <option value="1">1</option>
              <option value="2">2</option>
            </select>
          </label>
          <button onClick={handleSearch}>검색</button>
        </div>

        <div className="course-table">
          <table>
            <thead>
              <tr>
                <th>과목번호</th>
                <th>과목명</th>
                <th>분반</th>
                <th>주관학부</th>
                <th>학점</th>
                <th>강사</th>
                <th>강의시간</th>
                <th>강의실</th>
              </tr>
            </thead>
            <tbody>
              {courses.length > 0 ? (
                courses.map((course, index) => (
                  <tr key={index}>
                    <td>{course.se_id}</td>
                    <td>{course.se_name}</td>
                    <td>{course.se_section}</td>
                    <td>{course.se_department}</td>
                    <td>{course.se_credit}</td>
                    <td>{course.se_professor}</td>
                    <td>{course.se_time}</td>
                    <td>{course.se_room}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="8" style={{textAlign: "center"}}>
                    조회된 강의가 없습니다.
                  </td>
                </tr>
              )}
            </tbody>
          </table>

          <div className="summary">
            총 신청 과목수: {courses.length || "-"} / 총 신청 학점: {courses.length ? totalCredits : "-"}학점
          </div>
        </div>

        {isPasswordModalOpen && <PasswordResetModal onClose={() => setPasswordModalOpen(false)} />}
      </main>
    </div>
  );
}

export default MyPage;

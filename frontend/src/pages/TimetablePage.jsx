// src/pages/TimetablePage.jsx

import React, {useState, useEffect} from "react"; // useEffect 임포트
import axios from "axios"; // axios 임포트
import Sidebar from "../components/Sidebar";
import "../css/TimetablePage.css";

function TimetablePage() {
  const [year, setYear] = useState("2025");
  const [semester, setSemester] = useState("1");
  const [selectedCourses, setSelectedCourses] = useState([]); // 초기값을 빈 배열로 설정

  // --- 시간표 데이터를 서버에서 불러오는 함수 ---
  const fetchSchedule = async (y, s) => {
    try {
      const res = await axios.get("http://localhost:8080/api/my-page/timetable", {
        params: {year: y, semester: s},
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`, // 인증 토큰 추가
        },
      });
      setSelectedCourses(res.data);
    } catch (err) {
      console.warn("시간표 API 호출 실패. 더미 데이터를 사용합니다.");
      // 오류 발생 시 더미 데이터 사용
      setSelectedCourses([
        {
          courseIdNo: "C006",
          courseName: "인지심리학",
          section: 3,
          professorName: "오세훈",
          time: "월2,화3",
          classroom: "강의동 162호",
        },
        {
          courseIdNo: "C010",
          courseName: "전자기학",
          section: 2,
          professorName: "류지은",
          time: "화4,수1",
          classroom: "강의동 324호",
        },
        {
          courseIdNo: "CS101",
          courseName: "자료구조",
          section: "01",
          professorName: "김교수",
          time: "월1,화2",
          classroom: "IT관 201호",
        },
        {
          courseIdNo: "MATH201",
          courseName: "선형대수",
          section: "02",
          professorName: "이교수",
          time: "화3,목5",
          classroom: "수학관 101호",
        },
      ]);
      // 사용자에게 에러 메시지를 보여주고 싶다면 setError 상태를 활용
      // setError("시간표를 불러오는 데 실패했습니다.");
    }
  };

  // --- 컴포넌트 마운트 시 초기 시간표 로드 ---
  useEffect(() => {
    fetchSchedule(year, semester); // 컴포넌트가 처음 렌더링될 때 현재 년도/학기로 시간표 로드
  }, [year, semester]); // year 또는 semester가 변경되면 다시 fetchSchedule 호출

  const days = ["월", "화", "수", "목", "금", "토"];
  const periods = [1, 2, 3, 4, 5, 6, 7];

  // 시간표 초기화: 각 셀을 null로 초기화
  const timetable = {};
  days.forEach((day) => {
    timetable[day] = {};
    periods.forEach((p) => {
      timetable[day][p] = null;
    });
  });

  // 선택된 강의들을 시간표에 매핑
  selectedCourses.forEach((course) => {
    const timeParts = course.time.split(",");

    timeParts.forEach((part) => {
      const matches = part.match(/([월화수목금토])(\d+)/);

      if (matches) {
        const day = matches[1];
        const period = parseInt(matches[2], 10);

        if (timetable[day] && periods.includes(period)) {
          timetable[day][period] = course;
        } else {
          console.warn(`유효하지 않은 요일/교시 정보: ${day} ${period} (강의: ${course.courseName})`);
        }
      } else {
        console.warn(`시간 형식 오류: "${part}" (강의: ${course.courseName})`);
      }
    });
  });

  // --- 검색 버튼 클릭 시 호출되는 핸들러 ---
  const handleSearch = () => {
    fetchSchedule(year, semester);
  };

  return (
    <div className="timetable-grid">
      <Sidebar />

      <main className="main-content">
        <h2>수강 신청 타임테이블</h2>

        <div className="filter-section">
          <label>
            학년도:
            <select value={year} onChange={(e) => setYear(e.target.value)}>
              <option value="2025">2025</option>
              <option value="2024">2024</option>
            </select>
          </label>

          <label>
            학기:
            <select value={semester} onChange={(e) => setSemester(e.target.value)}>
              <option value="1">1학기</option>
              <option value="2">2학기</option>
            </select>
          </label>

          <button onClick={handleSearch}>검색</button>
        </div>

        <div className="timetable-wrapper">
          <table className="timetable-table">
            <thead>
              <tr>
                <th>교시</th>
                {days.map((day) => (
                  <th key={day}>{day}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {periods.map((p) => (
                <tr key={p}>
                  <td>{p}교시</td>
                  {days.map((d) => {
                    const c = timetable[d][p];
                    return (
                      <td key={d}>
                        {c && (
                          <div className="cell-course">
                            <div className="courseName">{c.courseName}</div>
                            <div className="info">
                              {c.courseIdNo}-{c.section}
                              <br />
                              {c.professorName}
                              <br />
                              {c.classroom}
                              <br />
                              {c.time}
                            </div>
                          </div>
                        )}
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
}

export default TimetablePage;

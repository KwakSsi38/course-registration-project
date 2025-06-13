// src/pages/TimetablePage.jsx

import React, {useState} from "react";
import Sidebar from "../components/Sidebar";
import "../css/TimetablePage.css";

function TimetablePage() {
  const [year, setYear] = useState("2025");
  const [semester, setSemester] = useState("1");
  const [selectedCourses, setSelectedCourses] = useState([
    {
      id: "CS101",
      name: "자료구조",
      section: "01",
      professor: "김교수",
      time: "월 1-3",
      room: "IT관 201호",
    },
    {
      id: "MATH201",
      name: "선형대수",
      section: "02",
      professor: "이교수",
      time: "화 2-4",
      room: "수학관 101호",
    },
  ]);

  const days = ["월", "화", "수", "목", "금", "토"];
  const periods = [1, 2, 3, 4, 5, 6, 7];

  const timetable = {};
  days.forEach((day) => {
    timetable[day] = {};
    periods.forEach((p) => {
      timetable[day][p] = null;
    });
  });

  selectedCourses.forEach((course) => {
    const matches = course.time.match(/([월화수목금토])\s?(\d)-(\d)/);
    if (matches) {
      const day = matches[1];
      const start = parseInt(matches[2], 10);
      const end = parseInt(matches[3], 10);
      for (let i = start; i <= end; i++) {
        timetable[day][i] = course;
      }
    }
  });

  const handleSearch = () => {
    // TODO: 실제 백엔드 연동 시 사용될 필터 처리
    console.log("검색", year, semester);
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
                          <div className="name">{c.name}</div>
                          <div className="info">
                            {c.id}-{c.section}
                            <br />
                            {c.professor}
                            <br />
                            {c.room}
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
      </main>
    </div>
  );
}

export default TimetablePage;

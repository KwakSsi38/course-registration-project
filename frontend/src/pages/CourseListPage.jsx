import React, {useEffect, useState} from "react";
import axios from "axios";
import Sidebar from "../components/Sidebar";
import "../css/CourseListPage.css";

function CourseListPage() {
  const [year] = useState("2025");
  const [semester] = useState("1");
  const [courses, setCourses] = useState([]);
  const [selectedCourses, setSelectedCourses] = useState([]);
  const [searchField, setSearchField] = useState("id");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [hasSearched, setHasSearched] = useState(false);

  useEffect(() => {
    const fetchMyCourses = async () => {
      try {
        const res = await axios.get("/api/subject/my-subject");
        setSelectedCourses(res.data);
      } catch (err) {
        // console.warn("나의 수강 목록 불러오기 실패");
        console.warn("나의 수강 목록 불러오기 실패. 더미 데이터 사용");
        setSelectedCourses([
          {
            id: "CS101",
            name: "자료구조",
            section: "01",
            department: "컴퓨터공학과",
            credit: 3,
            professor: "김교수",
            time: "월 1-3",
            room: "IT관 201호",
          },
        ]);
      }
    };

    fetchMyCourses();
  }, []);

  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      setCourses([]);
      setHasSearched(false);
      return;
    }

    setHasSearched(true);

    try {
      const res = await axios.get("/api/subject", {
        params: {
          field: searchField,
          keyword: searchKeyword,
        },
      });
      setCourses(res.data);
    } catch (err) {
      // console.warn("전체 강의 목록 검색 실패");
      console.warn("전체 강의 목록 검색 실패. 더미 데이터 사용");
      setCourses([
        {
          id: "CS102",
          name: "알고리즘",
          section: "02",
          department: "컴퓨터공학과",
          credit: 3,
          professor: "박교수",
          time: "화 2-4",
          room: "IT관 202호",
        },
        {
          id: "ENG101",
          name: "기초영어",
          section: "01",
          department: "교양학부",
          credit: 2,
          professor: "이교수",
          time: "수 1-2",
          room: "인문관 301호",
        },
      ]);
    }
  };

  const handleRegister = async (course) => {
    if (selectedCourses.find((c) => c.id === course.id)) return;

    try {
      await axios.post("/api/subject/apply", course);
      alert("수강 신청 완료!");
      setSelectedCourses([...selectedCourses, course]);
    } catch (err) {
      alert("수강 신청 실패");
      console.error(err);
    }
  };

  const handleCancel = async (course) => {
    try {
      await axios.post("/api/subject/cancel", course);
      alert("수강 취소 완료!");
      setSelectedCourses(selectedCourses.filter((c) => c.id !== course.id));
    } catch (err) {
      alert("수강 취소 실패");
      console.error(err);
    }
  };

  const totalCredits = selectedCourses.reduce((sum, c) => sum + c.credit, 0);

  return (
    <div className="course-grid">
      <Sidebar />

      <main className="main-content">
        <h2>수강 신청 내역</h2>

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
                <th>수강 취소</th>
              </tr>
            </thead>
            <tbody>
              {selectedCourses.length > 0 ? (
                selectedCourses.map((course, index) => (
                  <tr key={index}>
                    <td>{course.id}</td>
                    <td>{course.name}</td>
                    <td>{course.section}</td>
                    <td>{course.department}</td>
                    <td>{course.credit}</td>
                    <td>{course.professor}</td>
                    <td>{course.time}</td>
                    <td>{course.room}</td>
                    <td>
                      <button onClick={() => handleCancel(course)}>취소</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="9" style={{textAlign: "center"}}>
                    수강 신청한 강의가 없습니다.
                  </td>
                </tr>
              )}
            </tbody>
          </table>

          <div className="summary">
            총 신청 과목수: {selectedCourses.length} / 총 신청 학점: {totalCredits}학점
          </div>
        </div>

        <h2>수강 신청</h2>

        <form
          className="filter-section"
          onSubmit={(e) => {
            e.preventDefault();
            handleSearch();
          }}
        >
          <select value={searchField} onChange={(e) => setSearchField(e.target.value)}>
            <option value="id">과목번호</option>
            <option value="name">과목명</option>
            <option value="credit">학점</option>
            <option value="professor">강사</option>
          </select>
          <input
            type="text"
            placeholder="검색어 입력"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
          <button type="submit">검색</button>
        </form>

        <div className="course-table">
          <table>
            <thead>
              <tr>
                <th>과목번호</th>
                <th>과목명</th>
                <th>분반</th>
                <th>학부</th>
                <th>학점</th>
                <th>강사</th>
                <th>강의시간</th>
                <th>강의실</th>
                <th>수강 신청</th>
              </tr>
            </thead>
            <tbody>
              {hasSearched && courses.length > 0 ? (
                courses.map((course, index) => (
                  <tr key={index}>
                    <td>{course.id}</td>
                    <td>{course.name}</td>
                    <td>{course.section}</td>
                    <td>{course.department}</td>
                    <td>{course.credit}</td>
                    <td>{course.professor}</td>
                    <td>{course.time}</td>
                    <td>{course.room}</td>
                    <td>
                      <button onClick={() => handleRegister(course)}>신청</button>
                    </td>
                  </tr>
                ))
              ) : hasSearched ? (
                <tr>
                  <td colSpan="9" style={{textAlign: "center"}}>
                    검색 결과가 없습니다.
                  </td>
                </tr>
              ) : (
                <tr>
                  <td colSpan="9" style={{textAlign: "center"}}>
                    검색어를 입력해주세요.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
}

export default CourseListPage;

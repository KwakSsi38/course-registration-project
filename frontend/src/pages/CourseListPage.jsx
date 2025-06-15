import React, {useEffect, useState} from "react";
import axios from "axios";
import Sidebar from "../components/Sidebar";
import "../css/CourseListPage.css";

function CourseListPage() {
  const [courses, setCourses] = useState([]);
  const [selectedCourses, setSelectedCourses] = useState([]);
  const [searchField, setSearchField] = useState("id");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [hasSearched, setHasSearched] = useState(false);

  useEffect(() => {
    const fetchMyCourses = async () => {
      try {
        const res = await axios.get("http://localhost:8080/api/timetable/view");
        setSelectedCourses(res.data);
      } catch (err) {
        // console.warn("나의 수강 목록 불러오기 실패");
        console.warn("나의 수강 목록 불러오기 실패. 더미 데이터 사용");
        setSelectedCourses([]);
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
      const res = await axios.get("http://localhost:8080/api/sections/list", {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`, // 인증 토큰 추가
        },
      });
      setCourses(res.data);
    } catch (err) {
      // console.warn("전체 강의 목록 검색 실패");
      console.warn("전체 강의 목록 검색 실패. 더미 데이터 사용");
      setCourses([
        {
          courseIdNo: "CS102",
          courseName: "알고리즘",
          section: "02",
          major: "컴퓨터공학과",
          unit: 3,
          professorName: "박교수",
          time: "화 2-4",
          capacity: "80",
          classroom: "IT관 202호",
        },
      ]);
    }
  };

  const handleRegister = async (course) => {
    const {sectionId} = course;

    try {
      const enrollRes = await axios.post("http://localhost:8080/api/subject/apply", sectionId, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (enrollRes.data == "이미 같은 과목을 수강 신청하였습니다") {
        alert("이미 같은 과목을 수강 신청하였습니다");
        return;
      }
      alert("수강 신청 완료");
      const res = await axios.get("http://localhost:8080/api/timetable/view");
      setSelectedCourses(res.data);
    } catch (err) {
      alert("수강 신청 실패");
      console.error(err);
    }
  };

  const handleCancel = async (course) => {
    const {courseIdNo, section} = course;

    try {
      await axios.delete("http://localhost:8080/api/subject/cancel", {
        data: {
          courseIdNo,
          sectionNo: section,
        },
        headers: {
          "Content-Type": "application/json",
        },
      });
      alert("수강 취소 완료");
      const res = await axios.get("http://localhost:8080/api/timetable/view");
      setSelectedCourses(res.data);
    } catch (err) {
      alert("수강 취소 실패");
      console.error(err);
    }
  };

  const totalCredits = selectedCourses.reduce((sum, c) => sum + c.credits, 0);

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
                <th>여석</th>
                <th>수강 취소</th>
              </tr>
            </thead>
            <tbody>
              {selectedCourses.length > 0 ? (
                selectedCourses.map((course, index) => (
                  <tr key={index}>
                    <td>{course.courseIdNo}</td>
                    <td>{course.courseName}</td>
                    <td>{course.section}</td>
                    <td>{course.major}</td>
                    <td>{course.credits || "-"}</td>
                    <td>{course.professorName}</td>
                    <td>{course.time}</td>
                    <td>{course.classroom}</td>
                    <td>{course.capacity}</td>
                    <td>
                      <button onClick={() => handleCancel(course)}>취소</button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="10" style={{textAlign: "center"}}>
                    수강 신청한 강의가 없습니다.
                  </td>
                </tr>
              )}
            </tbody>
          </table>

          <div className="summary">
            총 신청 과목수: {selectedCourses.length} / 총 신청 학점: {selectedCourses.length ? totalCredits : "0"}학점
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
            <option value="unit">학점</option>
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
                <th>주관 학부</th>
                <th>학점</th>
                <th>강사</th>
                <th>강의시간</th>
                <th>강의실</th>
                <th>여석</th>
                <th>수강 신청</th>
              </tr>
            </thead>
            <tbody>
              {hasSearched && courses.length > 0 ? (
                courses.map((course, index) => (
                  <tr key={index}>
                    <td>{course.courseIdNo}</td>
                    <td>{course.courseName}</td>
                    <td>{course.section}</td>
                    <td>{course.major}</td>
                    <td>{course.credit}</td>
                    <td>{course.professorName}</td>
                    <td>{course.classTime}</td>
                    <td>{course.classroom}</td>
                    <td>{course.capacity}</td>
                    <td>
                      <button onClick={() => handleRegister(course)}>신청</button>
                    </td>
                  </tr>
                ))
              ) : hasSearched ? (
                <tr>
                  <td colSpan="10" style={{textAlign: "center"}}>
                    검색 결과가 없습니다.
                  </td>
                </tr>
              ) : (
                <tr>
                  <td colSpan="10" style={{textAlign: "center"}}>
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

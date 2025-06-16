import React, {useEffect, useState, useCallback} from "react";
import axios from "axios";
import Sidebar from "../components/Sidebar"; // Sidebar 컴포넌트 임포트
import "../css/CourseListPage.css"; // CSS 파일 임포트

function CourseListPage() {
  const [allCourses, setAllCourses] = useState([]); // 전체 강의 목록을 저장할 상태
  const [myCourses, setMyCourses] = useState([]); // 나의 수강 신청 목록을 저장할 상태
  const [searchField, setSearchField] = useState("id"); // 선택된 검색 필드
  const [searchKeyword, setSearchKeyword] = useState(""); // 입력된 검색 키워드
  const [totalCredits, setTotalCredits] = useState(0); // 총 학점 상태
  const [hasUserSearched, setHasUserSearched] = useState(false); // 사용자가 검색했는지 여부

  // --- API 호출 함수들 ---

  // 나의 수강 목록 및 총 학점을 불러오는 함수
  const fetchMyCourses = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get("http://localhost:8080/api/timetable/view", {
        // 나의 시간표 API
        headers: {Authorization: `Bearer ${token}`},
      });
      setMyCourses(response.data);

      // 총 학점 계산
      const calculatedCredits = response.data.reduce((sum, course) => sum + (course.credits || 0), 0);
      setTotalCredits(calculatedCredits);
    } catch (error) {
      console.error("나의 수강 목록 불러오기 실패:", error);
      setMyCourses([]); // 실패 시 빈 배열로 초기화
      setTotalCredits(0);
    }
  }, []);

  // 전체 강의 목록을 불러오는 함수 (초기 로드 또는 검색어 없을 때)
  const fetchAllCourses = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get("http://localhost:8080/api/sections/list", {
        // 전체 강의 목록 API (검색 파라미터 없을 때)
        headers: {Authorization: `Bearer ${token}`},
      });
      setAllCourses(response.data);
      setHasUserSearched(false); // 전체 목록 로드 시 검색 상태 초기화
    } catch (error) {
      console.error("전체 강의 목록 불러오기 실패:", error);
      alert("전체 강의 목록을 불러오는 데 실패했습니다.");
      setAllCourses([]); // 오류 발생 시 빈 배열로 설정
    }
  }, []);

  // --- useEffect 훅들 ---

  // 컴포넌트 마운트 시 초기 데이터 로드
  useEffect(() => {
    fetchMyCourses(); // 나의 수강 목록
    fetchAllCourses(); // 전체 강의 목록
  }, [fetchMyCourses, fetchAllCourses]);

  // --- 헬퍼 함수 ---

  // searchField 값에 따라 백엔드 쿼리 파라미터 이름 반환
  const getBackendParamName = (field) => {
    switch (field) {
      case "id":
        return "courseIdNo";
      case "name":
        return "courseName";
      case "credit":
        return "credit";
      case "major":
        return "major";
      case "professor":
        return "professorName";
      default:
        return "";
    }
  };

  // --- 이벤트 핸들러 함수들 ---

  // 검색 폼 제출 핸들러
  const handleSearch = async (e) => {
    e.preventDefault(); // 폼 기본 제출 동작 방지

    setHasUserSearched(true); // 사용자가 검색을 시도했음을 표시

    if (!searchKeyword.trim()) {
      // 검색어가 비어있을 경우 전체 목록을 다시 불러옵니다.
      fetchAllCourses();
      return;
    }

    try {
      const paramName = getBackendParamName(searchField);
      let paramValue = searchKeyword;

      // 학점 검색 시 숫자로 변환 (필수)
      if (searchField === "credit") {
        const parsedCredit = parseInt(searchKeyword, 10);
        if (isNaN(parsedCredit)) {
          alert("학점은 숫자로 입력해주세요.");
          setAllCourses([]); // 잘못된 입력 시 목록 비우기
          return;
        }
        paramValue = parsedCredit;
      }

      const params = {
        [paramName]: paramValue,
      };

      // 백엔드 API의 /api/sections/list 엔드포인트로 요청 (검색 파라미터 포함)
      const res = await axios.get("http://localhost:8080/api/sections/list", {
        params: params,
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      setAllCourses(res.data);
    } catch (err) {
      console.error("강의 검색 실패:", err);
      alert("강의 검색 중 오류가 발생했습니다.");
      setAllCourses([]); // 검색 실패 시 목록 비우기
    }
  };

  // 수강 신청 핸들러
  const handleRegister = async (course) => {
    const {sectionId, credit} = course;

    try {
      const isAlreadyEnrolled = myCourses.some((myCourse) => myCourse.sectionId === sectionId);
      if (isAlreadyEnrolled) {
        alert("이미 신청한 과목입니다.");
        return;
      }

      // 백엔드 API 호출 (EnrollResponseDto를 반환)
      const enrollRes = await axios.post(
        "http://localhost:8080/api/subject/apply", // 수강 신청 API 엔드포인트
        sectionId, // @RequestBody Long sectionId로 직접 전송
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      // EnrollResponseDto에서 메시지와 여석 정보 추출
      const responseData = enrollRes.data; // 백엔드에서 EnrollResponseDto를 직접 반환
      alert(responseData.message); // 백엔드 프로시저에서 반환된 결과 메시지 알림

      // ✅ 수강 신청 성공 시: 나의 수강 목록과 전체 강의 목록 모두 업데이트
      await fetchMyCourses(); // 나의 수강 목록 업데이트 (총 학점 재계산 포함)

      // 현재 검색 상태에 따라 전체 목록을 다시 불러오거나 검색 결과를 재갱신하여 여석 반영
      if (hasUserSearched && searchKeyword.trim()) {
        await handleSearch({preventDefault: () => {}}); // 현재 검색어로 다시 검색
      } else {
        await fetchAllCourses(); // 전체 목록 다시 로드
      }
    } catch (err) {
      console.error("수강 신청 실패:", err);
      if (err.response && err.response.data && err.response.data.message) {
        // 백엔드 컨트롤러에서 반환한 EnrollResponseDto의 message 필드를 사용
        alert("수강 신청 실패: " + err.response.data.message);
      } else {
        // 프로시저 자체 오류나 네트워크 오류 등
        alert("수강 신청 실패: 알 수 없는 오류가 발생했습니다.");
      }
    }
  };

  // 수강 취소 핸들러
  const handleCancel = async (course) => {
    const {courseIdNo, section} = course; // DTO에서 section은 Integer 타입

    try {
      const token = localStorage.getItem("token");
      const res = await axios.delete("http://localhost:8080/api/subject/cancel", {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        data: {
          // DELETE 요청의 경우 body는 'data' 속성에 넣어야 함
          courseIdNo: courseIdNo,
          sectionNo: section, // 백엔드 DeleteRequestDto의 sectionNo 필드명에 맞춰 전송
        },
      });
      alert(res.data); // 취소 성공 메시지

      // ✅ 수강 취소 성공 시: 나의 수강 목록과 전체 강의 목록 모두 업데이트
      await fetchMyCourses(); // 나의 수강 목록 업데이트 (총 학점 재계산 포함)

      // 현재 검색 상태에 따라 전체 목록을 다시 불러오거나 검색 결과를 재갱신하여 여석 반영
      if (hasUserSearched && searchKeyword.trim()) {
        await handleSearch({preventDefault: () => {}}); // 현재 검색어로 다시 검색
      } else {
        await fetchAllCourses(); // 전체 목록 다시 로드
      }
    } catch (err) {
      console.error("수강 취소 실패:", err);
      if (err.response && err.response.data) {
        alert("수강 취소 실패: " + err.response.data); // 백엔드에서 반환하는 String 메시지
      } else {
        alert("수강 취소 실패: 알 수 없는 오류가 발생했습니다.");
      }
    }
  };

  return (
    <div className="course-grid">
      <Sidebar /> {/* Sidebar 컴포넌트 */}
      <main className="main-content">
        <h2>나의 수강 신청 내역</h2>
        <div className="course-table">
          {" "}
          {/* 클래스명 기존과 통일 */}
          <table>
            <thead>
              <tr>
                <th>과목번호</th>
                <th>과목명</th>
                <th>분반</th>
                <th>주관학부</th> {/* 주관학부 추가 */}
                <th>학점</th>
                <th>강사</th>
                <th>강의시간</th>
                <th>강의실</th>
                <th>수강 취소</th>
              </tr>
            </thead>
            <tbody>
              {myCourses.length > 0 ? (
                myCourses.map((course) => (
                  <tr key={course.sectionId}>
                    {" "}
                    {/* key는 고유한 sectionId 사용 */}
                    <td>{course.courseIdNo}</td>
                    <td>{course.courseName}</td>
                    <td>{course.section}</td>
                    <td>{course.major}</td> {/* 주관학부 값 */}
                    <td>{course.credits || "-"}</td>
                    <td>{course.professorName}</td>
                    <td>{course.time}</td>
                    <td>{course.classroom}</td>
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
            총 신청 과목수: {myCourses.length} / 총 신청 학점: {totalCredits} 학점 (최대 18학점)
          </div>
        </div>
        <hr /> {/* 구분선 */}
        <h2>수강 신청</h2>
        <form className="filter-section" onSubmit={handleSearch}>
          <select value={searchField} onChange={(e) => setSearchField(e.target.value)}>
            <option value="id">과목번호</option>
            <option value="name">과목명</option>
            <option value="credit">학점</option>
            <option value="major">학과</option>
            <option value="professor">강사</option>
          </select>
          <input
            type="text"
            placeholder="검색어 입력"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
          <button type="submit">검색</button>
          <button
            type="button"
            onClick={() => {
              setSearchKeyword("");
              setHasUserSearched(false);
              fetchAllCourses(); // 검색 초기화 시 전체 목록 다시 불러오기
            }}
          >
            전체 목록
          </button>
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
              {allCourses.length > 0 ? (
                allCourses.map((course) => (
                  <tr key={course.sectionId}>
                    {" "}
                    {/* key는 고유한 sectionId 사용 */}
                    <td>{course.courseIdNo}</td>
                    <td>{course.courseName}</td>
                    <td>{course.section}</td>
                    <td>{course.major}</td>
                    <td>{course.credit}</td>
                    <td>{course.professorName}</td>
                    <td>{course.classTime}</td>
                    <td>{course.classroom}</td>
                    <td>
                      {/* remainingCapacity가 undefined이면 로딩 중이거나 오류로 간주 */}
                      {course.remainingCapacity !== undefined
                        ? `${course.remainingCapacity}/${course.capacity}`
                        : "N/A"}
                    </td>
                    <td>
                      <button
                        onClick={() => handleRegister(course)}
                      >
                        신청
                      </button>
                    </td>
                  </tr>
                ))
              ) : hasUserSearched ? ( // 사용자가 검색했으나 결과가 없을 때
                <tr>
                  <td colSpan="10" style={{textAlign: "center"}}>
                    검색 결과가 없습니다.
                  </td>
                </tr>
              ) : (
                // 초기 상태 (검색 전) 또는 데이터 로딩 중
                <tr>
                  <td colSpan="10" style={{textAlign: "center"}}>
                    강의 목록을 불러오는 중입니다...
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

// import logo from "./logo.svg";
import "./App.css";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import axios from "axios";
import {useEffect} from "react";

import Header from "./components/Header";
import PasswordResetModal from "./components/PasswordResetModal";
import ProtectedRoute from "./components/ProtectedRoute";

import LoginPage from "./pages/LoginPage";
import MyPage from "./pages/MyPage";
import CourseListPage from "./pages/CourseListPage";
import TimetablePage from "./pages/TimetablePage";

function App() {
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    }
  }, []); // 처음 렌더링 될 때 딱 1번만 실행

  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route
          path="/mypage"
          element={
            <ProtectedRoute>
              <MyPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/subject"
          element={
            <ProtectedRoute>
              <CourseListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/reset-password"
          element={
            <ProtectedRoute>
              <PasswordResetModal />
            </ProtectedRoute>
          }
        />
        <Route
          path="/timetable"
          element={
            <ProtectedRoute>
              <TimetablePage />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
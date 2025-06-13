import React, {useState} from "react";
import {Link, useLocation} from "react-router-dom";
import "../css/Components.css";

function Sidebar() {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation();

  return (
    <>
      {/* 접힘 상태에서도 항상 화면에 떠 있는 캡슐 버튼 */}
      {collapsed && (
        <button
          className="sidebar-capsule-toggle"
          onClick={() => setCollapsed(false)}
          aria-label="사이드바 열기"
        ></button>
      )}

      <div className={`sidebar-wrapper ${collapsed ? "collapsed" : ""}`}>
        <aside className="sidebar">
          {!collapsed && (
            <>
              {/* 닫기 버튼 */}
              <button
                className="sidebar-toggle inside-toggle"
                onClick={() => setCollapsed(true)}
                aria-label="사이드바 닫기"
              >
                ◀
              </button>

              <h3 className="sidebar-title">메뉴</h3>
              <ul className="sidebar-menu">
                <li className={location.pathname === "/mypage" ? "active" : ""}>
                  <Link to="/mypage">내 정보</Link>
                </li>
                <li className={location.pathname === "/subject" ? "active" : ""}>
                  <Link to="/subject">수강 신청</Link>
                </li>
                <li className={location.pathname === "/timetable" ? "active" : ""}>
                  <Link to="/timetable">수강 타임 테이블</Link>
                </li>
              </ul>
            </>
          )}
        </aside>
      </div>
    </>
  );
}

export default Sidebar;

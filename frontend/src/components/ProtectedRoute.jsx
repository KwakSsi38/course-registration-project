// src/components/ProtectedRoute.jsx

import {Navigate} from "react-router-dom";
import {isLoggedIn} from "../utils/auth";

const ProtectedRoute = ({children}) => {
  return isLoggedIn() ? children : <Navigate to="/" replace />;
};

export default ProtectedRoute;
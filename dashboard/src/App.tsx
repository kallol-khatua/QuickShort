import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

// Layouts
import DashboardLayout from "./layout/DashboardLayout";

// Public routes
import Signin from "./pages/AuthPages/Signin";

// Protected routes
import Home from "./pages/Home";

function App() {
  return (
    <>
      <Router>
        {/* <ScrollToTop /> */}
        <Routes>
          {/* Dashboard Layout */}
          <Route element={<DashboardLayout />}>
            <Route index path="/" element={<Home />} />
          </Route>

          {/* Auth Layout */}
          <Route path="/signin" element={<Signin />} />
          <Route path="/signup" element={<Home />} />

          {/* Fallback Route */}
          {/* <Route path="*" element={<NotFound />} /> */}
        </Routes>
      </Router>
    </>
  );
}

export default App;

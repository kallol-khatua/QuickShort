import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

// Layouts
import DashboardLayout from "./layout/DashboardLayout";
import { ScrollToTop } from "./components/common/ScrollToTop";

// Public routes
import Signin from "./pages/AuthPages/Signin";

// Protected routes
import Home from "./pages/Home";
import ProtectedRoutePages from "./pages/ProtectedRoutePages";
import LinksPage from "./pages/Links/LinksPage";
import CurrentWorkspacehandler from "./components/CurrentWorkspaceHandler";
import Upgrade from "./pages/Payments/Upgrade";

function App() {
  return (
    <>
      <Router>
        <ScrollToTop />
        <Routes>
          <Route path="/error" element={<div>error page</div>} />

          {/* Protected routes */}
          <Route element={<ProtectedRoutePages />}>
            <Route path="/onbording" element=<div>skds</div> />
          </Route>

          {/* Dashboard Layout */}
          <Route element={<DashboardLayout />}>
            <Route index path="/" element={<Home />} />
            <Route element={<CurrentWorkspacehandler />}>
              <Route index path="/:workspaceId/" element={<LinksPage />} />
              <Route index path="/:workspaceId/upgrade" element={<Upgrade />} />
              <Route path="/:workspaceId/analytics" element={<Signin />} />
              <Route path="/:workspaceId/settings" element={<Signin />} />
              <Route
                path="/:workspaceId/settings/billing"
                element={<Signin />}
              />
            </Route>
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

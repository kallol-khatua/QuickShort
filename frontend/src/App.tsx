import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ScrollToTop } from "./components/common/ScrollToTop";
import ShortCode from "./pages/ShortCode/ShortCode";
import HomePage from "./pages/Home/Home";

function App() {
  return (
    <>
      <Router>
        <ScrollToTop />
        <Routes>
          <Route path="/" element={<HomePage/>} />

          <Route path="/error" element={<div>error page</div>} />

          <Route path="/:shortCode" element={<ShortCode />} />

          {/* Fallback Route */}
          {/* <Route path="*" element={<NotFound />} /> */}
        </Routes>
      </Router>
    </>
  );
}

export default App;

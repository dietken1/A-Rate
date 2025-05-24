import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import GoogleCallback from "./pages/GoogleCallback";

const Router = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/auth/google/callback" element={<GoogleCallback />} />
    </Routes>
  </BrowserRouter>
);

export default Router;

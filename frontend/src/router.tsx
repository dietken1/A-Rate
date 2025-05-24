import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import Auth from "./pages/Auth";
import GoogleCallback from "./pages/GoogleCallback";

const Router = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/auth" element={<Auth />} />
      <Route path="/auth/google/callback" element={<GoogleCallback />} />
    </Routes>
  </BrowserRouter>
);

export default Router;

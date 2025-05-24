import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import Rating from "./pages/Rating";
import RatingDetail from "./pages/RatingDetail";
import TimeTable from "./pages/TimeTable";
import Resource from "./pages/Resource";
import Mentoring from "./pages/Mentoring";
import Activity from "./pages/Activity";
import GoogleCallback from "./pages/GoogleCallback";

const Router = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/rating" element={<Rating />} />
      <Route path="/google-auth" element={<GoogleCallback />} />
      <Route path="/rating/detail" element={<RatingDetail />} />
      <Route path="/timetable" element={<TimeTable />} />
      <Route path="/resource" element={<Resource />} />
      <Route path="/mentoring" element={<Mentoring />} />
      <Route path="/activity" element={<Activity />} />
    </Routes>
  </BrowserRouter>
);

export default Router;

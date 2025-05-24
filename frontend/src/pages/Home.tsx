import Sidebar from "../components/Sidebar";

const Home = () => {
  return (
    <div className="flex h-screen w-full h-full bg">
      <Sidebar />
      <div className="flex-1 overflow-auto m-3">
        <div>000님</div>
      </div>
    </div>
  );
};

export default Home;

import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import Header from './components/common/Header';
import Footer from './components/common/Footer';
import Uptime from './pages/Uptime';
import GoogleAuthCallback from './pages/GoogleAuthCallback';

const App: React.FC = () => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

  useEffect(() => {
    // 로컬 스토리지에서 인증 상태 확인
    const authStatus = localStorage.getItem('isAuthenticated') === 'true';
    setIsAuthenticated(authStatus);
  }, []);

  return (
    <Router>
      <div className="App">
        <Header isAuthenticated={isAuthenticated} />
        <main className="main-content">
          <Routes>
            <Route path="/" element={
              <div className="home-content">
                <h1>A-Rate에 오신 것을 환영합니다</h1>
                <p>시작하려면 로그인하세요</p>
              </div>
            } />
            <Route path="/uptime" element={<Uptime />} />
            <Route path="/google-auth" element={<GoogleAuthCallback />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </Router>
  );
}

export default App;

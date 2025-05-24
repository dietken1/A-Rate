import React from 'react';
import { Link } from 'react-router-dom';
import './Header.css';

interface HeaderProps {
    isAuthenticated: boolean;
}

const Header: React.FC<HeaderProps> = ({ isAuthenticated }) => {
    const handleLogin = (): void => {
        // 로컬 백엔드로 로그인 요청 (스프링의 OAuth2 로그인 엔드포인트 사용)
        const backendUrl = 'http://localhost:8080/oauth2/authorize/google?redirect_environment=local';
        console.log('백엔드 OAuth2 로그인 엔드포인트로 리디렉션:', backendUrl);
        
        // 구글 로그인 페이지로 리디렉션
        window.location.href = backendUrl;
    };

    const handleLogout = (): void => {
        // 로그아웃 처리
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('isAuthenticated');
        window.location.reload();
    };

    return (
        <header className="header">
            <div className="logo">
                <Link to="/">A-Rate</Link>
            </div>
            <nav className="nav">
                <ul>
                    <li><Link to="/">홈</Link></li>
                    <li><Link to="/uptime">업타임</Link></li>
                    {/* 추가 메뉴 항목 */}
                </ul>
            </nav>
            <div className="auth-buttons">
                {isAuthenticated ? (
                    <button onClick={handleLogout} className="btn-logout">로그아웃</button>
                ) : (
                    <button onClick={handleLogin} className="btn-login">로그인 (로컬전용)</button>
                )}
            </div>
        </header>
    );
};

export default Header; 
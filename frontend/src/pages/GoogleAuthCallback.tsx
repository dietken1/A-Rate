import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const GoogleAuthCallback: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        console.log('GoogleAuthCallback 컴포넌트가 마운트되었습니다.');
        console.log('현재 URL:', location.search);
        
        try {
            const query = new URLSearchParams(location.search);
            const token = query.get('token');
            const refreshToken = query.get('refreshToken');
            const errorMsg = query.get('error');

            console.log('파라미터 확인:', { token: !!token, refreshToken: !!refreshToken, error: errorMsg });

            if (errorMsg) {
                // 에러가 있을 경우 처리
                console.error('로그인 오류:', errorMsg);
                setError(errorMsg);
                setLoading(false);
                // 3초 후 홈으로 리디렉션
                setTimeout(() => {
                    navigate('/');
                }, 3000);
            } else if (token && refreshToken) {
                // 토큰 저장
                localStorage.setItem('accessToken', token);
                localStorage.setItem('refreshToken', refreshToken);
                
                // 인증 상태 저장
                localStorage.setItem('isAuthenticated', 'true');
                
                // 홈 페이지로 리디렉션
                setLoading(false);
                navigate('/');
            } else {
                // 토큰이 없는 경우 처리
                console.error('토큰을 받지 못했습니다');
                setError('인증 정보를 받지 못했습니다.');
                setLoading(false);
                setTimeout(() => {
                    navigate('/');
                }, 3000);
            }
        } catch (e) {
            console.error('GoogleAuthCallback 처리 중 오류 발생:', e);
            setError('인증 처리 중 오류가 발생했습니다.');
            setLoading(false);
            setTimeout(() => {
                navigate('/');
            }, 3000);
        }
    }, [navigate, location.search]);

    return (
        <div className="auth-callback-container" style={{ 
            padding: '2rem',
            maxWidth: '600px',
            margin: '0 auto',
            textAlign: 'center' 
        }}>
            {loading ? (
                <p>인증 처리 중입니다...</p>
            ) : error ? (
                <div className="error-message" style={{
                    backgroundColor: '#ffebee',
                    border: '1px solid #f44336',
                    borderRadius: '4px',
                    padding: '1rem',
                    marginBottom: '1rem'
                }}>
                    <h3 style={{ color: '#d32f2f', marginTop: 0 }}>로그인 오류</h3>
                    <p>{error}</p>
                    <p>3초 후 홈 페이지로 이동합니다...</p>
                </div>
            ) : (
                <p>인증이 완료되었습니다. 홈 페이지로 이동합니다...</p>
            )}
        </div>
    );
};

export default GoogleAuthCallback; 
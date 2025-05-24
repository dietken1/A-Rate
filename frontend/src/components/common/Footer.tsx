import React from 'react';
import './Footer.css';

const Footer: React.FC = () => {
    return (
        <footer className="footer">
            <div className="footer-content">
                <p>&copy; 2023 A-Rate. All rights reserved.</p>
                <p>아주대학교 강의평가 시스템</p>
            </div>
        </footer>
    );
};

export default Footer; 
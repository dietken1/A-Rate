import React from 'react';
import ReactDOM from 'react-dom/client'; // React 18 API 사용
import './index.css';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root')); // createRoot로 변경
root.render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);
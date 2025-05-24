import react, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Header from './components/common/Header';
import Footer from './components/common/Footer'
import Uptime from './pages/Uptime';

import './App.css';

function App(){
    const [userId, setUserId] = useState();

    return (
        <Router>
            <div className="App">
                <Header userId={userId} />
                <div id="main-content">
                    <Routes>
                        <Route path="/uptime" element={<Uptime />} />
                    </Routes>
                </div>
                <Footer />
            </div>
        </Router>
    );
}

export default App;
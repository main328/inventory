import { HashRouter, Routes, Route } from 'react-router-dom';

import Join from './component/Join';
import Login from './component/Login';
import Dashboard from './component/Dashboard';

import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

function App() {
    return (
        <div className="App">
            <HashRouter>
                <Routes>
                    <Route path="/" element={<Login></Login>}></Route>
                    <Route path="/join" element={<Join></Join>}></Route>
                    <Route path="/dashboard" element={<Dashboard></Dashboard>}></Route>
                </Routes>
            </HashRouter>
        </div>
    );
}

export default App;

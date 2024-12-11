import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import axios from 'axios';
import '../css/Login.css';

// React-Bootstrap.
import Stack from 'react-bootstrap/Stack';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Button from 'react-bootstrap/Button';

export default function Login() {
    const navigate = useNavigate();
    const [user, setUser] = useState(true);

    // 현재 로그인 상태 확인.
    useEffect(() => {
        if (user) {
            axios.get("/api/auth/status").then((response) => {
                if(response.status === 200){
                    navigate("/dashboard");
                }}).catch((error)=>{
                    setUser(false);
                    return;
                })
    }},[]);

    return (
        <Stack gap={2} className="col-md-3 mx-auto">
            <div id="login-form-style">
                <InputGroup className="mb-3">
                    <Form.Control id="loginUsername" type="text" placeholder="아이디"/>
                </InputGroup>
                <InputGroup className="mb-3">
                    <Form.Control id="loginPassword" type="password" placeholder="비밀번호"/>
                </InputGroup>
                <div className="mb-3 d-grid">
                    <Button variant="dark" onClick={()=>{
                        let username = document.querySelector("#loginUsername").value;
                        let password = document.querySelector("#loginPassword").value;

                        // 항목의 입력 및 최대길이 확인.
                        if (username === "" || password === "") {
                            alert("입력하지 않은 항목이 있습니다.");
                            return;
                        } else if (username.length > 255 || password.length > 255) {
                            alert("최대 입력길이인 255자리를 초과했습니다.");
                            return;
                        }

                        // Back-end 로그인 연동.
                        axios.post("/api/auth/login", {
                            username: username,
                            password: password
                        }).then((response)=>{
                            if (response.status === 200) {
                                navigate("/dashboard");
                            }}).catch((error)=>{
                            alert("회원가입 후 가능합니다.");
                            return;
                        })
                    }}>로그인</Button>
                </div>
                <div className="d-grid">
                    <Button variant="dark" onClick={()=>{navigate("/join");}}>회원가입</Button>
                </div>
            </div>
        </Stack>
    );
}

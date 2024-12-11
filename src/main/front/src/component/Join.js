import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import axios from 'axios';
import '../css/Join.css';

// React-Bootstrap.
import Stack from 'react-bootstrap/Stack';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Button from 'react-bootstrap/Button';

export default function Join() {
    const navigate = useNavigate();
    const [user, setUser] = useState(true);
    const [duplicate, setDuplicate] = useState(0);

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
            <div id="join-form-style">
                <InputGroup className="mb-3">
                    <Form.Control id="joinUsername" type="text" placeholder="아이디"/>
                    <Button id="joinDuplicate" variant="outline-secondary" onClick={()=>{
                        let username = document.querySelector("#joinUsername").value;

                        // 아이디 입력 및 최대길이 확인.
                        if (username === "") {
                            alert("아이디를 입력하지 않았습니다.");
                            return;
                        } else if (username.length > 255) {
                            alert("최대 입력길이인 255자리를 초과했습니다.");
                            return;
                        }

                        // Back-end 아이디 중복 확인 연동.
                        axios.post("/api/auth/duplicate", {
                            username: username,
                        }).then((response)=>{
                            if (response.status === 200) {
                                alert("사용 가능한 아이디입니다.");
                                setDuplicate(1);
                            }}).catch((error)=>{
                                alert("중복된 아이디입니다.");
                                    return;
                        })}}>중복확인</Button>
                </InputGroup>
                <InputGroup className="mb-3">
                    <Form.Control id="joinPassword" type="password" placeholder="비밀번호"/>
                </InputGroup>
                <InputGroup className="mb-3">
                    <Form.Control id="joinPasswordCheck" type="password" placeholder="비밀번호 확인"/>
                </InputGroup>
                <InputGroup className="mb-3">
                    <Form.Control id="joinNickname" type="text" placeholder="닉네임"/>
                </InputGroup>
                <InputGroup className="mb-3">
                    <Form.Control id="joinEmail" type="email" placeholder="이메일"/>
                </InputGroup>
                <InputGroup className="mb-3">
                    <Form.Control id="joinBirth" type="text" placeholder="생년월일"
                        onMouseOver={(e)=>{ e.currentTarget.type = "date"; }}
                        onMouseLeave={(e)=>{ e.currentTarget.type = "text"; }}/>
                </InputGroup>
                <InputGroup className="mb-3">
                    <Form.Control id="joinPhone" type="text" placeholder="전화번호"/>
                </InputGroup>
                <InputGroup className="mb-3">
                    <Form.Control id="joinAddress" type="text" placeholder="주소"/>
                </InputGroup>
                <div className="d-grid">
                    <Button variant="dark" onClick={()=>{
                        // Account 항목.
                        let username = document.querySelector("#joinUsername").value;
                        let password = document.querySelector("#joinPassword").value;
                        let passwordCheck = document.querySelector("#joinPasswordCheck").value;
                        // Profile 항목.
                        let nickname = document.querySelector("#joinNickname").value;
                        let email = document.querySelector("#joinEmail").value;
                        let birth = document.querySelector("#joinBirth").value;
                        let phone = document.querySelector("#joinPhone").value;
                        let address = document.querySelector("#joinAddress").value;

                        // 입력 항목이 정확한지 확인.
                        if (username === "" || password === "" || passwordCheck === "" || nickname === "" || email === "" || birth === "" || phone === "" || address === "") {
                            alert("입력하지 않은 항목이 있습니다.");
                            return;
                        }
                        if (username.length > 255 || password.length > 255 || passwordCheck.length > 255) {
                            alert("최대 입력길이인 255자리를 초과했습니다.");
                            return;
                        }
                        if (nickname.length > 50) {
                            alert("최대 입력길이인 50자리를 초과했습니다.");
                            return;
                        }
                        if (email > 320) {
                            alert("최대 입력길이인 320자리를 초과했습니다.");
                            return;
                        }
                        if (birth > 10) {
                            alert("최대 입력길이인 10자리를 초과했습니다.");
                            return;
                        }
                        if (phone > 15) {
                            alert("최대 입력길이인 15자리를 초과했습니다.");
                            return;
                        }
                        if (address > 100) {
                            alert("최대 입력길이인 255자리를 초과했습니다.");
                            return;
                        }

                        // 아이디 중복 확인을 진행했는지 확인.
                        if (duplicate === 0) {
                            alert("아이디 중복 확인 후 가능합니다.");
                            return;
                        }

                        // 비밀번호 및 비밀번호 확인 항목 내용이 일치하는지 확인.
                        if (password !== passwordCheck) {
                            alert("비밀번호가 일치하지 않습니다.");
                            return;
                        }

                        // Back-end 회원가입 연동.
                        axios.post("/api/auth/join", {
                            username: username,
                            password: password,
                            nickname: nickname,
                            email: email,
                            birth: birth,
                            phone: phone,
                            address: address
                        }).then((response)=>{
                            if (response.status === 200) {
                                alert("회원가입되었습니다.");
                                setDuplicate(0);
                                navigate("/");
                            }}).catch((error)=>{
                                alert("회원가입에 실패했습니다.");
                                return;
                            })}}>회원가입</Button>
                </div>
            </div>
        </Stack>
    );
}

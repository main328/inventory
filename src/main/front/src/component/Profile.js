import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import axios from 'axios';

import Accordion from 'react-bootstrap/Accordion';
import Modal from 'react-bootstrap/Modal';
import Tab from 'react-bootstrap/Tab';
import Tabs from 'react-bootstrap/Tabs';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Button from 'react-bootstrap/Button';

export default function Logout() {
    const navigate = useNavigate();
    const [updateCheck, setUpdateCheck] = useState(0);
    const [resignCheck, setResignCheck] = useState(0);
    // 사용자 데이터 저장.
    const [user, setUser] = useState(true);
    const [profile, setProfile] = useState("");

    // 회원수정 Modal 설정.
    const [updateModal, setUpdateModal] = useState(false);
    const updateModalClose = () => setUpdateModal(false);

    // 비밀번호 변경 Modal 설정.
    const [resetModal, setResetModal] = useState(false);
    const resetModalClose = () => setResetModal(false)

    // 입력 항목 문자열 초기화.
    const [textVerify, setTextVerify] = useState("");
    const [textPassword, setTextPassword] = useState("");
    const [textPasswordCheck, setTextPasswordCheck] = useState("");
    const [textNickname, setTextNickname] = useState("");
    const [textEmail, setTextEmail] = useState("");
    const [textBirth, setTextBirth] = useState("");
    const [textPhone, setTextPhone] = useState("");
    const [textAddress, setTextAddress] = useState("");

    useEffect(()=>{
        if (user) {
            axios.get("/api/auth/status").then((response)=>{
                if (response.status === 200) {
                    setProfile(response.data);
                    setUser(false);

                    // 90일이 지나면 비밀번호 재설정.
                    let today = new Date();
                    let old_day = new Date(response.data.account?.resetdate);
                    let new_day = (today-old_day)/24/60/60/1000;

                    if (new_day >= 90) {
                        // 비밀번호 변경 Modal.
                        setResetModal(true);
                    }                    
                }}).catch((error)=>{
                    alert("서비스 이용에 오류가 발생했습니다.");
                    navigate("/");
                })}
    });
    
    return (
        <div>
            <div>
                <Modal show={resetModal} onHide={resetModalClose} animation={false}>
                    <Modal.Header closeButton>
                        <Modal.Title>비밀번호 변경</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                    <Form.Label>현재 90일 동안 동일한 비밀번호를 사용 중입니다.<br/>개인정보 보호를 위해 비밀번호를 변경하시기 바랍니다.</Form.Label>
                    <InputGroup className="mb-3">
                        <Form.Control id="currentPassword" type="password" placeholder="현재 비밀번호" value={ textVerify } onChange={(e)=>{ setTextVerify(e.target.value) }}/>
                        <Button variant="outline-secondary" onClick={()=>{
                            let password = document.querySelector("#currentPassword").value;

                            // 비밀번호 항목 입력 확인.
                            if (password === "") {
                                alert("비밀번호를 입력하지 않았습니다.");
                                return;
                            }
                            if (password.length > 255) {
                                alert("최대 입력길이인 255자리를 초과했습니다.");
                                return;
                            }
                            
                            // Back-end 비밀번호 검증 연동.
                            axios.post("/api/auth/verify", {
                                password: password
                                }).then((response)=>{
                                if (response.status === 200) {
                                    alert("인증되었습니다.");
                                    setUpdateCheck(1);
                                }}).catch((error)=>{
                                    alert("인증되지 않았습니다.");
                                    setUpdateCheck(0);
                            })}}>인증</Button>
                    </InputGroup>
                    <InputGroup className="mb-3">
                        <Form.Control id="resetPassword" type="password" placeholder="신규 비밀번호" value={ textPassword } onChange={(e)=>{ setTextPassword(e.target.value) }}/>
                    </InputGroup>
                    <InputGroup className="mb-3">
                        <Form.Control id="resetPasswordCheck" type="password" placeholder="신규 비밀번호 확인" value={ textPasswordCheck } onChange={(e)=>{ setTextPasswordCheck(e.target.value) }}/>
                    </InputGroup>
                    </Modal.Body>
                    <Modal.Footer>
                    <Button variant="secondary" onClick={()=>{
                        let password = document.querySelector("#currentPassword").value;

                        if (updateCheck === 0) {
                            alert("비밀번호 인증 후 가능합니다.");
                            return;
                        }

                        // Back-end 회원가입 연동.
                        axios.patch("/api/auth/update", {
                            password: password
                        }).then((response)=>{
                            if (response.status === 200) {
                                alert("비밀번호를 변경하지 않았습니다.");
                                // 입력 항목 문자열 초기화.
                                setTextPassword("");
                                setTextPasswordCheck("");
                                setResetModal(false);
                            }}).catch((error)=>{
                                alert("비밀번호 변경에 실패했습니다.");
                                return;
                            })
                        }}>90일 후 변경</Button>
                        <Button variant="primary" onClick={()=>{
                            // Account 항목.
                            let password = document.querySelector("#resetPassword").value;
                            let passwordCheck = document.querySelector("#resetPasswordCheck").value;

                            if (updateCheck === 0) {
                                alert("비밀번호 인증 후 가능합니다.");
                                return;
                            }

                            // 입력 항목의 내용이 입력되었는지 확인.
                            if (password === "" || passwordCheck === "") {
                                alert("입력하지 않은 항목이 있습니다.");
                                return;
                            }

                            // 입력 항목의 자릿수가 정확한지 확인.
                            if (password.length > 255 || passwordCheck.length > 255) {
                                alert("최대 입력길이인 255자리를 초과했습니다.");
                                return;
                            }
                            
                            // 비밀번호 및 비밀번호 확인 항목 내용이 일치하는지 확인.
                            if (password !== passwordCheck) {
                                alert("비밀번호가 일치하지 않습니다.");
                                return;
                            }

                            // Back-end 회원가입 연동.
                            axios.patch("/api/auth/update", {
                                password: password
                            }).then((response)=>{
                                if (response.status === 200) {
                                    alert("비밀번호를 변경했습니다.");
                                    // 입력 항목 문자열 초기화.
                                    setTextPassword("");
                                    setTextPasswordCheck("");
                                    setResetModal(false);
                                }}).catch((error)=>{
                                    alert("비밀번호 변경에 실패했습니다.");
                                    return;
                                })
                        }}>비밀번호 변경</Button>
                    </Modal.Footer>
                </Modal>
            </div>
            <div className="mb-3 d-grid">
                <Button variant="dark" size="lg" onClick={()=> {
                    axios.post("/api/auth/logout").then((response)=>{
                        if (response.status === 200) {
                            navigate("/");
                        }
                })}}>로그아웃</Button>
            </div>
            <div className="mb-3 d-grid">
                <Accordion>
                    <Accordion.Item eventKey="0">
                        <Accordion.Header>환경설정</Accordion.Header>
                        <Accordion.Body>
                            <Tabs defaultActiveKey="update" className="mb-3">
                                <Tab eventKey="update" title="회원수정">
                                <InputGroup>
                                        <Form.Control id="updatePassword" placeholder="현재 비밀번호" value={ textVerify } onChange={(e)=>{ setTextVerify(e.target.value) }}/>
                                        <Button variant="outline-secondary" onClick={()=>{
                                            let password = document.querySelector("#updatePassword").value;

                                            // 비밀번호 항목 입력 확인.
                                            if (password === "") {
                                                alert("비밀번호를 입력하지 않았습니다.");
                                                return;
                                            }
                                            if (password.length > 255) {
                                                alert("최대 입력길이인 255자리를 초과했습니다.");
                                                return;
                                            }
                                            
                                            // Back-end 비밀번호 검증 연동.
                                            axios.post("/api/auth/verify", {
                                                password: password
                                                }).then((response)=>{
                                                if (response.status === 200) {
                                                    alert("인증되었습니다.");
                                                    setTextVerify("");
                                                    setUpdateCheck(1);
                                                }}).catch((error)=>{
                                                    alert("인증되지 않았습니다.");
                                                    setUpdateCheck(0);
                                                    return
                                            })}}>인증</Button>
                                    </InputGroup>
                                    <div className="d-grid">
                                        <Button variant="outline-warning" onClick={()=>{
                                            // 비밀번호 검증 확인.
                                            if (updateCheck === 0) {
                                                alert("비밀번호 인증 후 가능합니다.");
                                                return;
                                            }
                                            
                                            // 입력 항목 문자열 초기화.
                                            setTextPassword("");
                                            setTextPasswordCheck("");
                                            setTextNickname("");
                                            setTextEmail("");
                                            setTextBirth("");
                                            setTextPhone("");
                                            setTextAddress("");

                                            setUpdateModal(true);
                                            }}>회원수정</Button>
                                    </div>
                                    <Modal show={updateModal} onHide={updateModalClose} animation={false}>
                                        <Modal.Header closeButton>
                                        <Modal.Title>회원정보 수정</Modal.Title>
                                        </Modal.Header>
                                        <Modal.Body>
                                        <InputGroup className="mb-3">
                                            <Form.Control id="updatePassword" type="password" placeholder="신규 비밀번호" value={ textPassword } onChange={(e)=>{ setTextPassword(e.target.value) }}/>
                                        </InputGroup>
                                        <InputGroup className="mb-3">
                                            <Form.Control id="updatePasswordCheck" type="password" placeholder="신규 비밀번호 확인" value={ textPasswordCheck } onChange={(e)=>{ setTextPasswordCheck(e.target.value) }}/>
                                        </InputGroup>
                                        <InputGroup className="mb-3">
                                            <Form.Control id="updateNickname" type="text" placeholder={ profile.nickname } value={ textNickname } onChange={(e)=>{ setTextNickname(e.target.value) }}/>
                                        </InputGroup>
                                        <InputGroup className="mb-3">
                                            <Form.Control id="updateEmail" type="email" placeholder={ profile.email } value={ textEmail } onChange={(e)=>{ setTextEmail(e.target.value) }}/>
                                        </InputGroup>
                                        <InputGroup className="mb-3">
                                            <Form.Control id="updateBirth" type="text" placeholder={ profile.birth } value={ textBirth } onChange={(e)=>{ setTextBirth(e.target.value) }}
                                                onMouseOver={(e)=>{ e.currentTarget.type = "date" }}
                                                onMouseLeave={(e)=>{ e.currentTarget.type = "text" }}/>
                                        </InputGroup>
                                        <InputGroup className="mb-3">
                                            <Form.Control id="updatePhone" type="text" placeholder={ profile.phone } value={ textPhone } onChange={(e)=>{ setTextPhone(e.target.value) }}/>
                                        </InputGroup>
                                        <InputGroup className="mb-3">
                                            <Form.Control id="updateAddress" type="text" placeholder={ profile.address} value={ textAddress } onChange={(e)=>{ setTextAddress(e.target.value) }}/>
                                        </InputGroup>
                                        </Modal.Body>
                                        <Modal.Footer>
                                        <Button variant="secondary" onClick={updateModalClose}>닫기</Button>
                                        <Button variant="primary" onClick={()=>{
                                            // Account 항목.
                                            let password = document.querySelector("#updatePassword").value;
                                            let passwordCheck = document.querySelector("#updatePasswordCheck").value;
                                            // Profile 항목.
                                            let nickname = document.querySelector("#updateNickname").value;
                                            let email = document.querySelector("#updateEmail").value;
                                            let birth = document.querySelector("#updateBirth").value;
                                            let phone = document.querySelector("#updatePhone").value;
                                            let address = document.querySelector("#updateAddress").value;

                                            // 입력 항목이 정확한지 확인.
                                            if (password.length > 255 || passwordCheck.length > 255) {
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
                                                alert("최대 입력길이인 100자리를 초과했습니다.");
                                                return;
                                            }

                                            // 입력 항목의 자릿수가 정확한지 확인.
                                            if (password.length > 255 || passwordCheck.length > 255) {
                                                alert("최대 입력길이인 255자리를 초과했습니다.");
                                                return;
                                            }

                                            // 공백일 경우 null로 변환.
                                            if (password === "" || passwordCheck === "") {
                                                password = null;
                                                passwordCheck = null;
                                            }
                                            if (nickname === "") {
                                                nickname = null;
                                            }
                                            if (email === "") {
                                                email = null;
                                            }
                                            if (birth === "") {
                                                birth = null;
                                            }
                                            if (phone === "") {
                                                phone = null;
                                            }
                                            if (address === "") {
                                                address = null;
                                            }

                                            // Back-end 회원가입 연동.
                                            axios.patch("/api/auth/update", {
                                                password: password,
                                                nickname: nickname,
                                                email: email,
                                                birth: birth,
                                                phone: phone,
                                                address: address
                                            }).then((response)=>{
                                                if (response.status === 200) {
                                                    alert("회원정보를 수정했습니다.");
                                                    // 수정된 개인정보 저장.
                                                    setProfile(response.data);
                                                    setUpdateCheck(0);
                                                    // 입력 항목 문자열 초기화.
                                                    setTextPassword("");
                                                    setTextPasswordCheck("");
                                                    setTextNickname("");
                                                    setTextEmail("");
                                                    setTextBirth("");
                                                    setTextPhone("");
                                                    setTextAddress("");
                                                }}).catch((error)=>{
                                                    alert("회원정보 수정에 실패했습니다.");
                                                    return;
                                                })
                                        }}>수정</Button>
                                        </Modal.Footer>
                                    </Modal>
                                </Tab>
                                <Tab eventKey="resign" title="회원탈퇴">
                                    <InputGroup>
                                        <Form.Control id="resignPassword" placeholder="현재 비밀번호" value={ textVerify } onChange={(e)=>{ setTextVerify(e.target.value) }}/>
                                        <Button variant="outline-secondary" onClick={()=>{
                                            let password = document.querySelector("#resignPassword").value;
                                            
                                            // 비밀번호 항목 입력 확인.
                                            if (password === "") {
                                                alert("비밀번호를 입력하지 않았습니다.");
                                                return;
                                            }
                                            if (password.length > 255) {
                                                alert("최대 입력길이인 255자리를 초과했습니다.");
                                                return;
                                            }
                                            
                                            // Back-end 비밀번호 검증 연동.
                                            axios.post("/api/auth/verify", {
                                                password: password
                                                }).then((response)=>{
                                                if (response.status === 200) {
                                                    alert("인증되었습니다.");
                                                    setTextVerify("");
                                                    setResignCheck(1);
                                                }}).catch((error)=>{
                                                    alert("인증되지 않았습니다.");
                                                    setResignCheck(0);
                                                    return
                                            })}}>인증</Button>
                                    </InputGroup>
                                    <div className="d-grid">
                                        <Button variant="outline-danger" onClick={()=>{
                                            // 비밀번호 검증 확인.
                                            if (resignCheck === 0) {
                                                alert("비밀번호 인증 후 가능합니다.");
                                                return;
                                            }

                                            // Back-end 회원탈퇴 연동.
                                            axios.delete("/api/auth/resign").then((response)=>{
                                                if (response.status === 200) {
                                                    alert("회원탈퇴되었습니다.");
                                                    setTextVerify("");
                                                    setResignCheck(0);
                                                    navigate("/");
                                                } else {
                                                    alert("회원탈퇴에 실패했습니다.");
                                                    return;
                                            }}).catch((error)=>{
                                                alert("서비스 이용에 오류가 발생했습니다.");
                                                return;
                                        })}}>회원탈퇴</Button>
                                    </div>
                                </Tab>
                            </Tabs>
                        </Accordion.Body>
                    </Accordion.Item>
                </Accordion>
            </div>
        </div>
    );
}

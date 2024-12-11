import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import axios from 'axios';
import '../css/Navigation.css';

// React-Bootstrap.
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';

export default function Navigation() {
    const navigate = useNavigate();

    // Modal 상태 확인.
    const [categoryModal, setCategoryModal] = useState(false);
    const categoryModalClose = () => setCategoryModal(false);

    // 입력 항목 문자열 초기화.
    const [textCode, setTextCode] = useState("");
    const [textName, setTextName] = useState("");
    const [textPrice, setTextPrice] = useState("");
    const [textNote, setTextNote] = useState("");

    return (
        <div>
            <Row>
                <Col className="mb-3" md="3">
                    <div className="d-grid">
                        <Button variant="outline-primary" size="lg" onClick={()=>{navigate("/payment")}}>주문결제</Button>
                    </div>
                </Col>
                <Col className="mb-3" md="3">
                    <div className="d-grid">
                        <Button variant="outline-secondary" size="lg" onClick={()=>{navigate("/receipt")}}>주문관리</Button>
                    </div>
                </Col>
                <Col className="mb-3" md="3">
                    <div className="d-grid">
                        <Button variant="outline-secondary" size="lg" onClick={()=>{navigate("/inventory")}}>재고관리</Button>
                    </div>
                </Col>
                <Col className="mb-3" md="3">
                    <div className="d-grid">
                        <Button variant="outline-secondary" size="lg" onClick={()=>{navigate("/dashboard")}}>매출관리</Button>
                    </div>
                </Col>
            </Row>
            <Row>
                <Col className="mb-3" md="2">
                    <div className="d-grid">
                        <Button variant="outline-secondary" onClick={()=>{
                        }}>업체등록</Button>
                    </div>
                </Col>
                <Col className="mb-3" md="2">
                    <div className="d-grid">
                        <Button variant="outline-secondary" onClick={()=>{
                        }}>품목등록</Button>
                    </div>
                </Col>
                <Col className="mb-3" md="2">
                    <div className="d-grid">
                        <Button variant="outline-secondary" onClick={()=>{
                        }}>상품등록</Button>
                    </div>
                </Col>
                <Col className="mb-3" md="2">
                    <div className="d-grid">
                        <Button variant="outline-secondary" onClick={()=>{
                        }}>재고등록</Button>
                    </div>
                </Col>
                <Col className="mb-3" md="2">
                    <div className="d-grid">
                        <Button variant="outline-secondary" onClick={()=>{
                        }}>행사등록</Button>
                    </div>
                </Col>
                <Col className="mb-3" md="2">
                    <div className="d-grid">
                        <Button variant="outline-secondary" onClick={()=>{
                        }}>고객등록</Button>
                    </div>
                </Col>
            </Row>
        </div>
    );
}

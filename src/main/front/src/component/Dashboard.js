import '../css/Dashboard.css';

// Component.
import Navigation from './Navigation';
import Profile from './Profile';

// React-Bootstrap.
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

export default function Dashboard() {
    return (
        <Container id="dashboard-container-style" className="justify-content-md-center">
            <Row>
                <Col className="mb-3" md="9">
                    <Navigation></Navigation>
                </Col>
                <Col className="mb-3" md="3">
                    <Profile></Profile>
                </Col>
            </Row>
        </Container>
    );
}

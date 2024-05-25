import React from "react";
import { Container, Row, Col, Card, Button } from "react-bootstrap";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import "main/pages/css/LoginPage.css";

const LoginCard = () => {
  return (
    <Card className="login-card">
      <Card.Body>
        <Card.Title data-testid="loginPage-cardTitle">Welcome to Organic!</Card.Title>
        <Card.Text>
          To access features, please login with your Github account.
        </Card.Text>
        <Button href="/oauth2/authorization/github" variant="primary" className="login-btn">Log In</Button>
      </Card.Body>
    </Card>
  );
};

export default function LoginPage() {
  return (
    <div className="login-page-background"> 
      <BasicLayout>
        <Container className="login-container"> 
          <Row className="align-items-center justify-content-center"> 
            <Col sm="auto"><LoginCard /></Col>
          </Row>
        </Container>
      </BasicLayout>
    </div>
  );
}

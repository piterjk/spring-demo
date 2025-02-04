<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Access Denied</title>
    <%@ include file="../layout/head.jsp" %>
</head>
<body>

<div class="d-flex" id="wrapper">
    <%@ include file="../layout/sidebar.jsp" %>
    <main class="container">
        <div class="container mt-5">
        <h2>접근 거부됨</h2>
        <p>권한이 없습니다. <br/>관리자에게 문의하세요.</p>
            <div class="d-flex">
            <a href="/" class="me-3">홈으로 이동</a>
            <a href="#" id="history-back">🔙 이전 페이지로 이동</a>
            </div>
        </div>
    </main>
</div>

<script nonce="3e54b9e6-40c0-425f-ac88-8a34dd0adc46">
    document.getElementById("history-back").addEventListener("click", function() {
        history.back();
    });
</script>

<%@ include file="../layout/footer.jsp" %>
</body>
</html>

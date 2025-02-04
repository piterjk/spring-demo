<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="light">
<head>
    <title>게시글 등록</title>
    <%@ include file="../layout/head.jsp" %>
</head>
<body>

<div class="d-flex" id="wrapper">
<%@ include file="../layout/sidebar.jsp" %>
<main class="container">
<div class="container mt-5">
    <div class="card shadow-lg p-4">
        <h2 class="text-center mb-4">게시글 목록</h2>


            <div class="mb-3">
                <table class="table table-striped table-hover table-bordered">
                    <thead class="table-dark">
                    <tr>
                        <th>제목</th>
                        <th>내용</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="post" items="${postList}">
                        <tr>
                            <th>${post.title}</th>
                            <th>${post.content}</th>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>



            <button type="submit" class="btn btn-primary w-100">
                <a href="/post/register" class="text-white">등록</a>
            </button>

    </div>
</div>
</main>
</div>
</body>
</html>

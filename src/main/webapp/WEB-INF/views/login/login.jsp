<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html data-bs-theme="dark">
<head>
    <title>Login</title>
    <%@ include file="../layout/head.jsp" %>
    <link href="/assets/css/login.css" rel="stylesheet">

</head>
<body class="d-flex align-items-center py-4 bg-body-tertiary">


<main class="form-signin w-100 m-auto">

    <h1 class="h3 mb-3 fw-normal">Login</h1>
    <form action="/login" method="post">
        <div class="form-floating">
            <input type="text" id="username" name="username" placeholder="Username" required class="form-control">
            <label for="username">User ID</label>
        </div>
        <div class="form-floating">
            <input type="password" name="password" id="password" placeholder="Password" required class="form-control">
            <label for="password">Password</label>
        </div>

        <button type="submit" class="btn btn-primary w-100 py-2">Login</button>
    </form>
</main>

</body>
</html>

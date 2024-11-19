<template>
  <head>
    <meta charset="UTF-8" />
    <title>Welcome</title>
  </head>
  <body id="loginBody">
    <h3>Login</h3>

    <div>
      <form action="${root}/login" method="post">
        <input type="hidden" name="command" value="login" />
        <label for="fname">아이디</label>
        <input
          type="text"
          id="fname"
          name="memberId"
          placeholder="Your memberId..."
          required="required"
        />

        <label for="lname">패스워드</label>
        <input
          type="password"
          id="lname"
          name="password"
          placeholder="Your password ..."
          required="required"
        />
        <input type="submit" value="로그인" />
      </form>
      <a href="${root}/regist">회원가입</a>
    </div>
  </body>
</template>

<script setup></script>

<style scoped>
#loginBody {
  font-family: "Arial", sans-serif;
  background-color: #f9fcff;
  background-image: linear-gradient(147deg, #f9fcff 0%, #dee4ea 74%);
  background-repeat: repeat-x;
  height: 100%;
  padding: 20px;
  color: #333;
}

h3 {
  font-size: 36px;
  font-weight: bold;
  color: black;
  margin-bottom: 30px;
  text-align: center;
}

div {
  border-radius: 10px;
  background-color: #fff;
  padding: 40px;
  max-width: 500px;
  margin: 0 auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

label {
  font-size: 16px;
  font-weight: bold;
  color: #555;
  display: block;
  margin-bottom: 10px;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 15px;
  margin: 10px 0;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
  font-size: 16px;
  background-color: #f9f9f9;
  transition: all 0.3s ease;
}

input[type="text"]:focus,
input[type="password"]:focus {
  border-color: #833ab4;
  box-shadow: 0 0 10px rgba(131, 58, 180, 0.2);
}

input[type="submit"] {
  width: 100%;
  padding: 15px;
  border: none;
  border-radius: 50px;
  background-image: linear-gradient(45deg, #fcb045, #fd1d1d, #833ab4);
  color: white;
  font-size: 18px;
  font-weight: bold;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.4s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

input[type="submit"]:hover {
  background-position: right center;
  background-color: #fd1d1d;
}

a {
  display: inline-block;
  margin-top: 20px;
  color: #833ab4;
  font-size: 16px;
  text-decoration: none;
  font-weight: bold;
}

a:hover {
  color: #fd1d1d;
}
</style>

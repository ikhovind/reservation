<template>
  <div class="loginDiv">
  <h1> Login </h1>
    <form>
      <div class="container">
        <label>Username : </label>
        <input type="text" placeholder="Enter Username" name="username" required>
        <label>Password : </label>
        <input type="password" placeholder="Enter Password" name="password" required>
        <button type="submit">Login</button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  name: "Login"
}
</script>

<style scoped>

Body {
  font-family: Calibri, Helvetica, sans-serif;
  background-color: pink;
}

button {
  background-color: #93c47d;
  width: 100%;
  padding: 15px;
  margin: 10px 0px;
  border: none;
  cursor: pointer;
}
form {
  border: 3px solid #f1f1f1;
}
input[type=text], input[type=password] {
  width: 100%;
  margin: 8px 0;
  padding: 12px 20px;
  display: inline-block;
  border: 2px solid #93c47d;
  box-sizing: border-box;
}
button:hover {
  opacity: 0.7;
}
.loginDiv {
  width: 50%;
  margin: auto;
}

.container {
  padding: 25px;
  background-color: lightblue;
}

</style>
<template>
  <div class="loginDiv">
    <h1> Login </h1>
    <form>
      <div class="container">
        <p v-if="loginFailed">Login feilet, vennligst prøv igjen</p>
        <label for="userEmail">Username : </label>
        <input type="text" id="userEmail" placeholder="Epost" name="username" required>
        <label for="userPassword">Password : </label>
        <label for="userPassword"></label><input type="password" id="userPassword" placeholder="Passord" name="password" required>
        <button type="submit" v-on:click="login($event)">Login</button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  created() {
  },
  mounted() {
    this.verifyToken();
  },
  data() {
    return {
      loginFailed: false
    }
  },
  name: "Login",
  methods: {
    /**
     * verifies the users login-information
     * @param e event which causes this to be called, is used to prevent reload
     * @returns {Promise<void>}
     */
    async login(e) {
      e.preventDefault();
      const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
          email: document.getElementById("userEmail").value,
          password: document.getElementById("userPassword").value
        })
      };

      await fetch(this.$serverUrl+"/login", requestOptions)
          .then((response) => {
            console.log(response)
            response.json()
                .then(data => {
                  //Then with the data from the response in JSON...
                  if (data.result) {
                    localStorage.setItem("userId", data.userid);
                    localStorage.setItem("token", data.token);
                    localStorage.setItem("userType", data.userType);
                    console.log(document.cookie)
                    this.$emit('login');
                  } else {
                    this.loginFailed = true;
                  }
                })
          })
          //Then if an error is generated...
          .catch((error) => {
            error.toString();
            console.log(error);
          });
    },
    async verifyToken() {
      if (this.$router.currentRoute.path === "/" && localStorage.getItem("token") !== null) {
        const requestOptions = {
          method: 'GET',
          headers: {'Content-Type': 'application/json',
            'token': localStorage.getItem(localStorage.getItem('token'))}};
        await fetch(this.$serverUrl + "/login/verify", requestOptions)
            .then((response) => {
              console.log(response)
              response.json()
                  .then(data => {
                    //Then with the data from the response in JSON...
                    if (data.result) {
                      this.$router.push("reservations");
                    }
                  })
            })
            //Then if an error is generated...
            .catch((error) => {
              error.toString();
              console.log(error);
            });
      }
    }
  }
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
  margin: 10px 0;
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
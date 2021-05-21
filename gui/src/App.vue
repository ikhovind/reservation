<template>
  <div id="app">
    <router-view v-on:login="$router.push('reservations')" ref="routertest"></router-view>
  </div>
</template>

<script>

export default {
  name: 'App',
  components: {

  },
  mounted() {
    this.verifyToken();
  },
  methods: {
    async verifyToken() {
      if (this.$router.currentRoute.path === "/" && localStorage.getItem("token") !== null) {
        const requestOptions = {
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify({
            token: localStorage.getItem("token"),
          })
        };

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

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #393b39;
  background-color: #f6f6f6;
  height: 100%;
  overflow: auto;
}


html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>

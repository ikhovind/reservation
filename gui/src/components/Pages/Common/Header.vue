<template>
  <div class="topnav">
    <router-link to="reserve">Reserver rom</router-link>
    <router-link to="reservations">Mine reservasjoner</router-link>
    <router-link to="rooms">Romoversikt</router-link>
    <router-link to="users" v-if="this.admin">Brukere</router-link>
    <router-link to="userInfo" class="userLogo">
      <img id="userLogo" src="../../../assets/userico.png" alt="Min side">
    </router-link>
  </div>
</template>

<script>
export default {
  name: "Header",
  data() {
    return {
      //todo is needed?
      admin: true,
    }
  }
}
</script>

<style scoped>

#userLink {
  float: right;
}

#userLogo {
  float: right;
  height: 32px;
  padding: 8px;
  margin: 0.75vw 0;
}

.topnav {
  overflow: hidden;
  background-color: #60cd64;
  height: 48px;
  display: flex;
  horiz-align: center;
  align-items: center;
}

.topnav a:not(.userLogo) {
  display: block;
  margin: auto;
  text-decoration: none;
  color: #fcfffc;
  width: 100%;
  font-size: 18px;
  font-weight: bolder;
  height: 48px;
  display: flex;
  justify-content: center;
  align-items: center;
  horiz-align: center;
}

.topnav a:hover {
  background-color: #ddd;
  color: black;
}

.topnav a.active {
  background-color: #a5afaf;
  color: white;
}

</style>
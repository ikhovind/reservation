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
      admin: true,
    }
  }
}
</script>

<style scoped>
  .topnav {
    overflow: hidden;
    background-color: #93c47d;
    height: 48px;
  }

  #userLink {
    float: right;
  }

  #userLogo {
    float: right;
    height: 32px;
    padding: 8px;
  }

  .topnav a{
    float: left;
    color: #fcfffc;
    text-align: center;
    text-decoration: none;
    font-size: 17px;
  }

  .topnav a:not(.userLogo) {
    padding: 14px 16px;
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
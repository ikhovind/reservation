<template>
  <div class="topnav">
    <a href="nrk.no">Reserver rom</a>
    <a href="nrk.no">Mine reservasjoner</a>
    <a href="nrk.no">Romoversikt</a>
    <a id="userLink" href="vg.no" >
      <img id="userLogo" src="../../assets/userico.png" alt="Min side">
    </a>
  </div>
</template>

<script>
export default {
  name: "Header"
}
</script>

<style scoped>
  .topnav {
    overflow: hidden;
    background-color:  #93c47d;
  }

  #userLink {
    float: right;
  }

  #userLogo {
    float: right;
    width: 20%;
  }

  .topnav a {
    float: left;
    color: #f2f2f2;
    text-align: center;
    padding: 14px 16px;
    text-decoration: none;
    font-size: 17px;
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
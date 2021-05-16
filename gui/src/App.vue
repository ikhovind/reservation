<template>
  <div id="app">
    <UserInfo></UserInfo>
  </div>
</template>

<script>

import UserInfo from "@/components/UserInfoPage/UserInfo";
export default {
  name: 'App',
  components: {
    UserInfo
  },
  methods: {
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

<template>
  <div id="app">
    <Header></Header>
    <router-view></router-view>
  </div>
</template>

<script>

import Header from "@/components/Common/Header";
export default {
  name: 'App',
  components: {
    Header,
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

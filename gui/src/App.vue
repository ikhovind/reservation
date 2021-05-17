<template>
  <div id="app">
    <router-view v-on:login="$router.push('reserve')" ref="routertest"></router-view>
  </div>
</template>

<script>

export default {
  name: 'App',
  components: {
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

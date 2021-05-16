<template>
  <div>
    <h2>Min info</h2>
    <label for="firstName">Fornavn</label>
    <input type="text" id="firstName" disabled>
    <label for="surname" >Etternavn</label>
    <input type="text" id="surname" disabled>
    <label for="email">Epost</label>
    <input type="text" id="email" disabled>
    <label for="password">Passord</label>
    <input type="password" id="password" disabled>
    <label for="validUntil">Gyldig til</label>
    <input type="date" id="validUntil">
  </div>
</template>

<script>
export default {
  name: "UserInfo"
}
</script>

<style scoped>

</style>
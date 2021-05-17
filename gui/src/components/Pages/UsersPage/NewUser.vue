<template>
<div>
  <form>
    <label for="firstName">Fornavn</label>
    <input type="text" id="firstName">
    <label for="surname">Etternavn</label>
    <input type="text" id="surname">
    <label for="email">Epost</label>
    <input type="text" id="email">
    <label for="password">Passord</label>
    <input type="text" id="password">
    <label for="passwordRepeat">Gjenta passord</label>
    <input type="text" id="passwordRepeat">
    <p>Administrator?</p>
    <label for="admin">Ja</label>
    <input type="radio" value="false" id="admin">
    <label for="notAdmin">Nei</label>
    <input type="radio" value=false id="notAdmin">

    <label for="validUntil">Gyldig til</label>
    <input type="month" id="validUntil">
  </form>
  <button>Register user</button>
</div>
</template>

<script>
export default {
  name: "NewUser"
}
</script>

<style scoped>

</style>
<template>
  <div>
    <h2>Min info</h2>
    <img src="../../assets/edit.png" alt="Rediger bruker" @click="$refs.editUserModal.displayInput(false)">
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
    <EditUserModal ref="editUserModal"></EditUserModal>
  </div>
</template>

<script>
import EditUserModal from "@/components/Common/EditUserModal";
export default {
  name: "UserInfo",
  components: {EditUserModal}
}
</script>

<style scoped>

</style>
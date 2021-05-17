<template>
  <div>
    <button @click="$refs.editUserModal.displayInput(true)">Ny bruker</button>
    <UsersTable v-on:editUser="$refs.editUserModal.displayInput(false)"></UsersTable>
    <EditUserModal ref="editUserModal"></EditUserModal>
  </div>
</template>

<script>

import UsersTable from "@/components/UsersPage/UsersTable";
import EditUserModal from "@/components/Common/EditUserModal";
export default {
  name: "Users",
  components: {
    EditUserModal,
    UsersTable
  }
}
</script>

<style scoped>

</style>
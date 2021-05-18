<template>
  <div>
    <Header></Header>
    <button @click="$refs.editUserModal.displayInput(true)">Ny bruker</button>
    <UsersTable v-on:editUser="onClickChild"></UsersTable>
    <EditUserModal ref="editUserModal"></EditUserModal>
  </div>
</template>

<script>

import UsersTable from "@/components/Pages/UsersPage/UsersTable";
import EditUserModal from "@/components/Pages/Common/EditUserModal";
import Header from "@/components/Pages/Common/Header";
export default {
  name: "Users",
  components: {
    Header,
    EditUserModal,
    UsersTable
  }, methods: {
    onClickChild(value) {
      this.$refs.editUserModal.displayInput(value)
    }
  }
}
</script>

<style scoped>

</style>
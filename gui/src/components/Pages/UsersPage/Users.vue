<template>
  <div>
    <Header></Header>
    <button @click="emitNewUser">Ny bruker</button>
    <UsersTable v-on:editUser="editUserEmitted" ref="usersTable"></UsersTable>
    <EditUserModal v-on:userEdited="emitUserEdited" ref="editUserModal"></EditUserModal>
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
    editUserEmitted(value) {
      this.$refs.editUserModal.displayInput(value);
    },
    emitNewUser() {
      this.$refs.editUserModal.displayInput({
        newUser: true,
        userType: localStorage.getItem("userType")
      })
    },
    emitUserEdited(uid) {
      this.$refs.usersTable.updateChanged(uid)
    }
  }
}
</script>

<style scoped>

</style>
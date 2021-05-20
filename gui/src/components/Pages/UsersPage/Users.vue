<template>
  <div class="container">
    <Header></Header>
    <UsersTable id="usersTable"
                v-on:editUser="editUserEmitted"
                v-on:newUser="emitNewUser"
                ref="usersTable"></UsersTable>
    <EditUserModal v-on:userEdited="emitUserEdited"
                   v-on:userCreated="this.userCreatedEmitted"
                   ref="editUserModal"></EditUserModal>
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
    userCreatedEmitted(values) {
      this.$refs.usersTable.addNewUser(values);
    },
    emitNewUser() {
      console.log("it was emtied")
      this.$refs.editUserModal.displayInput({
        newUser: true,
        userType: localStorage.getItem("userType")
      })
    },
    emitUserEdited(uid) {
      this.$refs.usersTable.updateChanged(uid)
    },
  }
}
</script>

<style scoped>

.container {
  display: block;
  
}

#usersTable {
  min-width: 250px;
  width: auto;
  min-height: 150px;
}

</style>
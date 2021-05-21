<template>
  <div>
    <Header></Header>
    <div class="content">
      <UsersTable id="usersTable"
                  v-on:editUser="emitEditUser"
                  v-on:newUser="emitNewUser"
                  ref="usersTable"></UsersTable>
      <EditUserModal v-on:userEdited="userEditedEmitted"
                     v-on:userCreated="userCreatedEmitted"
                     ref="editUserModal"></EditUserModal>
    </div>
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
    /**
     * Runs when UsersTable emits "editUser", which signalizes that an admin wants to edit a user.
     * The newUser bool will always be false when set by this method, but the userType and
     * uid will change dependent on the user selected in the UsersTable.
     * @param values newUser: bool, userType: int, uid: String
     */
    emitEditUser(values) {
      this.$refs.editUserModal.displayInput(values);
    },
    /**
     * Does not actually emit anything, but tells the EditUserModal that it should
     * load the form to create a new user by calling it's method directly.
     */
    emitNewUser() {
      this.$refs.editUserModal.displayInput({
        newUser: true,
        userType: localStorage.getItem("userType")
      })
    },
    /**
     * Runs when EditUserModal emits "userCreated", which signalizes that a new
     * user has been created and should be added to the table. The UID of the newly created
     * user is carried through, so that we can get information about this single user
     * instead of refreshing the entire table.
     * @param uid String id of a user
     */
    userCreatedEmitted(uid) {
      this.$refs.usersTable.addNewUser(uid);
    },
    /**
     * Let's the UserTable know that a user was edited from the EditUserModal. Passes on the uid so that we can simply
     * replace the old information with the new, instead of refreshing the whole table.
     * @param uid String id of a user
     */
    userEditedEmitted(uid) {
      this.$refs.usersTable.updateChanged(uid)
    },
  }
}
</script>

<style scoped>

.content {
  width: 100%;
  height: 100%;
}

#usersTable {
  min-width: 250px;
  width: auto;
  min-height: 150px;
}

</style>
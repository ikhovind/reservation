<template>
  <div>
    <Header></Header>
    <NewUser></NewUser>
    <UsersTable></UsersTable>
  </div>
</template>

<script>

import Header from "@/components/Common/Header";
import NewUser from "@/components/UsersPage/NewUser";
import UsersTable from "@/components/UsersPage/UsersTable";
export default {
  name: "Users",
  components: {
    Header,
    NewUser,
    UsersTable
  }
}
</script>

<style scoped>

</style>
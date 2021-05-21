<template>
  <div class="container">
    <input class="form-control" v-model="filters.firstName.value" placeholder="Søk etter fornavn">
    <input class="form-control" v-model="filters.lastName.value" placeholder="Søk etter etternavn">
    <span>
      <smart-pagination
          :current-page.sync="currentPage"
          :total-pages="totalPages"/>
    </span>
    <v-table id="users"
             class="table-hover"
             :data="users"
             :filters="filters"
             :current-page.sync="currentPage"
             :page-size="this.pageSize"
             selection-mode="single"
             selected-class="table-info"
             @totalPagesChanged="totalPages = $event"
             @selectionChanged="selected = $event"
    >
      <thead slot="head">
      <v-th sort-key="firstName">Fornavn</v-th>
      <v-th sort-key="lastName">Etternavn</v-th>
      <v-th sort-key="email">Epost</v-th>
      <th>Telefon</th>
      <v-th sort-key="validUntil">Gyldig til</v-th>
      <v-th sort-key="userType">Type</v-th>
      </thead>
      <tbody slot="body" slot-scope="{displayData}">
      <v-tr v-for="user_object in displayData"
            :key="user_object.id"
            :row="user_object">
        <td>{{ user_object.firstName }}</td>
        <td>{{ user_object.lastName }}</td>
        <td>{{ user_object.email }}</td>
        <td>{{ user_object.phone }}</td>
        <td>{{ parseDate(user_object.validUntil) }}</td>
        <td><span v-if="user_object.userType===0">User</span><span v-else>Admin</span></td>
      </v-tr>
      </tbody>
    </v-table>

    <div class="footer">
      <button class="user-button" @click="emitEditUser">Rediger bruker</button>
      <button class="user-button" @click="deleteUser">Slett bruker</button>
      <button class="user-button" @click="emitNewUser">Opprett bruker</button>
      <table class="size-buttons">
        <tr>
          <td>
            <button type = "button" class="size-button" @click="changePageSize(5)">5</button>
          </td>
          <td>
            <button class="size-button" @click="changePageSize(10)">10</button>
          </td>
          <td>
            <button class="size-button" @click="changePageSize(15)">15</button>
          </td>
        </tr>
      </table>
      <span class="text text-strong">Valgt bruker:</span>
      <span v-if="selected.length === 0" class="text text-muted">Ingen bruker valgt, velg en fra tabellen</span>
      <span v-else class="text">{{ selected[0].email }}</span>
    </div>
  </div>
</template>

<script>

export default {
  name: "UsersTable",
  async created() {
    await this.loadUsers();
  },
  methods: {
    /**
     * Called when the document is created. Fetches all users that are saved in the
     * system and places them into the table. If the Request fails, the error is
     * logged instead and the table remains empty.
     */
    async loadUsers() {
      const getUsersOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          "token": localStorage.getItem("token")
        }
      }

      await fetch(this.$serverUrl + "/users", getUsersOptions)
          .then(response => {
            response.json()
                .then(data => {
                  if (data.result) {
                    for (let user in data.users) {
                      this.users.push(data.users[user]);
                    }
                  } else {
                    console.log("error:" + data.error)
                  }
                })
          })
          .catch((error) => {
            console.log(error);
          })
    },
    /**
     * Parses a date received from the backend system in format YYYY-MM-DD'T'hh:mm:ss'Z'
     * into format DD/MM/YYYY
     */
    parseDate(date) {
      return new Date(Date.parse(date)).toLocaleDateString();
    },
    parseDateIn(dateString) {
      let out = new Date(Date.parse(dateString))
      return out.toLocaleDateString().split("/").reverse().join("-")
    },
    /**
     * First checks if a user is selected in the table. If not, then it will
     * tell the admin to select one. Otherwise, it will emit an "editUser" signal
     * that will be picked up in Users.vue
     */
    emitEditUser: function () {
      if (this.selected.length !== 0) {
        this.$emit('editUser', {
          newUser: false,
          userType: localStorage.getItem("userType"),
          uid: this.selected[0].id
        })
      } else {
        alert("Velg en bruker")
      }
    },
    /**
     * Updates the currently selected user in the table with new values that have been
     * gotten from the database. This is called after a user has been edited.
     * @param uid id of the user being updated
     */
    async updateChanged(uid) {
      await fetch(this.$serverUrl + "/users/" + uid, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          "token": localStorage.getItem("token")
        }
      })
          .then(response => {
            response.json()
                .then(data => {
                  this.selected[0].firstName = data.user.firstName;
                  this.selected[0].lastName = data.user.lastName;
                  this.selected[0].email = data.user.email;
                  this.selected[0].userType = data.user.userType;
                  this.selected[0].validUntil = this.parseDateIn(data.user.validUntil);
                })
          }).catch(error => {
            console.log(error);
          })
    },
    /**
     * Adds a new user to the table using values that are fetched from the database.
     * This is called after a new user has been created.
     * @param uid id of the new user
     */
    async addNewUser(uid) {
      const getUsersOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          "token": localStorage.getItem("token")
        }
      }

      await fetch(this.$serverUrl + "/users/" + uid, getUsersOptions)
          .then(response =>
              response.json()
                  .then(data => {
                    this.users.push({
                      id: data.user.id,
                      firstName: data.user.firstName,
                      lastName: data.user.lastName,
                      email: data.user.email,
                      phone: data.user.phone,
                      validUntil: this.parseDateIn(data.user.validUntil),
                      userType: data.user.userType,
                    })
                  }))
    },
    /**
     * Attempts to delete the currently selected user from the database.
     * If no user is selected, the admin will be prompted to select one.
     * If the admin has selected their own user, they will be alerted
     * that they cannot delete themselves.
     */
    async deleteUser() {
      if(this.selected.length === 0){
        return alert("Velg en bruker.");
      }

      if (this.selected[0].id === localStorage.getItem("userId")) {
        alert("Du kan ikke slette deg selv!");
        return;
      }

      if (confirm("Er du sikker på at du vil slette denne brukeren?")) {
        const deleteUserOption = {
          method: 'DELETE',
          headers: {
            "token": localStorage.getItem("token")
          }
        }
        await fetch(this.$serverUrl + "/users/" + this.selected[0].id, deleteUserOption)
            .then(response =>
                response.json()
                    .then(data => {
                      if (data.result) {
                        console.log("Deleted")
                        this.users.splice(this.users.indexOf(this.selected[0]), 1);
                      } else {
                        alert("Kunne ikke slette brukeren,\nvennligst prøv igjen")
                      }
                    }))
            .catch(error => {
              console.log(error)
            })
      }
    },
    emitNewUser() {
      this.$emit('newUser')
    },
    changePageSize(x){
      this.pageSize = x;
    }
  },
  data: () => ({
    users: [],
    selected: [],
    filters: {lastName: {value: '', keys: ['lastName']}, firstName: {value: '', keys: ['firstName']}},
    currentPage: 1,
    totalPages: 0,
    pageSize: 10,
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    validUntil: '',
    userType: '',
  }),
}

</script>

<style scoped>
html {
}

div {
  display: inline-block;
  float: left;
}

table:not(.size-buttons) {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: auto;
  clear: left;
}

th, td:not(.size-buttons th, .size-buttons td) {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}

.table-hover tbody tr:hover {
  background-color: #c0ecad;
}

.table-hover tbody .table-info {
  background-color: #d5efba;
}

tr:nth-child(odd) {
  background-color: #faf6f6;
}

tr:nth-child(even) {
  background-color: #e7e7e7;
}

strong + span {
  margin-left: 5px;
}

.container {
  float: left;
  margin-top: 1.25vw;
  margin-left: 3.75vw;
}

.form-control {
  border-radius: 0.35rem;
  border: 0;
  padding: 8px 2px;
  margin: 10px 0;
  float: left;
}

.form-control:focus {
  outline: none;
}

.footer {
  margin: .35rem 0;
  width: 100%;
  height: fit-content;
  float: left;
}

.form-control:last-of-type {
  margin-right: 15px;
}

.form-control + .form-control {
  margin-left: 15px;
}

.user-button {
  padding: 5px 5px;
  border-radius: 0.33rem;
  float: left;
}

.user-button:hover{
  cursor: pointer;
  color: #5c5c5f;
  background-color: #bce7a8;
  border: 4px #6cf3b2;
  padding: 7px;
  box-shadow: none;
}

.user-button + .user-button {
  margin-left: 0.25rem;
  clear: none;
}

.size-buttons {
  width: 15%;
  float: right;
  clear: none;
}

.size-buttons tr {
  display: flex;
}

.size-buttons tr td {
  flex: 1;
  display: flex;
  flex-direction: column;
}

button {
  flex: 1;
}

.size-button {
  table-layout: fixed;
  padding: 5px;
  width: 100%;
  height: 100%;
  border-radius: 0.33rem;
}

.size-button:hover {
  cursor: pointer;
  color: #5c5c5f;
  background-color: #bce7a8;
  box-shadow: none;
}

.text {
  margin: 0.15rem 0;
  float: left;
  vertical-align: middle;
  clear: left;
}

.text-strong {
  font-weight: bold;
}

.text-muted {
  color: #5c5c5f;
}

.text + .text {
  clear: none;
  margin-left: 5px;
}

</style>

<style>
.pagination {
  display: inline-flex;
  padding-left: 0;
  list-style: none;
  border-radius: .25rem;
  float: left;
}

.page-link {
  padding: .5rem .75rem;
  margin-left: -1px;
  line-height: 1.25;
  color: #93c47d;
  background-color: #fcfffc;
  border: 1px solid #dee2e6;
}

.page-link:hover {
  z-index: 2;
  color: #739962;
  text-decoration: none;
  background-color: #e9ecef;
  border-color: #dee2e6;
}

.page-link:not(:disabled):not(.disabled) {
  cursor: pointer
}

.page-item:first-child .page-link {
  margin-left: 0;
  border-top-left-radius: .25rem;
  border-bottom-left-radius: .25rem;
}

.page-item:last-child .page-link {
  border-top-right-radius: .25rem;
  border-bottom-right-radius: .25rem;
}

.page-item.active .page-link {
  z-index: 1;
  color: #fff;
  background-color: #93c47d;
  border-color: #93c47d;
}

.page-item.disabled .page-link {
  color: #6c757d;
  pointer-events: none;
  cursor: auto;
  background-color: rgba(243, 243, 243, 0.96);
  border-color: #dee2e6;
}

</style>
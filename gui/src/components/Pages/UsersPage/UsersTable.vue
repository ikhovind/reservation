<template>
  <div>
    <label>Søk etter fornavn</label>
    <input class="form-control" v-model="filters.firstName.value" placeholder="Ola"><br>
    <label>Søk etter etternavn</label>
    <input class="form-control" v-model="filters.lastName.value" placeholder="Nordmann">
    <v-table id="users"
             class="table-hover"
             :data="users"
             :filters="filters"
             selection-mode="single"
             selected-class="table-info"
             @selectionChanged="selected = $event"
    >
      <thead slot="head">
      <v-th sort-key="firstName">Fornavn</v-th>
      <v-th sort-key="lastName">Etternavn</v-th>
      <v-th sort-key="email">Epost</v-th>
      <th>Telefon</th>
      <th>Gyldig til</th>
      <v-th sort-key="userType">Brukertype</v-th>
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
        <td>{{ user_object.userType }}</td>
      </v-tr>
      </tbody>
    </v-table>

    <strong>Valgte bruker:</strong>
    <div v-if="selected.length === 0" class="text-muted">Ingen Bruker Valgt</div>
    <div v-else>{{ selected[0].email }}</div>
    <button @click="emit_event">Rediger bruker</button>
    <button @click="deleteUser">Slett bruker</button>
  </div>
</template>

<script>

export default {
  name: "UsersTable",
  methods: {
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
    parseDate(date) {
      return new Date(Date.parse(date)).toLocaleDateString();
    },
    parseDateIn(dateString) {
      let out = new Date(Date.parse(dateString))
      return out.toLocaleDateString().split("/").reverse().join("-")
    },
    emit_event: function () {
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
    async addNewUser(data) {
      const getUsersOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          "token": localStorage.getItem("token")
        }
      }

      await fetch(this.$serverUrl + "/users/" + data, getUsersOptions)
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
    async deleteUser() {
      if(this.selected[0].id === localStorage.getItem("userId")){
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
                        this.users.splice(this.users.indexOf(this.selected[0]),1);
                      } else {
                        alert("Kunne ikke slette brukeren,\nvennligst prøv igjen")
                      }
                    }))
            .catch(error => {
              console.log(error)
            })
      }
    },
  },
  async created() {
    await this.loadUsers();
  },
  data: () => ({
    users: [],
    selected: [],
    filters: {lastName: {value: '', keys: ['lastName']}, firstName: {value: '', keys: ['firstName']}},
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

table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: auto;
  margin-left: 40px;
}

th, td {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}

tr:nth-child(even) {
  background-color: #dddddd;
}
</style>
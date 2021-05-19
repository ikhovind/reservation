<template>
  <div>
    <input type="text" placeholder="Søk etter brukere">
    <label for="sortUsers">Sorter på:</label>
    <select id="sortUsers">
      <option>Gyldig til</option>
      <option>Brukertype</option>
      <option>Etternavn</option>
      <option>Fornavn</option>
    </select>
    <v-table id="users"
             class="table-hover"
             :data="users"
             selection-mode="single"
             selected-class="table-info"
             @selectionChanged="selected = $event"
    >
      <thead slot="head">
      <th>Fornavn</th>
      <th>Etternavn</th>
      <th>Epost</th>
      <th>Telefon</th>
      <th>Brukertype</th>
      <th>Gyldig til</th>
      </thead>
      <tbody slot="body" slot-scope="{displayData}">
      <v-tr v-for="user_object in displayData"
            :key="user_object.id"
            :row="user_object">
        <td>{{ user_object.firstName }}</td>
        <td>{{ user_object.lastName }}</td>
        <td>{{ user_object.email }}</td>
        <td>{{ user_object.phone }}</td>
        <td>{{ user_object.userType }}</td>
        <td>{{ parseDate(user_object.validUntil) }}</td>
      </v-tr>
      </tbody>
    </v-table>

    <strong>Valgt:</strong>
    <div v-if="selected.length === 0" class="text-muted">Ingen Bruker Valgt</div>
    <div v-else>{{ selected[0].email }}</div>
    <button @click="emit_event">Rediger bruker</button>
  </div>
</template>

<script>

export default {
  name: "UsersTable",
  methods: {
    async loadUsers() {
      const getUsersOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json'}
      }

      await fetch(this.$serverUrl + "/users", getUsersOptions)
          .then(response => {
            response.json()
                .then(data => {
                  console.log(data)
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

      console.log(this.users)
    },
    parseDate(date) {
      return new Date(Date.parse(date)).toLocaleDateString();
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
        headers: {'Content-Type': 'application/json'}
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
    parseDateIn(dateString) {
      let out = new Date(Date.parse(dateString))
      return out.toLocaleDateString().split("/").reverse().join("-")
    },

  },
  async created() {
    await this.loadUsers();
  },
  data: () => ({
    users: [],
    selected: [],
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
  width: 100%;
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
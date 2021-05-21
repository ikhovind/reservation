<template>
  <div>
    <Header></Header>
    <h2>Min info</h2>
    <div class="clickable" @click="emitEditUser">
      <img id="editIcon" src="../../../assets/edit.png" alt="Rediger bruker">
      <p class="inside">Rediger</p>
    </div>
    <section class="container-outer">
      <div class="container-info">
        <span class="text-strong">Personalia</span>
        <div class="container-info-field">
          <label for="myFirstName">Fornavn</label>
          <input type="text" id="myFirstName" disabled>
        </div>
        <div class="container-info-field">
          <label for="myLastName">Etternavn</label>
          <input type="text" id="myLastName" disabled>
        </div>
      </div>
      <div class="container-info">
        <span class="text-strong">Kontakt</span>
        <div class="container-info-field">
          <label for="myEmail">Epost</label>
          <input type="text" id="myEmail" disabled>
        </div>
        <div class="container-info-field">
          <label for="myPhone">Telefonnummer</label>
          <input type="text" id="myPhone" disabled>
        </div>
      </div>
      <div class="container-info">
        <span class="text-strong">Annet</span>
        <div class="container-info-field">
          <label for="myValidUntil">Gyldig til</label>
          <input type="date" id="myValidUntil" disabled>
        </div>
      </div>
    </section>
    <EditUserModal ref="editUserModal"
                   v-on:selfEdited="loadOwnUser"></EditUserModal>
  </div>
</template>

<script>
import EditUserModal from "@/components/Pages/Common/EditUserModal";
import Header from "@/components/Pages/Common/Header";

export default {
  name: "UserInfo",
  components: {Header, EditUserModal},
  methods: {
    async loadOwnUser() {
      let getUserOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'applications/jso',
          "token": localStorage.getItem("token")
        }
      }

      await fetch(this.$serverUrl + "/users/" + localStorage.getItem("userId"), getUserOptions)
          .then(response =>
              response.json()
                  .then(data => {
                        document.getElementById("myEmail").value = data.user.email;
                        document.getElementById("myFirstName").value = data.user.firstName;
                        document.getElementById("myLastName").value = data.user.lastName;
                        document.getElementById("myPhone").value = data.user.phone;
                        document.getElementById("myValidUntil").value = this.parseDateIn(data.user.validUntil);
                      }
                  )
          )
    },
    parseDateIn(dateString) {
      let out = new Date(Date.parse(dateString))
      return out.toLocaleDateString().split("/").reverse().join("-")
    },
    parseDateOut(dateString) {
      let out = new Date(Date.parse(dateString))
      out.setUTCHours(23, 45)
      return out.toISOString()
    },
    emitEditUser() {
      this.$refs.editUserModal.displayInput({
        newUser: false,
        userType: localStorage.getItem("userType"),
        uid: localStorage.getItem("userId"),
        self: true,
      });
    },
  },
  async created() {
    await this.loadOwnUser();
  },
}
</script>

<style scoped>
#editIcon {
  height: 32px;
}

.container-outer {
  -moz-box-align: stretch;
  box-align: stretch;
  align-items: stretch;
  display: flex;
  -moz-flex-wrap: wrap;
  flex-wrap: wrap;
  max-width: 840px;
  margin-left: auto;
  margin-right: auto;
}

.container-info {
  display: flex;
  -moz-box-orient: vertical;
  box-orient: vertical;
  flex-direction: column;
  width: 100%;
}

.text-strong {
  font-weight: bolder;
}

.clickable {
  border-radius: 12px;
  border: 1px solid black;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  padding: 3px 12px;
}

.clickable:hover {
  cursor: pointer;
}
</style>
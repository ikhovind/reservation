<template>
  <div>
    <Header></Header>
    <div class="text-header">Min info</div>
    <section class="container-outer">
      <div class="container-info">
        <span class="text-field">Personalia</span>
        <div class="container-info-field">
          <div class="field-name"><label for="myFirstName">Fornavn</label></div>
          <div class="field-value"><span id="myFirstName"></span></div>
        </div>
        <div class="hr"></div>
        <div class="container-info-field">
          <div class="field-name"><label for="myLastName">Etternavn</label></div>
          <div class="field-value"><span id="myLastName"></span></div>
        </div>
      </div>
      <div class="container-info">
        <span class="text-field">Kontakt</span>
        <div class="container-info-field">
          <div class="field-name"><label for="myEmail">Epost</label></div>
          <div class="field-value"><span id="myEmail"></span></div>
        </div>
        <div class="hr"></div>
        <div class="container-info-field">
          <div class="field-name"><label for="myPhone">Telefonnummer</label></div>
          <div class="field-value"><span id="myPhone"></span></div>
        </div>
      </div>
      <div class="container-info">
        <span class="text-field">Annet</span>
        <div class="container-info-field">
          <div class="field-name"><span>Gyldig til</span></div>
          <div class="field-value"><span id="myValidUntil"></span></div>
        </div>
      </div>
    </section>
    <div class="clickable" @click="emitEditUser">
      <img class="image-icon" id="editIcon" src="../../../assets/edit.png" alt="Rediger bruker">
      <span class="text-button">Rediger</span>
    </div>
    <div class="clickable" @click="logoutAction">
      <img class="image-icon" id="logoutIcon" src="../../../assets/logout.png" alt="Logg ut">
      <span class="text-button">Logg ut</span>
    </div>
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
    /**
     * Fetches information about the User's account and loads them into the document
     */
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
                        document.getElementById("myEmail").innerHTML = data.user.email;
                        document.getElementById("myFirstName").innerHTML = data.user.firstName;
                        document.getElementById("myLastName").innerHTML = data.user.lastName;
                        document.getElementById("myPhone").innerHTML = data.user.phone;
                        document.getElementById("myValidUntil").innerHTML = this.parseDateIn(data.user.validUntil);
                      }
                  )
          )
    },
    /**
     * Parses a date received from the backend system in format YYYY-MM-DD'T'hh:mm:ss'Z'
     * into a more readable format; DD/MM/YYYY
     */
    parseDateIn(dateString) {
      let out = new Date(Date.parse(dateString))
      return out.toDateString();
    },
    /**
     * Parses the front-end's readable DD/MM/YYYY format into the format accepted by the
     * backend system; YYYY-MM-DD'T'hh:mm:ss'Z'
     */
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
    /**
     * Prompts the user to make sure they actually want to log out before clearing localStorage
     * and sending them back to the login page.
     */
    logoutAction() {
      if(confirm("Er du sikker på at\ndu vil logge ut?")){
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        localStorage.removeItem("userType");
        this.$router.push("/");
      }
    }
  },
  async created() {
    await this.loadOwnUser();
  },
}
</script>

<style scoped>
.image-icon {
  height: 32px;
  margin: 0.55rem 0 0.55rem 0.55rem;
}

.container-outer {
  -moz-box-align: stretch;
  box-align: stretch;
  align-items: stretch;
  display: flex;
  -moz-flex-wrap: wrap;
  flex-wrap: wrap;
  width: 33vw;
  min-width: 355px;
  max-width: 750px;
  margin-left: auto;
  margin-right: auto;
}

.container-info {
  display: flex;
  -moz-box-orient: vertical;
  box-orient: vertical;
  flex-direction: column;
  width: 100%;
  border-radius: 0.54rem;
  border: 2px solid #c8eed2;
  padding: 25px 25px 10px 25px;
  background-color: #fcfcfc;
}

.container-info + .container-info {
  margin-top: 2.65rem;
}

.container-info-field {
  display: flex;
  align-items: center;
  padding: 12px 0;
}


.field-name {
  display: flex;
  flex-basis: 156px;
  -moz-box-align: center;
  align-items: center;
  text-transform: uppercase;
  color: #5f6368;
  font-size: 0.865rem;
}

.field-value {
  align-items: center;
  display: flex;
  font-size: 1.23rem;
}

.text-field {
  align-items: center;
  display: flex;
  font-size: 1.75rem;
  font-weight: bold;
}

.hr {
  border-top: 1px solid #dbe2dc;
}

.text-header {
  font-size: 48px;
  font-weight: bold;
  padding: 0.86rem 3vw;
}

.text-button {
  margin: 0.75rem .45rem;
}

.clickable {
  margin-top: 12px;
  border-radius: 12px;
  border: 1px solid #c8eed2;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  background-color: #fcfcfc;
}

.clickable:hover {
  cursor: pointer;
  color: #5c5c5f;
  background-color: #bce7a8;
  border: 1px dot-dot-dash #bce7a8;
}
</style>
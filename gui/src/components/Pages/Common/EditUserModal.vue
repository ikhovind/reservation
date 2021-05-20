<template>
  <div id="editUser">
    <div v-if="this.showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <div class="modal-header">
            <slot name="header">
              <h1 v-if="this.newUser">Opprett bruker</h1>
              <h1 v-else>Rediger bruker</h1>
            </slot>
          </div>

          <div class="modal-body">
            <slot name="body">
              <form id="newUserForm">
                <span style="color: red" id="emailFault">Eposten er allerede registrert</span>
                <label for="email">Epost</label>
                <input class="formInput" name="email" type="text" :disabled=!this.newUser id="email" required>
                <br><br>
                <label for="phone">Telefon</label>
                <input class="formInput" name="phone" type="text" id="phone" required :disabled="!this.newUser">
                <br><br>
                <label for="firstName">Fornavn</label>
                <input class="formInput" name="firstName" type="text" id="firstName" required>
                <br><br>
                <label for="lastName">Etternavn</label>
                <input class="formInput" name="lastName" type="text" id="lastName" required>
                <br><br>
                <div v-bind:style="this.newUser ? {'display': 'none'} : {'display': 'inline'}">
                  <label for="newPasswordBool">Bytt passord?</label>
                  <input class="formInput" type="checkbox" id="newPasswordBool" v-on:click="toggleChangePassword">
                  <br><br>
                </div>
                <div v-bind:style="this.newUser ? {'display': 'inline'} : {'display': 'none'}" class="changePassword">
                  <span class="passwordFault" id="faultText"></span>
                  <br class="passwordFault">
                  <label class="changePassword" for="newPassword">Passord</label>
                  <input class="changePassword formInput" name="newPassword" type="password" id="newPassword" required>
                  <br class="changePassword">
                  <br class="changePassword">
                  <label class="changePassword" for="repeatPassword">Bekreft</label>
                  <input class="changePassword formInput" name="repeatPassword" type="password" id="repeatPassword"
                         required>
                  <br>
                  <br>
                </div>
                <label for="validUntil">Gyldig til</label>
                <input class="formInput" name="validUntil" type="date" :disabled="!this.admin" id="validUntil" required>
                <br>
                <br>
                <label for="userType">Bruker type</label>
                <select class="formInput custom-select" name="userType" :disabled="!this.admin" id="userType" required>
                  <option value="0">User</option>
                  <option value="9">Admin</option>
                </select>
              </form>
            </slot>
          </div>

          <div class="modal-footer">
            <slot name="footer">
              <button class="modal-default-button" @click="submitUser()">Lagre Bruker</button>
              <button class="modal-default-button" @click="initCloseModal()">Avbryt</button>
            </slot>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "EditUserModal",
  methods: {
    initCloseModal() {
      if(confirm("Er du sikker på at du vil avbryte?")){
        this.closeModal();
      }
    },
    closeModal() {
      this.showModal = false;
      //todo is needed?
      this.admin = false;
    },
    async displayInput(args) {
      this.newUser = args.newUser;
      this.admin = parseInt(args.userType) !== 0;
      this.editSelf = args.self;
      if (!args.newUser) {
        await this.loadExistingUser(args.uid);
      } else {
        await this.loadNewUserForm();
      }
      this.showModal = true;
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
    async loadExistingUser(uid) {
      this.uid = uid;
      if (uid === localStorage.getItem("userId")) {
        this.editSelf = true;
      }
      let getUserOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          "token": localStorage.getItem("token")
        }
      }
      await fetch(this.$serverUrl + "/users/" + uid, getUserOptions)
          .then(response => {
            response.json()
                .then(data => {
                  document.getElementById("firstName").value = data.user.firstName;
                  document.getElementById("lastName").value = data.user.lastName;
                  document.getElementById("phone").value = data.user.phone;
                  document.getElementById("email").value = data.user.email;
                  document.getElementById("userType").value = data.user.userType;
                  document.getElementById("validUntil").value = this.parseDateIn(data.user.validUntil);
                  if (this.editSelf) {
                    this.disableTypeField();
                  }
                })
          }).catch(error => {
            console.log(error);
          })
    },
    async loadNewUserForm() {
      this.newUser = true;
    },
    toggleChangePassword() {
      let elements = document.getElementsByClassName("changePassword");
      let checkBox = document.getElementById("newPasswordBool").checked
      for (let i = 0; i < elements.length; i++) {
        elements[i].style.display = checkBox ? "inline" : "none";
      }
    },
    togglePasswordFault(message) {
      this.passwordFault = !this.passwordFault
      document.getElementById("faultText").innerHTML = message;
      let elements = document.getElementsByClassName("passwordFault");
      for (let i = 0; i < elements.length; i++) {
        elements[i].style.display = this.passwordFault ? "block" : "none";
      }
    },
    validatePassword() {
      if (this.passwordFault) {
        this.togglePasswordFault()
      }
      let newPass = document.getElementById("newPassword").value;
      if (newPass.length < 4) {
        this.togglePasswordFault("Passordet må være minst 4 tegn")
        document.getElementById("newPassword").value = "";
        document.getElementById("repeatPassword").value = "";
        return false;
      }
      if (document.getElementById("newPassword").value !== document.getElementById("repeatPassword").value) {
        this.togglePasswordFault("Passordene samsvarer ikke, prøv igjen")
        document.getElementById("repeatPassword").value = "";
        return false;
      }
      return true;
    },
    validateEntries() {
      document.getElementById("emailFault").style.display = "none";
      if (document.getElementById("email").value.length === 0) {
        console.log("invalid email")
        return false;
      }
      if (this.newUser) {
        if (document.getElementById("phone").value.length === 0) {
          console.log("phone")
          return false;
        }
      }
      if (document.getElementById("lastName").value.length === 0) {
        console.log("lastname")
        return false;
      }
      if (document.getElementById("firstName").value.length === 0) {
        console.log("fnawe")
        return false;
      }
      return true;
    },
    async submitUser() {
      if (!this.validateEntries()) {
        console.log("invalid entreies")
        return;
      }
      let checked = document.getElementById("newPasswordBool").checked;
      if (checked || this.newUser) {
        if (!this.validatePassword()) {
          return;
        }
      }
      let userObj = {}
      userObj["firstName"] = document.getElementById("firstName").value;
      userObj["lastName"] = document.getElementById("lastName").value;
      userObj["validUntil"] = this.parseDateOut(document.getElementById("validUntil").value);
      if (!this.editSelf) {
        userObj["userType"] = document.getElementById("userType").value;
      }
      if (this.newUser) {
        userObj["password"] = document.getElementById("newPassword").value;
        userObj["email"] = document.getElementById("email").value;
        userObj["phone"] = document.getElementById("phone").value;
      }
      if (checked) {
        userObj["newPassword"] = document.getElementById("newPassword").value;
      }

      const submitUserOptions = {
        method: this.newUser ? 'POST' : 'PUT',
        headers: {
          'token': localStorage.getItem("token"),
          'Content-Type': "application/json"
        },
        body: JSON.stringify(userObj)
      }

      let url = this.$serverUrl + "/users"
      url += this.newUser ? "" : "/" + this.uid

      await fetch(url, submitUserOptions)
          .then(response => {
            response.json()
                .then(data => {
                  if (data.result) {
                    if (this.editSelf) {
                      this.$emit('selfEdited')
                    }
                    if (this.newUser) {
                      this.newUserCreated(data.id)
                    }
                    if (!this.newUser) {
                      this.$emit('userEdited', this.uid)
                    }
                    this.showModal = false;
                  } else {
                    if (data.error === "email already registered") {
                      document.getElementById("emailFault").style.display = "block";
                      document.getElementById("email").style.border = '1px solid red';
                    }
                    console.log(data.error)
                  }
                })
          })
    },
    disableTypeField() {
      document.getElementById("userType").disabled = true;
    },
    newUserCreated(data) {
      this.$emit('userCreated', data)
    }
  },
  data() {
    return {
      newUser: true,
      showModal: false,
      uid: '',
      passwordFault: false,
      editSelf: false,
      emailFault: false,
    }
  },
}
</script>

<style scoped>
.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: table;
  transition: opacity 0.3s ease;
}

.modal-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.modal-container {
  width: auto;
  height: auto;
  display: inline-block;
  margin: 0 auto;
  padding: 20px 75px;
  background-color: #fff;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  transition: all 0.3s ease;
  font-family: Helvetica, Arial, sans-serif;
}

.modal-header h3 {
  margin-top: 0;
  color: #42b983;
}

.modal-body {
  margin: 20px 0;
}

.modal-footer {
  margin-top: 2.25rem;
  margin-bottom: 0.55rem;
}

.modal-default-button {
  padding: 5px 5px;
  width: 6rem;
  border-radius: 0.33rem;
}

.modal-default-button:hover {
  cursor: pointer;
  color: #5c5c5f;
  background-color: #bce7a8;
  border: 4px #6cf3b2;
  padding: 7px;
  box-shadow: none;
}

.passwordFault {
  color: red;
  display: none;
  padding: 0;
  margin: 0;
}

input {
  border-radius: 0.35rem;
  border: 1px solid grey;
  padding: 3px 2px;
  margin: 3px 0;
}

input:focus {
  outline: none;
}

label {
  float: left;
  padding: 3px 2px;
  margin: 3px 0;
}

select {
  padding: 3px 2px 3px 3px;
  border-radius: 0.35rem;
  border: 1px solid grey;
  -moz-appearance: none;
  -webkit-appearance: none;
  appearance: unset;
}

select:hover {
  cursor: pointer;
}

input, select {
  float: right;
  width: 125px;
  box-sizing: content-box;
}

label + input, label + select {
  margin-left: .66rem;
}

button + button {
  margin-left: 0.75rem;
}

input:invalid {
  border: 1px solid red;
}

#emailFault {
  display: none;
}

/*
 * The following styles are auto-applied to elements with
 * transition="modal" when their visibility is toggled
 * by Vue.js.
 *
 * You can easily play with the modal transition by editing
 * these styles.
 */

.modal-enter {
  opacity: 0;
}

.modal-leave-active {
  opacity: 0;
}

.modal-enter .modal-container,
.modal-leave-active .modal-container {
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}
</style>
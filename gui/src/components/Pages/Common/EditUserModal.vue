<template>
  <div id="editUser">
    <div v-if="this.showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">

          <div class="modal-header">
            <slot name="header">
              <h1 v-if="this.newUser">Ny bruker</h1>
              <h1 v-else>Rediger bruker</h1>
            </slot>
          </div>

          <div class="modal-body">
            <slot name="body">
              <form id="newUserForm">
                <label for="email">Epost</label>
                <input name="email" type="text" :disabled=!this.newUser id="email">
                <br>
                <br>
                <div v-if="this.newUser">
                  <label for="phone">Telefon</label>
                  <input name="phone" type="text" id="phone">
                  <br>
                  <br>
                </div>
                <label for="firstName">Fornavn</label>
                <input name="firstName" type="text" id="firstName">
                <br>
                <br>
                <label for="lastName">Etternavn</label>
                <input name="lastName" type="text" id="lastName">
                <br>
                <br>
                <div v-bind:style="this.newUser ? {'display': 'none'} : {'display': 'inline'}" class="dumb">
                  <label for="newPasswordBool">Change password?</label>
                  <input type="checkbox" id="newPasswordBool" v-on:click="toggleChangePassword">
                  <br>
                  <br>
                </div>
                <div v-bind:style="this.newUser ? {'display': 'inline'} : {'display': 'none'}" class="changePassword">
                  <span class="passwordFault" id="faultText"></span>
                  <br class="passwordFault">
                  <label class="changePassword" for="newPassword">Passord</label>
                  <input class="changePassword" name="newPassword" type="password" id="newPassword">
                  <br class="changePassword">
                  <br class="changePassword">
                  <label class="changePassword" for="repeatPassword">Gjenta passord</label>
                  <input class="changePassword" name="repeatPassword" type="password" id="repeatPassword">
                  <br>
                  <br>
                </div>
                <label for="validUntil">Gyldig til</label>
                <input name="validUntil" type="date" :disabled="!this.admin" id="validUntil">
                <br>
                <br>
                <label for="userType">Bruker type</label>
                <select name="userType" :disabled="!this.admin" id="userType">
                  <option value="0">User</option>
                  <option value="9">Admin</option>
                </select>
              </form>
            </slot>
          </div>

          <div class="modal-footer">
            <slot name="footer">
              <button class="modal-default-button" @click="submitUser()">Lagre Bruker</button>
              <button class="modal-default-button" @click="closeModal()">Avbryt</button>
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
    closeModal() {
      this.showModal = false;
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
      if(uid === localStorage.getItem("userId")) {
        this.editSelf = true;
      }
      let getUserOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json'}
      }
      await fetch(this.$serverUrl + "/users/" + uid, getUserOptions)
          .then(response => {
            response.json()
                .then(data => {
                  document.getElementById("firstName").value = data.user.firstName;
                  document.getElementById("lastName").value = data.user.lastName;
                  document.getElementById("email").value = data.user.email;
                  document.getElementById("userType").value = data.user.userType;
                  document.getElementById("validUntil").value = this.parseDateIn(data.user.validUntil);
                  if(this.editSelf) {
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
    async submitUser() {
      let checked = document.getElementById("newPasswordBool").checked;
      if (checked) {
        if (!this.validatePassword()) {
          return;
        }
      }
      let userObj = {}
      userObj["firstName"] = document.getElementById("firstName").value;
      userObj["lastName"] = document.getElementById("lastName").value;
      userObj["validUntil"] = this.parseDateOut(document.getElementById("validUntil").value);
      if(!this.editSelf){
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
      console.log(JSON.stringify(userObj))

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
                    if(this.editSelf) {
                      this.$emit('selfEdited')
                    }
                    if(!this.newUser) {
                      this.$emit('userEdited', this.uid)
                    }
                    this.showModal = false;
                  } else {
                    console.log(data.error)
                  }
                })
          })
    },
    disableTypeField() {
      document.getElementById("userType").disabled = true;
    }
  },
  data() {
    return {
      newUser: true,
      showModal: false,
      uid: '',
      passwordFault: false,
      editSelf: false,
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
  margin: 0px auto;
  padding: 20px 150px;
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

.modal-default-button {
  float: right;
}

.passwordFault {
  color: red;
  display: none;
  padding: 0;
  margin: 0;
}

.dumb {
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
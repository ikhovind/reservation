<template>
  <div id="editUser">
    <div v-if="this.showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">

          <div class="modal-header">
            <slot name="header">
              <h1 v-if="this.newUser">Ny</h1>
              <h1 v-else>Rediger</h1>
               <h1>bruker</h1>
            </slot>
          </div>

          <div class="modal-body">
            <slot name="body">
              <form id="newUserForm">
                <label for="firstName">Fornavn</label>
                <input name="firstName" type="text" id="firstName">
                <br>
                <br>
                <label for="surname" >Etternavn</label>
                <input  name="lastName" type="text" id="surname">
                <br>
                <br>
                <label for="email">Epost</label>
                <input  name="email" type="text" :disabled="!this.admin" id="email" >
                <br>
                <br>
                <label for="password">Passord</label>
                <input  name="password" type="password" id="password" >
                <br>
                <br>
                <label for="repeatPassword">Gjenta passord</label>
                <input  name="repeatPassword" type="password" id="repeatPassword">
                <br>
                <br>
                <label for="validUntil">Gyldig til</label>
                <input  name="validUntil" type="date" :disabled="!this.admin" id="validUntil">
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
    submitUser() {
      let elements = document.getElementById("newUserForm").elements
      let userObj = {}
      for(let i = 0 ; i < elements.length ; i++){
        let item = elements.item(i);
        userObj[item.name] = item.value;
      }
      console.log(JSON.stringify(userObj));
    },
    displayInput(args) {
      console.log(args.newUser)
      console.log(args.admin)
      console.log(args.uid)
      this.newUser = args.newUser;
      this.showModal = true;
      this.admin = args.admin;
    }
  },
  data() {
    return {
      newUser: true,
      showModal: false
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
  width: 40%;
  height: 379px;
  margin: 0px auto;
  padding: 20px 30px;
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
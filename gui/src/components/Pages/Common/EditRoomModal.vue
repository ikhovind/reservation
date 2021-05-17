<template>
  <div id="editSection">
    <div v-if="this.showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">

          <div class="modal-header">
            <slot name="header">
              <h1 v-if="this.newRoom">Nytt</h1>
              <h1 v-else>Rediger</h1>
              <h1>seksjon</h1>
            </slot>
          </div>

          <div class="modal-body">
            <slot name="body">
              <form id="newRoomForm">
                <label for="roomName">romnavn</label>
                <input type="text" id="roomName" name="roomName">
              </form>
            </slot>
          </div>

          <div class="modal-footer">
            <slot name="footer">
              <button class="modal-default-button" v-on:click="submitRoom($event)">Lagre rom</button>
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
  name: "EditRoomModal",
  data() {
    return {
      showModal: false,
      newRoom: true,
    }
  },
  methods: {
    closeModal() {
      this.showModal = false;
    },
    displayInput(newRoom) {
      this.newRoom = newRoom;
      this.showModal = true;
    },
    async submitRoom(e) {
      e.preventDefault()
      const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
          roomName: document.getElementById("roomName").value,
        })
      };

      await fetch("https://localhost:8443/rooms", requestOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if(data.result) {
              this.$emit('createdRoom');
              this.closeModal();
            }
            else {
              console.log(data.error);
            }
          })
          //Then with the error genereted...
          .catch((error) => {
            error.toString();
          });
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
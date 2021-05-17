<template>
  <div id="editSection">
    <div v-if="this.showModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">

          <div class="modal-header">
            <slot name="header">
              <h1 v-if="this.newSection">Ny</h1>
              <h1 v-else>Rediger</h1>
              <h1>seksjon</h1>
            </slot>
          </div>

          <div class="modal-body">
            <slot name="body">
              <form id="newSectionForm">
                <label for="sectionName">Navn på seksjon</label>
                <input name="sectionName" id="sectionName" type="text">
                <label for="sectionName">Valgfri beskrivelse av seksjon</label>
                <input name="sectionDesc" id="sectionDesc" type="text">
              </form>
            </slot>
          </div>

          <div class="modal-footer">
            <slot name="footer">
              <button class="modal-default-button" @click="submitSection($event)">Lagre seksjon</button>
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
  name: "EditSectionModal",
  data() {
    return {
      showModal: false,
      newSection: true,
    }
  },
  methods: {
    closeModal() {
      this.showModal = false;
    },
    async submitSection(e) {
      e.preventDefault()
      const addSectionOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
          sectionName: document.getElementById("sectionName").value,
          sectionDesc: document.getElementById("sectionDesc").value,
        })
      };

      let sectionId = 5;
      await fetch("https://localhost:8443/rooms/" + sectionId + "/sections", addSectionOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              this.$emit('createdSection');
              this.closeModal();
            } else {
              console.log(data.error);
            }
          })
          //Then with the error genereted...
          .catch((error) => {
            error.toString();
          });
    },
    displayInput(newSection) {
      this.newSection = newSection;
      this.showModal = true;
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
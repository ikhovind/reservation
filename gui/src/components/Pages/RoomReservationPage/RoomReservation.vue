<template>
  <div>
    <Header></Header>
    <div class="reserveDiv">
      <h3>Reserver rom</h3>
      <form class="selectRoom">
        <label for="rooms">Velg et rom</label>
        <select id="rooms" v-model="key" @change="changeRoomSelection()">
          <option disabled value="">Vennligst velg et rom</option>
          <option v-for="room in rooms" :value="room" v-bind:key="room.roomId">{{room.roomName}}</option>
        </select>
        <label for="sections">Velg en seksjon</label>
        <select id="sections">
          <option value="">Hele rommet</option>
          <option v-for="section in selectedSections" :value="section" v-bind:key="section.sectionId">{{section.sectionName}}</option>
        </select>
        <label for="datePicker">Velg en dato</label>
        <input type="date" id="datePicker">
        <div class="buttonList">
          <button class="timeButton">08:00</button>
          <button class="timeButton">08:15</button>
          <button class="timeButton">08:30</button>
        </div>
      </form>
      <button @click="submitReservation()">reserver</button>
      <img v-if="this.selectedRoomId !== ''" src="../../../assets/plus.png" alt="add new section"
           @click="$refs.editSectionModal.displayInput(true, selectedRoomId)">
    </div>
    <EditSectionModal ref="editSectionModal" v-on:createdSection="loadRoomsAndSections()"></EditSectionModal>
  </div>
</template>

<script>
import EditSectionModal from "@/components/Pages/Common/EditSectionModal";
import Header from "@/components/Pages/Common/Header";
export default {
  async created() {
    await this.loadRoomsAndSections();
  },
  name: "RoomReservation",
  components: {Header, EditSectionModal},
  data () {
    return {
      rooms: [],
      sections: [[]],
      selectedSections: [],
      selectedRoomId: ""
    }
  },
  methods: {
    changeRoomSelection() {
      const ef = document.getElementById("rooms");
      this.selectedSections = this.sections[ef.selectedIndex];
      console.log(this.rooms);
      console.log(ef.selectedIndex)
      console.log(this.rooms[ef.selectedIndex - 1])
      this.selectedRoomId = this.rooms[ef.selectedIndex - 1].roomId;
    },

    async loadRoomsAndSections() {
      let selectedIndex;
      let ef = document.getElementById("rooms");
      if (ef != null) {
        selectedIndex = ef.selectedIndex;

      }
      //we're reloading these so ned to empty them first
      this.rooms = [];
      this.sections = [[]];
      this.selectedSections = [];
      this.selectedRoomId = "";

      const addSectionOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json'}
      };

      await fetch("https://localhost:8443/rooms", addSectionOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              console.log(data);
              for (let room in data.rooms){
                this.rooms.push(data.rooms[room]);
                this.sections.push(data.rooms[room].sections)
              }

            } else {
              console.log(data.error);
            }
          })
          //Then with the error genereted...
          .catch((error) => {
            error.toString();
          });
      // set the selection as the same room as before the reload
      if (ef != null) {
        ef.selectedIndex = selectedIndex;
      }
    }
  }
}
</script>

<style scoped>

.buttonList {
  float: right;
}
.timeButton {

  display: block;
  background-color: #81c485; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  font-size: 16px;
}
</style>
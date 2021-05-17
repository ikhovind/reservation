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
      <button>reserver</button>
      <img src="../../../assets/plus.png" alt="add new section" @click="$refs.editSectionModal.displayInput(true)">
    </div>
    <EditSectionModal ref="editSectionModal"></EditSectionModal>
  </div>
</template>

<script>
import EditSectionModal from "@/components/Pages/Common/EditSectionModal";
import Header from "@/components/Pages/Common/Header";
export default {
  async created() {
    const addSectionOptions = {
      method: 'GET',
      headers: {'Content-Type': 'application/json'}
    };

    await fetch("https://localhost:8443/rooms", addSectionOptions)
        .then((response) => response.json())
        //Then with the data from the response in JSON...
        .then(data => {
          if (data.result) {
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
  },
  name: "RoomReservation",
  components: {Header, EditSectionModal},
  data () {
    return {
     rooms: [],
      sections: [[]],
      selectedSections: []
    }
  },
  methods: {
    changeRoomSelection() {
      const ef = document.getElementById("rooms");
      this.selectedSections = this.sections[ef.selectedIndex];
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
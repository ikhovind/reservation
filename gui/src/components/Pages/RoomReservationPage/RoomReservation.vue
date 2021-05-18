<template>
  <div>
    <Header></Header>
    <div class="reserveDiv">
      <h3>Reserver rom</h3>
      <form class="selectRoom">
        <label for="rooms">Velg et rom</label>
        <select id="rooms" @change="changeRoomSelection()">
          <option disabled value="">Vennligst velg et rom</option>
          <option v-for="(room,i) in rooms" :key="i" :value="room" >{{room.roomName}}</option>
        </select>
        <label for="sections">Velg en seksjon</label>
        <select id="sections">
          <option value="">Hele rommet</option>
          <option v-for="(section, i) in selectedSections" :key="i" :value="section" >{{section.sectionName}}</option>
        </select>
        <label for="datePicker">Velg en dato</label>
        <input @change="selectDate()" type="date" id="datePicker">
        <div v-if="this.selectedTime !== ''" class="buttonList">
          <button v-for="index in 72" :key="index"
                  @click="setTime(index)"
          v-bind:class="isBetween(index) ? 'blue' : 'timeButton'">
            {{ Math.floor(index / 4 + 6) }}:{{ padMinutes(((index % 4) * 15))}}</button>
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
      reservedTimes: [[]],
      selectedRoomId: "",
      startTime: null,
      endTime: null,
      selectedTime: ""
    }
  },
  methods: {
    changeRoomSelection() {
      const ef = document.getElementById("rooms");
      this.selectedSections = this.sections[ef.selectedIndex];
      this.selectedRoomId = this.rooms[ef.selectedIndex - 1].roomId;
      this.setReservedTimes();
    },
    async setReservedTimes() {
      const addSectionOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json',
                  'token': localStorage.getItem("token")}
      };

      await fetch("https://localhost:8443/reservations/rooms/" + this.selectedRoomId, addSectionOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              for (let reservation in data.reservations) {
                this.reservedTimes[reservation].push(new Date(data.reservations[reservation].timeFrom));
                this.reservedTimes[reservation].push(new Date(data.reservations[reservation].timeTo));
              }
              console.log(this.reservedTimes);
            } else {
              console.log(data.error);
            }
          })
          //Then with the error genereted...
          .catch((error) => {
            error.toString();
          });

    },
    padMinutes(n) {
      return (n < 10) ? ("0" + n) : n;
    },
    setTime(n) {
      n = (new Date(this.selectedTime + " " + Math.floor(n / 4 + 6) + ":" + this.padMinutes(((n % 4) * 15)) + ":00"));
      if(this.startTime === null && this.endTime === null) {
        this.startTime = n;
      }
      else if (this.startTime.getTime() === n.getTime()) {
        this.startTime = this.endTime;
        this.endTime = null;
      }
      else if (this.endTime === null) {
        if(this.startTime.getTime() > n.getTime()) {
          this.endTime = this.startTime;
          this.startTime = n;
        }
        else {
          this.endTime = n;
        }
      }
      else if (this.endTime.getTime() === n.getTime()) {
        this.endTime = null;
      }
      else if(this.startTime.getTime() > n.getTime()) {
        this.startTime = n;
      }
      else {
        this.endTime = n;
      }
    },
    selectDate() {
      this.selectedTime = document.getElementById("datePicker").value;
    },

    isBetween(n) {
      n = (new Date(this.selectedTime + " " + Math.floor(n / 4 + 6) + ":" + this.padMinutes(((n % 4) * 15)) + ":00"));
      if (this.endTime === null || this.startTime === null) {
        if (this.endTime != null) return this.endTime.getTime() === n.getTime()
        if (this.startTime != null) return this.startTime.getTime() === n.getTime()
        else return false;
      }
      else{
        return (n.getTime() >= this.startTime.getTime() && n <= this.endTime.getTime());
      }
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

      const getRoomsOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json'}
      };

      await fetch("https://localhost:8443/rooms", getRoomsOptions)
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
  overflow-y: scroll;
  height: 500px;
}
.blue {
  display: block;
  background-color: #45ccea; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  font-size: 16px;
}
.red {
  display: block;
  background-color: #f54f4f; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  font-size: 16px;
}
.yellow {
  display: block;
  background-color: #f6cb49; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  font-size: 16px;
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
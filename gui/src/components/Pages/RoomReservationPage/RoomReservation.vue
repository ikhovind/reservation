<template>
  <div>
    <Header></Header>
    <div class="reserveDiv">
      <h3>Reserver rom</h3>
      <form class="selectRoom">
        <label for="rooms">Velg et rom</label>
        <select id="rooms" @change="changeRoomSelection()">
          <option v-for="(room,i) in rooms" :key="i" :value="room" >{{room.roomName}}</option>
        </select>
        <label for="sections">Velg en seksjon</label>
        <select @change="selectSection()" id="sections">
          <option value="">Hele rommet</option>
          <option v-for="(section, i) in availableSections" :key="i" :value="section" >{{section.sectionName}}</option>
        </select>
        <label for="datePicker">Velg en dato</label>
        <input @change="selectDate()" type="date" id="datePicker">
        <div v-if="this.selectedTime !== ''" class="buttonList">
          <button v-for="index in 72" :key="index"
                  @click="setTime(index)"
          :class="[isReserved(index) ? 'red' : isBetween(index) ? 'blue' : 'timeButton']"
          :disabled="isDisabled(index)">
            {{ Math.floor(index / 4 + 6) }}:{{ padMinutes(((index % 4) * 15))}}</button>
        </div>
      </form>
      <button @click="submitReservation()">reserver</button>
      <img src="../../../assets/plus.png" alt="add new section"
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
      sections: [],
      availableSections: [],
      reservedTimes: [],
      selectedRoomId: "",
      startTime: null,
      endTime: null,
      selectedTime: "",
      selectedSectionId: ""
    }
  },
  methods: {
    changeRoomSelection() {
      const ef = document.getElementById("rooms");
      this.availableSections = this.sections[ef.selectedIndex];
      this.selectedRoomId = this.rooms[ef.selectedIndex].roomId;
      this.setReservedTimes();
    },
    async setReservedTimes() {
      const addSectionOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json',
                  'token': localStorage.getItem("token")}
      };
      this.reservedTimes = [];
      await fetch("https://localhost:8443/reservations/rooms/" + this.selectedRoomId, addSectionOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              for (let reservation in data.reservations) {
                this.reservedTimes.push(new Array());
                this.reservedTimes[reservation].push(new Date(data.reservations[reservation].timeFrom));
                this.reservedTimes[reservation].push(new Date(data.reservations[reservation].timeTo));
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
    isDisabled(n) {
      n = (new Date(this.selectedTime + " " + Math.floor(n / 4 + 6) + ":" + this.padMinutes(((n % 4) * 15)) + ":00"));
      for (let i in this.reservedTimes){
        if (this.startTime != null) {
          if(this.startTime.getTime() <= this.reservedTimes[i][0].getTime()){
            return n.getTime() > this.reservedTimes[i][0].getTime();
          }
          if(this.startTime.getTime() >= this.reservedTimes[i][1].getTime()){
            return n.getTime() < this.reservedTimes[i][1].getTime();
          }
        }
        else {
          return n.getTime() > this.reservedTimes[i][0] && n.getTime() < this.reservedTimes[i][1];
        }
      }
      return false;
    },
    isReserved(n) {
      n = (new Date(this.selectedTime + " " + Math.floor(n / 4 + 6) + ":" + this.padMinutes(((n % 4) * 15)) + ":00"));
      for (let arr in this.reservedTimes) {
        if(n.getTime() > this.reservedTimes[arr][0].getTime() && n.getTime() < this.reservedTimes[arr][1].getTime()) {
          return true;
        }
      }
      return false
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
    async selectSection() {
      try {
        this.selectedSectionId = this.availableSections[document.getElementById("sections").selectedIndex -1].sectionId;
      } catch (e) {
        this.selectedSectionId = "";
      }
      const addSectionOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json',
          'token': localStorage.getItem("token")}
      };
      this.reservedTimes = [];
      let url= "/reservations/rooms/" + this.selectedRoomId;

      await fetch("https://localhost:8443" + url, addSectionOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              let counter = 0;
              for (let reservation in data.reservations) {
                if (data.reservations[reservation].section != null) {
                  if (this.selectedSectionId === "" || data.reservations[reservation].section.sectionId === this.selectedSectionId) {
                    this.reservedTimes.push([]);
                    this.reservedTimes[counter].push(new Date(data.reservations[reservation].timeFrom));
                    this.reservedTimes[counter].push(new Date(data.reservations[reservation].timeTo));
                    counter++;
                  }
                }
                else {
                  this.reservedTimes.push([]);
                  this.reservedTimes[counter].push(new Date(data.reservations[reservation].timeFrom));
                  this.reservedTimes[counter].push(new Date(data.reservations[reservation].timeTo));
                  counter++;
                }
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
    async submitReservation() {
      const requestOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json',
        'token': localStorage.getItem("token")},
        body: JSON.stringify({
          timeFrom: this.startTime,
          timeTo: this.endTime
        })
      };
      let sectionIndex = document.getElementById("sections").selectedIndex;
      let url;
      if (sectionIndex == 0) {
        url = "/reservations/rooms/" + this.selectedRoomId;
      }
      else {
        url = "/reservations/rooms/" + this.selectedRoomId + "/sections/" + this.availableSections[sectionIndex - 1].sectionId;
      }
      await fetch("https://localhost:8443" + url, requestOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              this.$emit('created reservation');
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
    async loadRoomsAndSections() {
      //we're reloading these so ned to empty them first
      this.rooms = [];
      this.sections = [];
      this.availableSections = [];
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
                this.sections.push(new Array());
                for (let i in data.rooms[room].sections){
                  this.sections[room].push(data.rooms[room].sections[i]);
                }
              }
            } else {
              console.log(data.error);
            }
          })
          //Then with the error genereted...
          .catch((error) => {
            error.toString();
          });
      this.changeRoomSelection();
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
<template>
  <div>
    <Header></Header>
    <h2>Romoversikt</h2>
    <EditRoomModal ref="editRoomModal" ></EditRoomModal>
    <label for="selectRoom">Velg rom</label>
    <select id="selectRoom" @change="changeRoomSelection()">
      <option value="all">Alle rom</option>
      <option v-for="(room,i) in rooms" :key="i" :value="room" >{{room.roomName}}</option>
    </select>
    <label for="selectSection">Velg seksjon</label>
    <select @change="changeSectionSelection()" id="selectSection">
      <option value="">Hele rommet</option>
      <option v-for="(section, i) in currentSections" :key="i" :value="section.sectionId" >{{section.sectionName}}</option>
    </select>
    <label for="selectDate">Velg dato</label>
    <input @change="changeDateSelection()" type="date" id="selectDate">
    <h3>Alle reservasjoner for valgt rom</h3>
    <label for="sortReservations">Sorter reservasjoner</label>
    <select @change="sortReservations()" id="sortReservations">
      <option value="room">Rom</option>
      <option value="date">Dato</option>
      <option value="descroom">Rom synkende</option>
      <option value="descdate">Dato synkende</option>
    </select>
    <table @click="selectReservation($event)" id="reservationTable">
      <tr>
        <th>Rom</th>
        <th>Seksjon</th>
        <th>Dato</th>
        <th>Fra</th>
        <th>Til</th>
      </tr>
    </table>

    <div v-if="isAdmin()">
      <button @click="$refs.editRoomModal.displayInput(true)">Legg til nytt rom</button>
      <button :disabled="selectedIndex === -1" @click="showEditModal = true">Rediger reservasjon</button>
      <div v-if="showEditModal" id="editReservationModal">
        <div id="editSection">
          <div class="modal-mask">
            <div class="modal-wrapper">
              <div class="modal-container">
              <EditReservation :reservation="reservations[this.selectedIndex]" v-on:createdReservation="closeModal()"></EditReservation>
                <button @click="closeModal()">Avbryt</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <label for="userInfo">Brukerinfo</label>
      <table id="userInfo">
        <tr>
          <th>Navn</th>
          <th>Telefonnummer</th>
          <th>Epost</th>
        </tr>
      </table>
    </div>
  </div>
</template>

<script>
import Header from "@/components/Pages/Common/Header";
import EditRoomModal from "@/components/Pages/Common/EditRoomModal";
import EditReservation from "@/components/Pages/Common/EditReservation";
export default {
  name: "RoomOverview",
  components: {EditReservation, EditRoomModal, Header},
  created() {
    this.fetchReservations();
    this.loadRoomsAndSections();
  },
  data () {
    return {
      showEditModal: false,
      reservations: [],
      selectedIndex: -1,
      rooms: [],
      allSections: [],
      currentSections: [],
      currentReservations: [],
      selectedSection: undefined,
      currentSort: 'room'
    }
  },
  methods: {
    closeModal() {
       this.showEditModal = false;
    },
    isAdmin() {
      return localStorage.getItem("userType") !== "0";
    },
    sortReservations() {
      this.currentSort = document.getElementById("sortReservations").value;
      this.reservationDemands();
    },
    async fetchReservations() {
      const addSectionOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'token': localStorage.getItem("token")
        }
      };
      await fetch(this.$serverUrl + "/reservations", addSectionOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              for (let reservation in data.reservations) {
                try {
                  this.reservations.push(data.reservations[reservation]);
                  this.addReservationToTable(data.reservations[reservation], "max");
                } catch (e) {
                  console.log(e);
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
    addReservationToTable(reservation, index) {
      let table = document.getElementById("reservationTable");
      if (index === "max") index = table.rows.length
      let row = table.insertRow(index);
      let cell0 = row.insertCell(0);
      let cell1 = row.insertCell(1);
      let cell2 = row.insertCell(2);
      let cell3 = row.insertCell(3);
      let cell4 = row.insertCell(4);
      row.addEventListener('click', function () {
        if (row.style.color === "blue") {
          row.style.color = "black";
          this.selectedIndex = -1;
          return;
        }
        for (const tableKey in table.rows) {
          table.rows.item(tableKey).style.color = 'black';
        }
        row.style.color = 'blue';
      });
      cell0.innerText = reservation.room.roomName;
      if ("section" in reservation) {
        cell1.innerText = reservation.section.sectionName;
      } else {
        cell1.innerText = "Hele rommet"
      }
      cell2.innerText = new Date(reservation.timeFrom).getDate() + "/" + (new Date(reservation.timeFrom).getMonth() + 1);
      cell3.innerText = new Date(reservation.timeFrom).getHours() + ":" + new Date(reservation.timeFrom).getMinutes();
      cell4.innerText = new Date(reservation.timeTo).getHours() + ":" + new Date(reservation.timeTo).getMinutes();
    },
    selectReservation(i) {
      if (i.target.parentElement.style.color === 'black') {
        let table = document.getElementById("userInfo");
        this.selectedIndex = -1;
        if (table.rows.length > 1){
          table.deleteRow(table.rows.length - 1);
        }
        return;
      }
      try {
        this.selectedIndex = (i.target.parentElement.rowIndex - 1);
        let table = document.getElementById("userInfo");
        if (table.rows.length > 1){
          table.deleteRow(table.rows.length - 1);
        }
        let row = table.insertRow(table.rows.length);
        let cell0 = row.insertCell(0);
        let cell1 = row.insertCell(1);
        let cell2 = row.insertCell(2);
        cell0.innerText = this.reservations[this.selectedIndex].user.firstName +
            this.reservations[this.selectedIndex].user.lastName;
        cell1.innerText = this.reservations[this.selectedIndex].user.phone;
        cell2.innerText = this.reservations[this.selectedIndex].user.email;
      } catch (e) {
        console.log(e);
      }
    },
    async loadRoomsAndSections() {
      //we're reloading these so ned to empty them first
      this.rooms = [];
      this.allSections = [];
      this.availableSections = [];
      const getRoomsOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json', 'token': localStorage.getItem("token")}
      };

      await fetch( this.$serverUrl + "/rooms", getRoomsOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              for (let room in data.rooms){
                this.rooms.push(data.rooms[room]);
                this.allSections.push(new Array());
                for (let i in data.rooms[room].sections){
                  this.allSections[room].push(data.rooms[room].sections[i]);
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
    changeRoomSelection() {
      const ef = document.getElementById("selectRoom");
      let index = ef.selectedIndex;
      if (index > 0) {
        this.currentSections = this.allSections[index - 1];
      }
      else {
        this.currentSections = [];
      }
      this.reservationDemands();
    },
    changeSectionSelection() {
      this.reservationDemands();
    },
    changeDateSelection() {
      this.reservationDemands();
    },
    reservationDemands() {
      let table = document.getElementById("reservationTable");
      let lastChild = document.getElementById("reservationTable").lastChild;
      //delete table we are filling
      for (let i = table.rows.length; i > 1; i--) {
        table.deleteRow(lastChild.rowIndex);
      }
      this.currentReservations = [...this.reservations];
      let date = document.getElementById("selectDate").value;
      let roomIndex = document.getElementById("selectRoom").selectedIndex;
      let sectionIndex = document.getElementById("selectSection").selectedIndex;
      if (date !== "") {
        let dateObj = new Date(date);
        //only consider whole day
        dateObj.setHours(0,0,0,0);
        //to avoid concurrent modification
        let length = this.currentReservations.length;
        //need to keep count of number of elements removed
        let counter = 0;
        for (let i = 0; i < length; i++) {
          let checkDateObj = new Date(this.currentReservations[i - counter].timeFrom)
          checkDateObj.setHours(0,0,0,0);
          if (dateObj.getTime() !== checkDateObj.getTime()){
            //console.log(this.currentReservations[i].room.roomName);
            this.currentReservations.splice(i - counter, 1);
            counter++;
          }
        }
      }
      //if room is selected
      if (roomIndex > 0) {
        let roomObj = this.rooms[roomIndex - 1];
        let length = this.currentReservations.length;
        let counter = 0;
        for (let k = 0; k < length; k++) {
          if (roomObj.roomId !== this.currentReservations[k - counter].room.roomId){
            this.currentReservations.splice(k - counter, 1);
            counter++;
          }
        }
        //only care about section if room is selected
        if (sectionIndex > 0){
          let length = this.currentReservations.length;
          let counter = 0;
          for (let j = 0; j < length; j++) {
            if (this.currentReservations[j - counter].section === undefined ||
                this.currentReservations[j - counter].section.sectionId !== this.currentSections[sectionIndex - 1].sectionId) {
                this.currentReservations.splice(j - counter,1);
            }
          }
        }
      }
      let desc = this.currentSort.includes("desc");
      if (this.currentSort === "room") {
        this.currentReservations.sort();
      }
      else if (this.currentSort === "date") {
        this.currentReservations.sort(function(firstRes, secondRes) {
          let firstDate = new Date(firstRes.timeFrom);
          let secondDate = new Date(secondRes.timeFrom);
          if (firstDate.getTime() === secondDate.getTime()) return 0;
          if (firstDate.getTime() > secondDate.getTime()) return 1;
          if (firstDate.getTime() < secondDate.getTime()) return -1;
        });
      }
      if (desc) this.currentReservations.reverse();
      for (let i in this.currentReservations) {
        this.addReservationToTable(this.currentReservations[i]);
      }
    }
  }
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



</style>
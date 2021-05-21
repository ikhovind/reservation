<template>
  <div>
    <Header></Header>
    <h2>Romoversikt</h2>
    <EditRoomModal ref="editRoomModal" ></EditRoomModal>
    <label for="selectRoom">Velg rom:</label>
    <br>
    <select id="selectRoom" @change="changeRoomSelection()">
      <option value="all">Alle rom:</option>
      <option v-for="(room,i) in rooms" :key="i" :value="room" >{{room.roomName}}</option>
    </select>
    <br>
    <label for="selectSection">Velg seksjon:</label>
    <br>
    <select @change="filterReservationTable()" id="selectSection">
      <option value="">Hele rommet</option>
      <option v-for="(section, i) in currentSections" :key="i" :value="section.sectionId" >{{section.sectionName}}</option>
    </select>
    <br>
    <label for="selectDate">Velg dato:</label>
    <br>
    <input @change="filterReservationTable()" type="date" id="selectDate">
    <h3>Alle reservasjoner med passende kriterier:</h3>
    <label for="sortReservations">Sorter reservasjoner:</label>
    <br>
    <select @change="changeSorting()" id="sortReservations">
      <option value="room">Rom</option>
      <option value="date">Dato</option>
      <option value="descroom">Rom synkende</option>
      <option value="descdate">Dato synkende</option>
    </select>
    <table @click="selectReservation($event)" class="tables" id="reservationTable">
      <tr>
        <th>Rom</th>
        <th>Seksjon</th>
        <th>Dato</th>
        <th>Fra</th>
        <th>Til</th>
      </tr>
    </table>

    <div v-if="isAdmin()">
      <br>
      <button class="user-button" :disabled="selectedIndex === -1" @click="showModal()">Rediger reservasjon</button>
      <br>
      <button class="user-button" @click="$refs.editRoomModal.displayInput(true)">Legg til nytt rom</button>
      <div v-if="showEditModal" id="editReservationModal">
        <div id="editSection">
          <div class="modal-mask">
            <div class="modal-wrapper">
              <div class="modal-container">
              <EditReservation :reservation="this.currentReservations[this.selectedIndex]" v-on:createdReservation="closeModal()"></EditReservation>
                <button class="user-button" @click="closeModal()">Avbryt</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <br>
      <br>
      <label id="userInfoLabel" for="userInfo">Brukerinfo:</label>
      <table class="tables" id="userInfo">
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
    this.getRoomsAndSections();
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
      this.fetchReservations();
      let table = document.getElementById("userInfo");
      if (table.rows.length > 1){
        table.deleteRow(table.rows.length - 1);
      }
      this.showEditModal = false;
    },
    showModal() {
      this.filterReservationTable();
      this.showEditModal = true;
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
        console.log("sel " + this.selectedIndex)
        let table = document.getElementById("userInfo");
        if (table.rows.length > 1){
          table.deleteRow(table.rows.length - 1);
        }
        let row = table.insertRow(table.rows.length);
        row.style.textAlign = "left";
        row.style.border = "1px solid #999999"

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
    isAdmin() {
      return localStorage.getItem("userType") !== "0";
    },
    changeSorting() {
      this.currentSort = document.getElementById("sortReservations").value;
      this.filterReservationTable();
    },
    /**
     * fetches the reservations from our backend-server
     * @returns {Promise<void>}
     */
    async fetchReservations() {
      let table = document.getElementById("reservationTable");
      let lastChild = document.getElementById("reservationTable").lastChild;

      for (let i = table.rows.length; i > 1; i--) {
        table.deleteRow(lastChild.rowIndex);
      }
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
                  this.addReservationToTable(data.reservations[reservation]);
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
    /**
     * inserts the given reservation at the end of the reservation table
     * @param reservation the reservation you want to add
     */
    addReservationToTable(reservation) {
      let table = document.getElementById("reservationTable");
      let row = table.insertRow(table.rows.length);
      let cell0 = row.insertCell(0);
      let cell1 = row.insertCell(1);
      let cell2 = row.insertCell(2);
      let cell3 = row.insertCell(3);
      let cell4 = row.insertCell(4);
      if (table.rows.length % 2 === 1) {
        row.style.backgroundColor = "#e7e7e7";
      }
      row.style.textAlign = "left";
      row.style.border = "1px solid #999999"
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
    /**
     * fetches all rooms and sections from our backend-server
     * @returns {Promise<void>}
     */
    async getRoomsAndSections() {
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
    /**
     * is called when the user filters by a different room, updates the sections they have to choose from
     */
    changeRoomSelection() {
      const ef = document.getElementById("selectRoom");
      let index = ef.selectedIndex;
      if (index > 0) {
        this.currentSections = this.allSections[index - 1];
      }
      else {
        this.currentSections = [];
      }
      this.filterReservationTable();
    },
    /**
     * deletes the reservation table and fills it again with only the reservation that fits our current criteria
     */
    filterReservationTable() {
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
      this.sortReservations();
    },
    /**
     * sorts the currentReservations-array and ads it to the table in a sorted order
     *
     * sorts on room or date, both ascending and descending
     */
    sortReservations() {
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
.user-button {
  padding: 5px 5px;
  border-radius: 0.33rem;
  margin: auto;
  margin-top: 8px;
}

.user-button:hover:enabled{
  cursor: pointer;
  color: #5c5c5f;
  background-color: #bce7a8;
  border: 4px #6cf3b2;
  padding: 7px;
  box-shadow: none;
}

.user-button + .user-button {
  margin-left: 0.25rem;
  clear: none;
}

.tables {
  font-family: Arial, Helvetica, sans-serif;
  border-collapse: collapse;
  width: 60%;
  margin: auto;
  max-height: 300px;
  overflow-y: scroll;
  margin-top: 20px;
}

#userInfoLabel{
  margin: auto;
  margin-top: 20px;
}

.tables td, .tables th {
  border: 1px solid #999999;
  padding: 8px;
}

.tables th {
  padding-top: 12px;
  padding-bottom: 12px;
  text-align: left;
  background-color: #4CAF50;
  color: white;
}

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
  overflow: hidden;
}

.modal-wrapper {
  display: table-cell;
  vertical-align: middle;
  overflow: hidden;
}

label {
  font-weight: bold;
}

.modal-container {
  width: 40%;
  height: 600px;
  margin: 0px auto;
  padding: 20px 30px;
  background-color: #c4c4c4;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  transition: all 0.3s ease;
  font-family: Helvetica, Arial, sans-serif;
  overflow: hidden;
}

.modal-header h3 {
  margin-top: 0;
  color: #42b983;
}
</style>
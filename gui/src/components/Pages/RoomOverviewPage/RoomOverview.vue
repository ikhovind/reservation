<template>
  <div>
    <Header></Header>
    <h2>Romoversikt</h2>
    <EditRoomModal ref="editRoomModal" v-on:createdRoom="test()"></EditRoomModal>
    <label for="selectRoom">Velg rom</label>
    <select id="selectRoom">
      <option>Rom 1</option>
      <option>Rom 2</option>
    </select>
    <label for="selectSection">Velg seksjon</label>
    <select id="selectSection">
      <option>section 1</option>
      <option>section 2</option>
    </select>
    <label for="selectDate">Velg dato</label>
    <input type="date" id="selectDate">
    <h3>Alle reservasjoner for valgt rom</h3>
    <label for="sortReservations">Sorter reservasjoner</label>
    <select id="sortReservations">
      <option>Rom</option>
      <option>Seksjon</option>
      <option>Dato</option>
      <option>Fra</option>
      <option>Til</option>
      <option>Varighet</option>
    </select>

    <div v-if="isAdmin()">
      <button @click="$refs.editRoomModal.displayInput(true)">Legg til nytt rom</button>
      <table @click="selectReservation($event)" id="reservationTable">
        <tr>
          <th>Rom</th>
          <th>Seksjon</th>
          <th>Dato</th>
          <th>Fra</th>
          <th>Til</th>
        </tr>
      </table>
      <button :disabled="selectedIndex === -1">Rediger reservasjon</button>
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
export default {
  name: "RoomOverview",
  components: {EditRoomModal, Header},
  created() {
    this.fetchReservations();
  },
  data () {
    return {
      reservations: [],
      selectedIndex: -1
    }
  },
  methods: {
    test() {
      console.log("woho");
    },
    isAdmin() {
      return localStorage.getItem("userType") !== "0";
    },
    async fetchReservations() {
      const addSectionOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'token': localStorage.getItem("token")
        }
      };
      await fetch("https://localhost:8443/reservations", addSectionOptions)
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
    addReservationToTable(reservation) {
      let table = document.getElementById("reservationTable");
      let row = table.insertRow(table.rows.length);
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
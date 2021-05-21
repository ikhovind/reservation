<template>
  <div id="root">
    <Header></Header>
    <div class="container">
      <div class="text-header">
        Reservasjonsoversikt
      </div>
      <div class="sort-box">
        <label for="datePicker" class="text-strong">Velg dag</label>
        <input class="formInput" id="datePicker" type="date">
        <label for="sortReservations" class="text-strong">Sorter etter</label>
        <select class="formInput" id="sortReservations">
          <option>Rom</option>
          <option>Seksjon</option>
          <option>dato</option>
          <option>Fra-tidspunkt</option>
          <option>Til-tidspunkt</option>
          <option>Varighet</option>
        </select>
      </div>
      <table @click="selectReservation($event)" id="reservationTable" class="tables">
        <tr>
          <th>Rom</th>
          <th>Seksjon</th>
          <th>Dato</th>
          <th>Fra</th>
          <th>Til</th>
        </tr>
      </table>
      <button :disabled="this.selectedIndex === -1" @click="deleteReservation()">Slett reservasjon</button>
    </div>
  </div>
</template>

<script>
import Header from "@/components/Pages/Common/Header";

export default {
  name: "MyReservations",
  created() {
    this.fetchReservations();
  },
  data() {
    return {
      reservations: [],
      selectedIndex: -1
    }
  },
  components: {Header},
  methods: {
    /**
     * Called upon component creation, fetches the signed in users' reservations and then
     * loads them into the reservations array and adds them to the table.
     */
    async fetchReservations() {
      console.log("Fetching")
      const addSectionOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'token': localStorage.getItem("token")
        }
      };
      await fetch(this.$serverUrl + "/users/" + localStorage.getItem("userId"), addSectionOptions)
          .then(response =>
              response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              for (let reservation in data.user.reservations) {
                this.reservations.push(data.user.reservations[reservation]);
                this.addReservationToTable(data.user.reservations[reservation]);
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
     * Converts a reservation into a table row to be added into the reservation table.
     * @param reservation reservation to be added
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
     * Used to select update a row's style and this.selectedIndex when a reservation
     * is selected from the table
     * @param i the row being selected
     */
    selectReservation(i) {
      if (i.target.parentElement.style.color === 'black') {
        this.selectedIndex = -1;
        return;
      }
      try {
        this.selectedIndex = (i.target.parentElement.rowIndex - 1);
      } catch (e) {
        console.log(e);
      }
    },
    /**
     * Attempts to the delete the reservation that is currently selected.
     * Removes the reservation from the table and array if the request is successful.
     */
    async deleteReservation() {
      const addSectionOptions = {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'token': localStorage.getItem("token")
        }
      };
      console.log(this.reservations);
      await fetch(this.$serverUrl + "/reservations/" + this.reservations[this.selectedIndex].reservationId, addSectionOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              this.reservations.splice(this.selectedIndex, 1);
              document.getElementById("reservationTable").deleteRow(this.selectedIndex + 1);
            } else {
              console.log(data.error);
            }
          })
          //Then with the error genereted...
          .catch((error) => {
            error.toString();
          });
    }
  }
}
</script>

<style scoped>
.formInput {
  border-radius: 0.35rem;
  border: 0;
  padding: 8px 2px;
  margin: 10px 0;
}

.formInput:focus {
  outline: none;
}

.container {
  margin-top: 4vh;
}

.sort-box {
  margin-top: 0.673rem;
}

.text-header {
  font-size: 36px;
}

.text-strong {
  font-weight: bold;
  color: #474545;
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

.tables th {
  padding-top: 12px;
  padding-bottom: 12px;
  text-align: left;
  background-color: #4CAF50;
  color: white;
}

.tables td, .tables th {
  border: 1px solid #999999;
  padding: 8px;
}

button {
  padding: 5px 5px;
  border-radius: 0.33rem;
  color: #393b39;
  margin-top: 0.673rem;
}

button:hover{
  cursor: pointer;
  color: #395e52;
  background-color: #bce7a8;
  border: 4px #6cf3b2;
  padding: 7px;
  box-shadow: none;
}

select {
  padding: 3px 2px 3px 3px;
  border-radius: 0.35rem;
  border: 1px solid grey;
  -moz-appearance: none;
  -webkit-appearance: none;
  appearance: unset;
}

select:hover:enabled {
  cursor: pointer;
}

.formInput + label {
  margin-left: 0.748rem;
}

label + .formInput {
  margin-left: 0.39rem;
}

</style>
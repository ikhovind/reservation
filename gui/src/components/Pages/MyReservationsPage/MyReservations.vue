<template>
<div>
  <Header></Header>
  <label for="datePicker">Velg dag</label>
  <input id="datePicker" type="date">
  <label for="sortReservations">sorter reservasjonene dine</label>
  <select id="sortReservations">
    <option>Rom</option>
    <option>Seksjon</option>
    <option>dato</option>
    <option>Fra-tidspunkt</option>
    <option>Til-tidspunkt</option>
    <option>Varighet</option>
  </select>
  <form>
    <table @click="selectReservation($event)" id="reservationTable">
      <tr>
        <th>Rom</th>
        <th>Seksjon</th>
        <th>Dato</th>
        <th>Fra</th>
        <th>Til</th>
      </tr>
    </table>

  </form>
  <button :disabled="this.selectedIndex === -1" @click="deleteReservation()">Slett reservasjon</button>
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
    async fetchReservations() {
      const addSectionOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'token': localStorage.getItem("token")
        }
      };
      await fetch("https://this.$serverUrl/users/" + localStorage.getItem("userId"), addSectionOptions)
          .then((response) => response.json())
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
      if("section" in reservation) {
        cell1.innerText = reservation.section.sectionName;
      }
      else {
        cell1.innerText = "Hele rommet"
      }
      cell2.innerText = new Date(reservation.timeFrom).getDate() + "/" + (new Date(reservation.timeFrom).getMonth() + 1);
      cell3.innerText = new Date(reservation.timeFrom).getHours() + ":" + new Date(reservation.timeFrom).getMinutes();
      cell4.innerText = new Date(reservation.timeTo).getHours() + ":" + new Date(reservation.timeTo).getMinutes();
    },
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
    async deleteReservation() {
      const addSectionOptions = {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'token': localStorage.getItem("token")
        }
      };
      console.log(this.reservations);
      await fetch("https://" + this.$serverUrl + "/reservations/" + this.reservations[this.selectedIndex].reservationId, addSectionOptions)
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

</style>
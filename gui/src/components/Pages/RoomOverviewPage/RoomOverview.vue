<template>
  <div>
    <Header></Header>
    <h2>Romoversikt</h2>
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
    <div class="buttonList">
      <button class="timeButton">08:00</button>
      <button class="timeButton">08:15</button>
      <button class="timeButton">08:30</button>
    </div>


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
    <table>
      <tr>
        <th>Rom</th>
        <th>Seksjon</th>
        <th>Dato</th>
        <th>Fra</th>
        <th>Til</th>
      </tr>
      <tr>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
      </tr>
    </table>
    <label for="userInfo">Brukerinfo</label>
    <table id="userInfo">
      <tr>
        <th>Navn</th>
        <th>Telefonnummer</th>
        <th>Epost</th>
      </tr>
      <tr>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
      </tr>
    </table>
  </div>
</template>

<script>
import Header from "@/components/Pages/Common/Header";
export default {
  name: "RoomOverview",
  components: {Header}
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
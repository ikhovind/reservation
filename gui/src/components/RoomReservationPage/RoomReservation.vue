<template>
  <div>
    <Header></Header>
    <div class="reserveDiv">
      <h3>Reserver rom</h3>
      <form class="selectRoom">
        <label for="rooms">Velg et rom</label>
        <select name="rooms" id="rooms">
          <option>option1</option>
          <option>option2</option>
        </select>
        <label for="sections">Velg en seksjon</label>
        <select name="sections" id="sections">
          <option>section 1</option>
          <option>section 2</option>
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
      <img src="../../assets/plus.png" alt="add new section">
    </div>
  </div>
</template>

<script>
import Header from "@/components/Common/Header";
export default {
  name: "RoomReservation",
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
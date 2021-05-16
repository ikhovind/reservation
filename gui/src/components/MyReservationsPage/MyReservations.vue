<template>
<div>
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
  </form>
  <button>Rediger reservasjon</button>
</div>
</template>

<script>
export default {
  name: "MyReservations"
}
</script>

<style scoped>

</style>
<template>
  <div>
    <input type="text" placeholder="Søk etter brukere">
    <label for="sortUsers">Sorter på:</label>
    <select id="sortUsers">
      <option>Gyldig til</option>
      <option>Administrator</option>
      <option>Etternavn</option>
      <option>Fornavn</option>
    </select>
    <table>
      <tr>
        <th>Fornavn</th>
        <th>Etternavn</th>
        <th>Epost</th>
        <th>Administrator</th>
        <th>Gyldig til</th>
      </tr>
      <tr>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
        <th>Ingebrigt</th>
      </tr>
    </table>
    <button @click="$emit('editUser')">Rediger bruker</button>
  </div>
</template>

<script>
export default {
  name: "UsersTable"
}
</script>

<style scoped>

</style>
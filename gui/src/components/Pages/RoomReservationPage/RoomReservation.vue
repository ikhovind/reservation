<template>
  <div>
    <Header></Header>
    <EditReservation ref="editReservation"></EditReservation>
      <img id="addSection" src="../../../assets/plus.png" alt="add new section"
           @click="$refs.editSectionModal.displayInput(true, $refs.editReservation.selectedRoomId)">
    <EditSectionModal ref="editSectionModal" v-on:createdSection="$refs.editReservation.loadRoomsAndSections()"></EditSectionModal>
  </div>
</template>

<script>
import EditSectionModal from "@/components/Pages/Common/EditSectionModal";
import Header from "@/components/Pages/Common/Header";
import EditReservation from "@/components/Pages/Common/EditReservation";
export default {
  components: {Header, EditReservation, EditSectionModal}
}
</script>

<style scoped>
#addSection {
  margin-left: auto;
  margin-right: auto;
  position: relative;
  top: -150px;
  left: 100px;
  width: 50px;
}
</style>
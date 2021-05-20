<template>
  <div>
    <div class="reserveDiv">
      <h3 v-if="!edit">Reserver rom</h3>
        <h3 v-else>Endre tidspunkt til reservasjon</h3>
      <form class="selectRoom">
        <div v-if="!edit">
          <label for="rooms">Velg et rom</label>
          <select id="rooms" @change="changeRoomSelection()">
            <option v-for="(room,i) in rooms" :key="i" :value="room" >{{room.roomName}}</option>
          </select>
          <label for="sections">Velg en seksjon</label>
          <select @change="selectSection()" id="sections">
            <option value="">Hele rommet</option>
            <option v-for="(section, i) in availableSections" :key="i" :value="section" >{{section.sectionName}}</option>
          </select>
          <label for="datePicker">Velg en dato</label>
          <input @change="selectDate()" type="date" id="datePicker">

        </div>
        <div v-if="this.selectedTime !== ''" class="buttonList">
          <button v-for="index in 56" :key="index"
                  @click="setTimeSelection(index)"
                  :class="[(isReserved(index) ? 'red' :isPast(index) ? 'grey'  : isBetween(index) ? 'blue' : 'timeButton')]"
                  :disabled="isDisabled(index)">
            {{ Math.floor(index / 4 + 8) }}:{{ padMinutes(((index % 4) * 15))}}</button>
        </div>
      </form>
      <button :disabled="rooms.length === 0" @click="submitReservation()">Lagre</button>

    </div>
  </div>
</template>

<script>

/**
 * is either shown as a modal for admin when they are editing reservation, or as part of page when user is making reservation
 */
export default {
  name: "EditReservation",
  async created() {
    await this.loadRoomsAndSections();
    if (this.reservation !== undefined) {
      this.edit = true;
      this.selectedRoomId = this.reservation.room.roomId;
      if (this.reservation.section !== undefined) {
        this.selectedSectionId = this.reservation.section.sectionId;
      }
      let date = new Date(this.reservation.timeFrom);
      this.selectedTime = (date.getFullYear() + "-"  + ((date.getMonth() + 1 < 10) ? ("0" + (date.getMonth() + 1)) : date.getMonth() + 1) + "-" + date.getDate());
    }
    let today = new Date().toISOString().split('T')[0];
    document.getElementById("datePicker").setAttribute('min', today);
  },
  data () {
    return {
      rooms: [],
      sections: [],
      availableSections: [],
      reservedTimes: [],
      selectedRoomId: "",
      startTime: null,
      endTime: null,
      selectedTime: "",
      selectedSectionId: "",
      edit: false
    }
  },
  props: {
    reservation: undefined
  },
  methods: {
    /**
     * is called when the user selects a different room
     *
     * resets selected start and end-time, as well as updating the available sections
     *
     * lastly sets the times in which a room is reserved
     */
    changeRoomSelection() {
      this.startTime = null;
      this.endTime = null;
      const ef = document.getElementById("rooms");
      let index = ef.selectedIndex;
      if (index < 0) index = 0;
      this.availableSections = this.sections[index];
      try {
        this.selectedRoomId = this.rooms[index].roomId;
      } catch (e) {
        this.selectedRoomId = "";
      }
      this.setReservedTimes();
    },
    /**
     * is called from changeRoomSelection
     *
     * fetches the previous reservations from the selected room and marks these for the user
     * @returns {Promise<void>}
     */
    async setReservedTimes() {
      const addSectionOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json',
          'token': localStorage.getItem("token")}
      };
      this.reservedTimes = [];
      await fetch(this.$serverUrl + "/reservations/rooms/" + this.selectedRoomId, addSectionOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              for (let reservation in data.reservations) {
                this.reservedTimes.push(new Array());
                this.reservedTimes[reservation].push(new Date(data.reservations[reservation].timeFrom));
                this.reservedTimes[reservation].push(new Date(data.reservations[reservation].timeTo));
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
    padMinutes(n) {
      return (n < 10) ? ("0" + n) : n;
    },
    /**
     * is called when the user attempts to select a timestamp, this decides whether that timestamp will be the
     * beginning or end of their selected period, or if the user is attempting to unselect a timestamp
     *
     * @param n int between 0 and 56, the number of 15-minute increments between 8 am and 10 pm on a given day
     */
    setTimeSelection(n) {
      //n only us the time, so we use the selectedDate which the user has selected to get the date
      n = (new Date(this.selectedTime + " " + Math.floor(n / 4 + 8) + ":" + this.padMinutes(((n % 4) * 15)) + ":00"));
      //if no selections have been made the timestamp is the start
      if(this.startTime === null && this.endTime === null) {
        this.startTime = n;
      }
      //if the startTime is clicked on the user wants to unselect
      else if (this.startTime.getTime() === n.getTime()) {
        this.startTime = this.endTime;
        this.endTime = null;
      }
      // the user is attempting to select and endtime
      else if (this.endTime === null) {
        //start time after selected time, selected time is new starttime
        if(this.startTime.getTime() > n.getTime()) {
          this.endTime = this.startTime;
          this.startTime = n;
        }
        else {
          //start time after selected time, selected time is end
          this.endTime = n;
        }
      }
      //user is attempting to unselect end
      else if (this.endTime.getTime() === n.getTime()) {
        this.endTime = null;
      }
      //user has selected end but is now attempting to select time before their start, selection is expanded
      else if(this.startTime.getTime() > n.getTime()) {
        this.startTime = n;
      }
      //selection is expanded other way
      else {
        this.endTime = n;
      }
    },
    /**
     * stores the date that the user has selected as well as reseting any selected timestamps
     */
    selectDate() {
      this.startTime = null;
      this.endTime = null;
      this.selectedTime = document.getElementById("datePicker").value;
    },
    /**
     * returns whether or not a button with a given integer is disabled based on both previous reservation and the current selection
     * i.e. cannot select across an existing reservation
     * @param n n int between 0 and 56, the number of 15-minute increments since 8 am on a given day
     * @returns {boolean}
     */
    isDisabled(n) {
      let org = n;
      if (this.isPast(n)) return true;
      n = (new Date(this.selectedTime + " " + Math.floor(n / 4 + 8) + ":" + this.padMinutes(((n % 4) * 15)) + ":00"));
      for (let i in this.reservedTimes){
        if (this.startTime != null) {
          if(this.startTime.getTime() <= this.reservedTimes[i][0].getTime()){
            return n.getTime() >= this.reservedTimes[i][0].getTime();
          }
          if(this.startTime.getTime() >= this.reservedTimes[i][1].getTime()){
            return n.getTime() <= this.reservedTimes[i][1].getTime();
          }
        }
        else {
          return n.getTime() >= this.reservedTimes[i][0] && n.getTime() <= this.reservedTimes[i][1];
        }
      }
      return this.isReserved(org) || this.isPast(org);
    },
    /**
     * only relevant when doing same day bookings, checks whether a timestamp is in the past
     * @param n
     * @returns {boolean}
     */
    isPast(n) {
      let today = new Date().toISOString().split('T')[0];
      if(this.selectedTime === today) {
        n = (new Date(this.selectedTime + " " + Math.floor(n / 4 + 8) + ":" + this.padMinutes(((n % 4) * 15)) + ":00"));
        let time = new Date();
        return (n.getTime() <= time.getTime());
      }
      return false;
    },
    /**
     * checks whether or not a given timestamp is already reserved
     * @param n int between 0 and 56, the number of 15-minute increments between 8 am and 10 pm on a given day
     * @returns {boolean}
     */
    isReserved(n) {
      n = (new Date(this.selectedTime + " " + Math.floor(n / 4 + 8) + ":" + this.padMinutes(((n % 4) * 15)) + ":00"));
      if (this.edit) {
        let timeFrom = new Date(this.reservation.timeTo);
        let timeTo = new Date(this.reservation.timeFrom);
        if(n.getTime() >= timeFrom.getTime() && n.getTime() <= timeTo.getTime()) return true;
      }
      for (let arr in this.reservedTimes) {
        if(n.getTime() >= this.reservedTimes[arr][0].getTime() && n.getTime() <= this.reservedTimes[arr][1].getTime()) {
          return true;
        }
      }
      return false
    },
    /**
     * checks whether or not a given timestamp is between the users selections, this is used to check if the timestamp
     * should be coloured blue
     * @param n int between 0 and 56, the number of 15-minute increments between 8 am and 10 pm on a given day
     * @returns {boolean}
     */
    isBetween(n) {
      n = (new Date(this.selectedTime + " " + Math.floor(n / 4 + 8) + ":" + this.padMinutes(((n % 4) * 15)) + ":00"));
      if (this.endTime === null || this.startTime === null) {
        if (this.endTime != null) return this.endTime.getTime() === n.getTime()
        if (this.startTime != null) return this.startTime.getTime() === n.getTime()
        else return false;
      }
      else{
        return (n.getTime() >= this.startTime.getTime() && n <= this.endTime.getTime());
      }
    },
    /**
     * is called when the user selects a section within a room, fetches the reservations in that section from our backend
     * @returns {Promise<void>}
     */
    async selectSection() {
      this.startTime = null;
      this.endTime = null;
      try {
        this.selectedSectionId = this.availableSections[document.getElementById("sections").selectedIndex - 1].sectionId;
      } catch (e) {
        this.selectedSectionId = "";
      }
      const addSectionOptions = {
        method: 'GET',
        headers: {'Content-Type': 'application/json',
          'token': localStorage.getItem("token")}
      };
      this.reservedTimes = [];
      let url= "/reservations/rooms/" + this.selectedRoomId;

      await fetch(this.$serverUrl + "" + url, addSectionOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              let counter = 0;
              for (let reservation in data.reservations) {
                if (data.reservations[reservation].section != null) {
                  if (this.selectedSectionId === "" || data.reservations[reservation].section.sectionId === this.selectedSectionId) {
                    this.reservedTimes.push([]);
                    this.reservedTimes[counter].push(new Date(data.reservations[reservation].timeFrom));
                    this.reservedTimes[counter].push(new Date(data.reservations[reservation].timeTo));
                    counter++;
                  }
                }
                else {
                  this.reservedTimes.push([]);
                  this.reservedTimes[counter].push(new Date(data.reservations[reservation].timeFrom));
                  this.reservedTimes[counter].push(new Date(data.reservations[reservation].timeTo));
                  counter++;
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
     * sends a put-request to our backend with the user's reservation
     * @returns {Promise<void>}
     */
    async submitReservation() {
      let method = (this.edit ? "PUT" : "POST")
      const requestOptions = {
        method: method,
        headers: {'Content-Type': 'application/json',
          'token': localStorage.getItem("token")},
        body: JSON.stringify({
          timeFrom: this.startTime,
          timeTo: this.endTime
        })
      };
      //let sectionIndex = document.getElementById("sections").selectedIndex;
      let url;
      if (this.selectedSectionId === "") {
        url = "/reservations/rooms/" + this.selectedRoomId;
      }
      else {
        url = "/reservations/rooms/" + this.selectedRoomId + "/sections/" + this.selectedSectionId;
      }
      await fetch(this.$serverUrl + "" + url, requestOptions)
          .then((response) => response.json())
          //Then with the data from the response in JSON...
          .then(data => {
            if (data.result) {
              this.reservedTimes.push([this.startTime, this.endTime]);
              this.startTime = null;
              this.endTime = null;
              this.$emit('createdReservation');
              this.closeModal();
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
     * gets all rooms and sections from our backend-server
     * @returns {Promise<void>}
     */
    async loadRoomsAndSections() {
      //we're reloading these so ned to empty them first
      this.rooms = [];
      this.sections = [];
      this.availableSections = [];
      if (!this.edit) this.selectedRoomId = "";
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
                this.sections.push(new Array());
                for (let i in data.rooms[room].sections){
                  this.sections[room].push(data.rooms[room].sections[i]);
                }
              }
              if(!this.edit) this.changeRoomSelection();
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

.buttonList {
  float: right;
  overflow-y: scroll;
  height: 500px;
}
.blue {
  display: block;
  background-color: #45ccea; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  font-size: 16px;
}
.red {
  display: block;
  background-color: #f54f4f; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  font-size: 16px;
}
.yellow {
  display: block;
  background-color: #f6cb49; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  font-size: 16px;
}

.grey {
  display: block;
  background-color: #616161; /* Green */
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  font-size: 16px;
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
import Login from "@/components/Pages/LoginPage/Login";
import Users from "@/components/Pages/UsersPage/Users";
import UserInfo from "@/components/Pages/UserInfoPage/UserInfo";
import RoomOverview from "@/components/Pages/RoomOverviewPage/RoomOverview";
import MyReservations from "@/components/Pages/MyReservationsPage/MyReservations";
import RoomReservation from "@/components/Pages/RoomReservationPage/RoomReservation";

export default [
    {
        path: "/",
        component: Login
    },
    {
        path: "/users",
        component: Users
    },
    {
        path: "/reservations",
        component: MyReservations
    },
    {
        path: "/rooms",
        component: RoomOverview
    },
    {
        path: "/userInfo",
        component: UserInfo
    },
    {
        path: "/reserve",
        component: RoomReservation
    }
]
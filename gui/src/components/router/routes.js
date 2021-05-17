import Login from "@/components/LoginPage/Login";
import Users from "@/components/UsersPage/Users";
import UserInfo from "@/components/UserInfoPage/UserInfo";
import RoomOverview from "@/components/RoomOverviewPage/RoomOverview";
import MyReservations from "@/components/MyReservationsPage/MyReservations";
import RoomReservation from "@/components/RoomReservationPage/RoomReservation";

export default [
    {
        path: "/login",
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
        path: "/",
        component: RoomReservation
    }
]
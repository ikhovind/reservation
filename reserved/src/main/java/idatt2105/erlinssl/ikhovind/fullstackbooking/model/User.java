package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseModel {
    private String firstName;
    private String lastName;
    private String phone;
    @Column(unique = true)
    private String email;
    private String password;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Reservation> reservations;
    private Timestamp validUntil;
    private int userType;

    public User(String firstName, String lastName, String phone,
                String email, String password, Timestamp validUntil, int userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.validUntil = validUntil;
        this.reservations = new ArrayList<>();
        this.userType = userType;
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        this.reservations.remove(reservation);
    }

    public JSONObject toJson() {
        JSONObject res = new JSONObject();
        res.put("id", getId());
        res.put("firstName", firstName);
        res.put("lastName", lastName);
        res.put("phone", phone);
        res.put("email", email);
        //res.put("password",password);
        JSONArray reservationJson = new JSONArray();
        for (Reservation r :
                this.reservations) {
            reservationJson.put(r.toJson());
        }
        res.put("reservations", reservationJson);
        res.put("validUntil", validUntil);
        res.put("userType", userType);

        return res;
    }
}

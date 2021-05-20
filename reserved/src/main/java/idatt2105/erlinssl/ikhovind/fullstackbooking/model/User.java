package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * User POJO, defines the variables a User resource should have.
 * A user's ID is inherited from the superclass {@link BaseModel}
 * @see BaseModel
 */
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
    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "user")
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

    public JSONObject toSmallJson() {
        JSONObject res = new JSONObject();
        res.put("id", getId());
        res.put("firstName", firstName);
        res.put("lastName", lastName);
        res.put("phone", phone);
        res.put("email", email);
        //res.put("password",password);
        res.put("validUntil", validUntil);
        res.put("userType", userType);

        return res;
    }

    public JSONObject toJson() {
        JSONObject res = toSmallJson();
        JSONArray reservationJson = new JSONArray();
        for (Reservation r : this.reservations) {
            reservationJson.put(r.toJson());
        }
        res.put("reservations", reservationJson);

        return res;
    }
}

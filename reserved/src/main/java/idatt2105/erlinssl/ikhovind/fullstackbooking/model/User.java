package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private Timestamp valid_until;
    private boolean admin;

    public User(String firstName, String lastName, String phone, String email, String password, Timestamp valid_until, boolean admin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.valid_until = valid_until;
        this.reservations = new ArrayList<>();
        this.admin = admin;
    }

    public JSONObject toJson() {
        JSONObject res = new JSONObject();
        res.put("uid", getUid());
        res.put("firstName", firstName);
        res.put("lastName", lastName);
        res.put("phone", phone);
        res.put("email", email);
        //res.put("password",password);
        res.put("reservations", reservations);
        res.put("valid_until", valid_until);
        res.put("admin", admin);

        return res;
    }
}

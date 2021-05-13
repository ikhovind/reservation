package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseModel {
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String password;
    private boolean admin;

    public User(String firstName, String lastName, String email, String password, boolean admin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }
}

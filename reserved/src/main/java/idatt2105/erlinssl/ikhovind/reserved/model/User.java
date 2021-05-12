package idatt2105.erlinssl.ikhovind.reserved.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseModel {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String salt;
    private boolean admin;

    public User(String firstName, String lastName, String email, String password, String salt, boolean admin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.admin = admin;
    }
}

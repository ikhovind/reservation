package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class UserService {

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;

    //https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt
    public User registerNewUserAccount(User newUser) throws IllegalArgumentException {
        if (emailExist(newUser.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with that email address already exists: " + newUser.getEmail());
        }
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        //return repository.save(user);
        return userRepository.save(newUser);
    }

    public boolean verifyPassword(User user, String password){
        return encoder.matches(password, user.getPassword());
    }

    public User getSingleUserByEmail(String email) {
        return userRepository.findUserByEmail(email.toLowerCase());
    }

    public User getSingleUser(UUID uid) {
        return userRepository.findById(uid)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Iterable<User> findUsersByName(String firstName, String lastName) {
        return userRepository.findByFirstNameLikeAndLastNameLike(firstName+"%", lastName+"%");
    }

    private boolean emailExist(String email){
        return false;
    }
}

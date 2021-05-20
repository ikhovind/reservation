package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.Reservation;
import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

/**
 * Service class that can be used to access the user repository, as well as
 * handling business logic where necessary.
 */
@Slf4j
@Service
@NoArgsConstructor
@AllArgsConstructor
public class UserService {

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;

    /**
     * Called when a new user is to be created. Makes sure that the given email isn't already registered,
     * or throws an exception if it is. If the email is unique, the user's password is encoded before
     * being saved in the database.
     *
     * @param newUser {@link User} to add to the database
     * @return the {@link User} that was added to the database
     * @throws IllegalArgumentException if email is already registered
     */
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

    public boolean verifyPassword(User user, String password) {
        return encoder.matches(password, user.getPassword());
    }

    public User getSingleUserByEmail(String email) {
        return userRepository.findUserByEmail(email.toLowerCase());
    }

    /**
     * Attempts to find a {@link User} by a given {@link UUID}.
     *
     * @param uid {@link UUID} of a {@link User}
     * @return the {@link User} that was found
     * @throws EntityNotFoundException if no {@link User} exists for the given ID
     */
    public User getSingleUser(UUID uid) {
        return userRepository.findById(uid)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Used to find all {@link User}s whose names begin with the given parameters.
     *
     * @param firstName the first name to check for
     * @param lastName the last name to check for
     * @return an {@link Iterable} with the {@link User}s found
     */
    public Iterable<User> findUsersByName(String firstName, String lastName) {
        return userRepository.findByFirstNameLikeAndLastNameLike(firstName + "%", lastName + "%");
    }

    /**
     * Called when an existing user is to be updated, whether it be their user information or
     * that they're getting new reservations assigned to them. The method has to keep track
     * of whether a new password was set or not, so that we do not save plaintext in the database,
     * or encode an already encoded password.
     *
     * @param u {@link User} being updated
     * @param newPass true if password was changed, false if it wasn't
     * @return the newly updated {@link User} object
     */
    public User updateUser(User u, boolean newPass) {
        System.out.println("password changed: " + newPass);
        if(newPass){
            System.out.println("new is " + u.getPassword());
            u.setPassword(encoder.encode(u.getPassword()));
        }
        return userRepository.save(u);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    private boolean emailExist(String email) {
        return userRepository.existsByEmail(email);
    }
}

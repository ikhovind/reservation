package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class UserService {


    @Autowired
    private UserRepository userRepository;

    //https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt
    public User registerNewUserAccount(User newUser) throws IllegalArgumentException {
        if (emailExist(newUser.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with that email address already exists: " + newUser.getEmail());
        }
        //return repository.save(user);
        return userRepository.save(newUser);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getSingleUser(UUID uid) {
        return userRepository.findById(uid)
                .orElseThrow(EntityNotFoundException::new);
    }

    private boolean emailExist(String email){
        return false;
    }
}

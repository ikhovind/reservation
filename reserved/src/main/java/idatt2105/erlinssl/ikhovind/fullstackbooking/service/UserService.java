package idatt2105.erlinssl.ikhovind.fullstackbooking.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import idatt2105.erlinssl.ikhovind.fullstackbooking.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                    "There is an account with that email adress:" + newUser.getEmail());
        }
        //return repository.save(user);
        return userRepository.save(newUser);
    }

    private boolean emailExist(String email){
        return false;
    }
}

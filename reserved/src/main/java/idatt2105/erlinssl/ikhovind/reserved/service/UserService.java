package idatt2105.erlinssl.ikhovind.reserved.service;

import idatt2105.erlinssl.ikhovind.reserved.model.User;
import idatt2105.erlinssl.ikhovind.reserved.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    //https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt
    public User registerNewUserAccount(User newUser) throws IllegalArgumentException {
        if (emailExist(newUser.getEmail())) {
            throw new IllegalArgumentException(
                    "There is an account with that email adress:" + newUser.getEmail());
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        //return repository.save(user);
        return userRepository.saveUser(newUser);
    }

    private boolean emailExist(String email){
        return false;
    }
}

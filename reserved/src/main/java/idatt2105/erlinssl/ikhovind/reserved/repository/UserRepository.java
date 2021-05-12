package idatt2105.erlinssl.ikhovind.reserved.repository;

import idatt2105.erlinssl.ikhovind.reserved.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User saveUser(User user);
}

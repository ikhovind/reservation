package idatt2105.erlinssl.ikhovind.fullstackbooking.repo;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
}

package idatt2105.erlinssl.ikhovind.fullstackbooking.repo;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Iterable<User> findByFirstNameLikeAndLastNameLike(String firstName, String lastName);
}

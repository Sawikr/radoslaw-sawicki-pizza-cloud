package pizza.repository;

import org.springframework.data.repository.CrudRepository;
import pizza.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

  User findByUsername(String username);

  //User save(User user);

}
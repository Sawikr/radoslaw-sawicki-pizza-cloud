package pizza.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import pizza.domain.Order;
import pizza.domain.User;

public interface OrderRepository extends CrudRepository<Order, Long> {

  List<Order> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);

  List<Order> findOrdersByUser(User user);

}

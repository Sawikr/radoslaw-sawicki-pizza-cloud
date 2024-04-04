package pizza.repository;

import org.springframework.data.repository.CrudRepository;
import pizza.domain.Pizza;

public interface PizzaRepository extends CrudRepository<Pizza, Long> {

}

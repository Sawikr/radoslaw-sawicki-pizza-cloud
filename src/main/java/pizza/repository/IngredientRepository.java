package pizza.repository;

import org.springframework.data.repository.CrudRepository;
import pizza.domain.Ingredient;

public interface IngredientRepository 
         extends CrudRepository<Ingredient, String> {

}

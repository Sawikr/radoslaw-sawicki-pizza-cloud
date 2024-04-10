package pizza.actuator;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.actuate.info.Info.Builder;
import pizza.repository.PizzaRepository;

@Component
public class PizzaInfoContributor implements InfoContributor {

    private final PizzaRepository pizzaRepo;

    public PizzaInfoContributor(PizzaRepository tacoRepo) {
        this.pizzaRepo = tacoRepo;
    }

    @Override
    public void contribute(Builder builder) {
        long pizzaCount = pizzaRepo.count();
        Map<String, Object> pizzaMap = new HashMap<String, Object>();
        pizzaMap.put("count", pizzaCount);
        builder.withDetail("pizza-stats", pizzaMap);
    }
}

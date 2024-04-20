package pizza.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import pizza.domain.Ingredient;
import pizza.domain.Ingredient.Type;
import pizza.domain.Order;
import pizza.domain.Pizza;
import pizza.domain.User;
import pizza.repository.IngredientRepository;
import pizza.repository.PizzaRepository;
import pizza.repository.UserRepository;

@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignPizzaController {

  private static Logger logger = LoggerFactory.getLogger(DesignPizzaController.class);
  
  private final IngredientRepository ingredientRepo;
  private final PizzaRepository pizzaRepo;
  private final UserRepository userRepo;

  @Autowired
  public DesignPizzaController(IngredientRepository ingredientRepo, PizzaRepository pizzaRepo, UserRepository userRepo) {
    this.ingredientRepo = ingredientRepo;
    this.pizzaRepo = pizzaRepo;
    this.userRepo = userRepo;
  }

  @ModelAttribute(name = "order")
  public Order order() {
    return new Order();
  }
  
  @ModelAttribute(name = "design")
  public Pizza design() {
    return new Pizza();
  }
  
  @GetMapping
  public String showDesignForm(Model model, Principal principal) {
    List<Ingredient> ingredients = new ArrayList<>();
    ingredientRepo.findAll().forEach(i -> ingredients.add(i));
    
    Type[] types = Type.values();
    for (Type type : types) {
      model.addAttribute(type.toString().toLowerCase(), 
          filterByType(ingredients, type));      
    }
    
    String username = principal.getName();
    User user = userRepo.findByUsername(username);
    model.addAttribute("user", user);
    logger.info("The logged in user is " + user);

    return "design";
  }

  @PostMapping
  public String processDesign(@Valid Pizza pizza, Errors errors, @ModelAttribute Order order) {
    
    if (errors.hasErrors()) {
      return "design";
    }

    Pizza saved = pizzaRepo.save(pizza);
    order.addDesign(saved);

    return "redirect:/orders/current";
  }

  private List<Ingredient> filterByType(
      List<Ingredient> ingredients, Type type) {
    return ingredients
              .stream()
              .filter(x -> x.getType().equals(type))
              .collect(Collectors.toList());
  }
}

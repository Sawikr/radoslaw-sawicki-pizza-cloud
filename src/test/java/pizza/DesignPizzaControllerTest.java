package pizza;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pizza.domain.Ingredient;
import pizza.domain.Ingredient.Type;
import pizza.domain.Pizza;
import pizza.domain.User;
import pizza.repository.IngredientRepository;
import pizza.repository.OrderRepository;
import pizza.repository.PizzaRepository;
import pizza.repository.UserRepository;
import pizza.controller.DesignPizzaController;

@RunWith(SpringRunner.class)
@WebMvcTest(DesignPizzaController.class)
public class DesignPizzaControllerTest {

  @Autowired
  private MockMvc mockMvc;
  
  private List<Ingredient> ingredients;

  private Pizza design;
  
  @MockBean
  private IngredientRepository ingredientRepository;

  @MockBean
  private PizzaRepository designRepository;

  @MockBean
  private OrderRepository orderRepository;

  @MockBean
  private UserRepository userRepository;

  @Before
  public void setup() {
    ingredients = Arrays.asList(
      new Ingredient("FLTO", "pszenna", Type.WRAP),
      new Ingredient("COTO", "kukurydziana", Type.WRAP),
      new Ingredient("GRBF", "mielona wołowina", Type.PROTEIN),
      new Ingredient("CARN", "kawałki mięsa", Type.PROTEIN),
      new Ingredient("TMTO", "pomidory pokrojone w kostkę", Type.VEGGIES),
      new Ingredient("LETC", "sałata", Type.VEGGIES),
      new Ingredient("CHED", "cheddar", Type.CHEESE),
      new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
      new Ingredient("SLSA", "pikantny sos pomidorowy", Type.SAUCE),
      new Ingredient("SRCR", "śmietana", Type.SAUCE)
    );
    
    when(ingredientRepository.findAll())
        .thenReturn(ingredients);
        
    when(ingredientRepository.findById("FLTO")).thenReturn(Optional.of(new Ingredient("FLTO", "pszenna", Type.WRAP)));
    when(ingredientRepository.findById("GRBF")).thenReturn(Optional.of(new Ingredient("GRBF", "mielona wołowina", Type.PROTEIN)));
    when(ingredientRepository.findById("CHED")).thenReturn(Optional.of(new Ingredient("CHED", "cheddar", Type.CHEESE)));
    
    design = new Pizza();
    design.setName("Test Pizza");

    design.setIngredients(Arrays.asList(
        new Ingredient("FLTO", "pszenna", Type.WRAP),
        new Ingredient("GRBF", "mielona wołowina", Type.PROTEIN),
        new Ingredient("CHED", "cheddar", Type.CHEESE)
    	));

    when(userRepository.findByUsername("testuser"))
    		.thenReturn(new User("testuser", "testpass", "Test User", "123 Street", "Someville", "CO", "12345", "123-123-1234"));
  }

  @Test
  @WithMockUser(username="testuser", password="testpass")
  public void testShowDesignForm() throws Exception {
	mockMvc.perform(get("/design"))
        .andExpect(status().isOk())
        .andExpect(view().name("design"))
        .andExpect(model().attribute("wrap", ingredients.subList(0, 2)))
        .andExpect(model().attribute("protein", ingredients.subList(2, 4)))
        .andExpect(model().attribute("veggies", ingredients.subList(4, 6)))
        .andExpect(model().attribute("cheese", ingredients.subList(6, 8)))
        .andExpect(model().attribute("sauce", ingredients.subList(8, 10)));
  }

  @Test
  @WithMockUser(username="testuser", password="testpass", authorities="ROLE_USER")
  public void processDesign() throws Exception {
    when(designRepository.save(design))
        .thenReturn(design);
    
    when(userRepository.findByUsername("testuser"))
	.thenReturn(new User("testuser", "testpass", "Test User", "123 Street", "Someville", "CO", "12345", "123-123-1234"));

    mockMvc.perform(post("/design").with(csrf())
        .content("name=Test+Taco&ingredients=FLTO,GRBF,CHED")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().stringValues("Location", "/orders/current"));
  }
}

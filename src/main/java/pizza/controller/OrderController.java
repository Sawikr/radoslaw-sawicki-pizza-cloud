package pizza.controller;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import pizza.domain.Order;
import pizza.domain.User;
import pizza.repository.OrderRepository;
import pizza.repository.UserRepository;
import pizza.security.RegistrationForm;
import pizza.web.OrderProps;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

  private static Logger logger = LoggerFactory.getLogger(RegistrationForm.class);

  private final OrderRepository orderRepo;
  private final UserRepository userRepo;
  private final OrderProps props;

  public OrderController(OrderRepository orderRepo, UserRepository userRepo, OrderProps props) {
    this.orderRepo = orderRepo;
    this.userRepo = userRepo;
    this.props = props;
  }

  @GetMapping("/current")
  public String orderForm(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute Order order) {

    User user = getUser(userDetails);
    if (order.getDeliveryName() == null) {
      order.setDeliveryName(user.getFullname());
    }
    if (order.getDeliveryStreet() == null) {
      order.setDeliveryStreet(user.getStreet());
    }
    if (order.getDeliveryCity() == null) {
      order.setDeliveryCity(user.getCity());
    }
    if (order.getDeliveryState() == null) {
      order.setDeliveryState(user.getState());
    }
    if (order.getDeliveryZip() == null) {
      order.setDeliveryZip(user.getZip());
    }

    logger.info("User is: " + user.toString());
    return "orderForm";
  }

  @PostMapping
  public String processOrder(@AuthenticationPrincipal UserDetails userDetails, @Valid Order order, Errors errors, SessionStatus sessionStatus) {

    User user = getUser(userDetails);
    if (errors.hasErrors()) {
      return "orderForm";
    }

    order.setUser(user);
    orderRepo.save(order);
    sessionStatus.setComplete();

    logger.info("An order titled: " + order + ", saved for the user " + user.getUsername() + "!");
    return "redirect:/orders";
  }

  @GetMapping
  public String ordersForUser(@AuthenticationPrincipal UserDetails userDetails, Model model) {

    User user = getUser(userDetails);
    List<Order> order = orderRepo.findOrdersByUser(user);
    Pageable pageable = PageRequest.of(0, props.getPageSize());
    model.addAttribute("orders",
        orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));

    logger.info(user.getUsername() + " user orders: " + Arrays.toString(order.toArray()));
    return "orderList";
  }

  private User getUser(UserDetails userDetails) {
    String username = userDetails.getUsername();
    return userRepo.findByUsername(username);
  }
}

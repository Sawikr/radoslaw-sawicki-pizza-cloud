package pizza.controller;

import javax.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import pizza.config.IAuthenticationFacade;
import pizza.domain.Order;
import pizza.domain.User;
import pizza.repository.OrderRepository;
import pizza.repository.UserRepository;
import pizza.web.OrderProps;

@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

  private final OrderRepository orderRepo;
  private final UserRepository userRepo;
  private final OrderProps props;
  private final IAuthenticationFacade authenticationFacade;

  public OrderController(OrderRepository orderRepo, UserRepository userRepo, OrderProps props, IAuthenticationFacade authenticationFacade) {
    this.orderRepo = orderRepo;
    this.userRepo = userRepo;
    this.props = props;
    this.authenticationFacade = authenticationFacade;
  }

  @GetMapping("/current")
  public String orderForm(@ModelAttribute Order order) {
    User user = getAuthenticatedUser();

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

    return "orderForm";
  }

  @PostMapping
  public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus) {

    if (errors.hasErrors()) {
      return "orderForm";
    }
    User user = getAuthenticatedUser();

    order.setUser(user);
    orderRepo.save(order);
    sessionStatus.setComplete();

    return "redirect:/";
  }

  @GetMapping
  public String ordersForUser(Model model) {
    User user = getAuthenticatedUser();

    Pageable pageable = PageRequest.of(0, props.getPageSize());
    model.addAttribute("orders",
        orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));

    return "orderList";
  }

  private User getAuthenticatedUser() {
    Authentication authentication = authenticationFacade.getAuthentication();
    String username = authentication.getName();
    return userRepo.findByUsername(username);
  }
}

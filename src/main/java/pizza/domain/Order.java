package pizza.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name="PIZZA_ORDERS")
public class Order implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  private Date placedAt;
  
  @ManyToOne
  private User user;
  
  @NotBlank(message="Podanie imienia i nazwiska jest obowiązkowe")
  private String deliveryName;
  
  @NotBlank(message="Podanie ulicy jest obowiązkowe")
  private String deliveryStreet;
  
  @NotBlank(message="Podanie miejscowości jest obowiązkowe")
  private String deliveryCity;
  
  @NotBlank(message="Podanie województwa jest obowiązkowe")
  private String deliveryState;
  
  @NotBlank(message="Podanie kodu pocztowego jest obowiązkowe")
  private String deliveryZip;

  @CreditCardNumber(message="To nie jest prawidłowy numer karty kredytowej")
  private String ccNumber;
  
  @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
           message="Wartość musi być w formacie MM/RR")
  private String ccExpiration;

  @Digits(integer=3, fraction=0, message="Nieprawidłowy kod CVV")
  private String ccCVV;

  @ManyToMany(targetEntity= Pizza.class)
  private List<Pizza> pizzas = new ArrayList<>();
  
  public void addDesign(Pizza design) {
    this.pizzas.add(design);
  }
  
  @PrePersist
  void placedAt() {
    this.placedAt = new Date();
  }
  
}

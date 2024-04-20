package pizza.security;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pizza.domain.Role;
import pizza.domain.User;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class RegistrationForm {

  private static Logger logger = LoggerFactory.getLogger(RegistrationForm.class);

  private String username;
  private String password;
  private String fullname;
  private String street;
  private String city;
  private String state;
  private String zip;
  private String phone;
  private List<Role> roles = new ArrayList<>();
  
  public User toUser(PasswordEncoder passwordEncoder) {
    roleName();

    return new User(
            username, passwordEncoder.encode(password),
            fullname, street, city, state, zip, phone, roles);
  }

  private void roleName() {
    String roleName;
    if (username.equalsIgnoreCase("Admin")) {
      roleName = "ROLE_ADMIN";
    }
    else {
      roleName = "ROLE_USER";
    }

    logger.info("Logger is working: " + roleName);
    logger.info("Registered user is " + username);
    Role role = new Role(roleName);
    roles.add(role);
  }
}

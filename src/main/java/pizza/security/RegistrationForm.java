package pizza.security;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import pizza.domain.Role;
import pizza.domain.User;
import java.util.ArrayList;
import java.util.List;

@Data
public class RegistrationForm {

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

    Role role = new Role(roleName);
    roles.add(role);
  }
}

package pizza;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DesignAndOrderPizzaBrowserTest {

  private static HtmlUnitDriver browser;

  @LocalServerPort
  private int port;

  @Autowired
  TestRestTemplate rest;

  @BeforeClass
  public static void setup() {
    browser = new HtmlUnitDriver();
    browser.manage().timeouts()
        .implicitlyWait(10, TimeUnit.SECONDS);
  }

  @AfterClass
  public static void closeBrowser() {
    browser.quit();
  }

  @Test
  public void testDesignAPizzaPage_HappyPath() throws Exception {
    browser.get(homePageUrl());
    clickDesignAPizza();
    assertLandedOnLoginPage();
    doRegistration("testuser", "testpassword");
    assertLandedOnLoginPage();
    doLogin("testuser", "testpassword");
    assertDesignPageElements();
    buildAndSubmitAPizza("Basic Pizza", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
    clickBuildAnotherPizza();
    buildAndSubmitAPizza("Another Pizza", "COTO", "CARN", "JACK", "LETC", "SRCR");
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
    doLogout();
  }

  @Test
  public void testDesignAPizzaPage_EmptyOrderInfo() throws Exception {
    browser.get(homePageUrl());
    clickDesignAPizza();
    assertLandedOnLoginPage();
    doRegistration("testuser2", "testpassword");
    doLogin("testuser2", "testpassword");
    assertDesignPageElements();
    buildAndSubmitAPizza("Basic Pizza", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
    submitEmptyOrderForm();
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
    doLogout();
  }

  @Test
  public void testDesignAPizzaPage_InvalidOrderInfo() throws Exception {
    browser.get(homePageUrl());
    clickDesignAPizza();
    assertLandedOnLoginPage();
    doRegistration("testuser3", "testpassword");
    doLogin("testuser3", "testpassword");
    assertDesignPageElements();
    buildAndSubmitAPizza("Basic Pizza", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
    submitInvalidOrderForm();
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
    doLogout();
  }

  //
  //Metody testujące akcje przeglądarki WWW
  //
  private void buildAndSubmitAPizza(String name, String... ingredients) {
    assertDesignPageElements();

    for (String ingredient : ingredients) {
      browser.findElementByCssSelector("input[value='" + ingredient + "']").click();
    }
    browser.findElementByCssSelector("input#name").sendKeys(name);
    browser.findElementByCssSelector("form#tacoForm").submit();
  }

  private void assertLandedOnLoginPage() {
    assertEquals(loginPageUrl(), browser.getCurrentUrl());
  }

  private void doRegistration(String username, String password) {
    browser.findElementByLinkText("tutaj").click();
    assertEquals(registrationPageUrl(), browser.getCurrentUrl());
    browser.findElementByName("username").sendKeys(username);
    browser.findElementByName("password").sendKeys(password);
    browser.findElementByName("confirm").sendKeys(password);
    browser.findElementByName("fullname").sendKeys("Test McTest");
    browser.findElementByName("street").sendKeys("1234 Test Street");
    browser.findElementByName("city").sendKeys("Testville");
    browser.findElementByName("state").sendKeys("TX");
    browser.findElementByName("zip").sendKeys("12345");
    browser.findElementByName("phone").sendKeys("123-123-1234");
    browser.findElementByCssSelector("form#registerForm").submit();
  }

  private void doLogin(String username, String password) {
    browser.findElementByName("username").sendKeys(username);
    browser.findElementByName("password").sendKeys(password);
    browser.findElementByCssSelector("form#loginForm").submit();
  }

  private void doLogout() {
    WebElement logoutForm = browser.findElementByCssSelector("form#logoutForm");
    if (logoutForm != null) {
      logoutForm.submit();
    }
  }

  private void assertDesignPageElements() {
    assertEquals(designPageUrl(), browser.getCurrentUrl());
    List<WebElement> ingredientGroups = browser.findElementsByClassName("ingredient-group");
    assertEquals(5, ingredientGroups.size());

    WebElement wrapGroup = browser.findElementByCssSelector("div.ingredient-group#wraps");
    List<WebElement> wraps = wrapGroup.findElements(By.tagName("div"));
    assertEquals(2, wraps.size());
    assertIngredient(wrapGroup, 0, "FLTO", "pszenna");
    assertIngredient(wrapGroup, 1, "COTO", "kukurydziana");

    WebElement proteinGroup = browser.findElementByCssSelector("div.ingredient-group#proteins");
    List<WebElement> proteins = proteinGroup.findElements(By.tagName("div"));
    assertEquals(2, proteins.size());
    assertIngredient(proteinGroup, 0, "GRBF", "mielona wołowina");
    assertIngredient(proteinGroup, 1, "CARN", "kawałki mięsa");

    WebElement cheeseGroup = browser.findElementByCssSelector("div.ingredient-group#cheeses");
    List<WebElement> cheeses = proteinGroup.findElements(By.tagName("div"));
    assertEquals(2, cheeses.size());
    assertIngredient(cheeseGroup, 0, "CHED", "cheddar");
    assertIngredient(cheeseGroup, 1, "JACK", "Monterrey Jack");

    WebElement veggieGroup = browser.findElementByCssSelector("div.ingredient-group#veggies");
    List<WebElement> veggies = proteinGroup.findElements(By.tagName("div"));
    assertEquals(2, veggies.size());
    assertIngredient(veggieGroup, 0, "TMTO", "pomidory pokrojone w kostkę");
    assertIngredient(veggieGroup, 1, "LETC", "sałata");

    WebElement sauceGroup = browser.findElementByCssSelector("div.ingredient-group#sauces");
    List<WebElement> sauces = proteinGroup.findElements(By.tagName("div"));
    assertEquals(2, sauces.size());
    assertIngredient(sauceGroup, 0, "SLSA", "pikantny sos pomidorowy");
    assertIngredient(sauceGroup, 1, "SRCR", "śmietana");
  }

  private void fillInAndSubmitOrderForm() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    fillField("input#deliveryName", "Ima Hungry");
    fillField("input#deliveryStreet", "1234 Culinary Blvd.");
    fillField("input#deliveryCity", "Foodsville");
    fillField("input#deliveryState", "CO");
    fillField("input#deliveryZip", "81019");
    fillField("input#ccNumber", "4111111111111111");
    fillField("input#ccExpiration", "10/19");
    fillField("input#ccCVV", "123");
    browser.findElementByCssSelector("form#orderForm").submit();
  }

  private void submitEmptyOrderForm() {
    assertEquals(currentOrderDetailsPageUrl(), browser.getCurrentUrl());
    // Usunięcie zawartości pul wypełnionych automatycznie na podstawie profilu użytkownika
    fillField("input#deliveryName", "");
    fillField("input#deliveryStreet", "");
    fillField("input#deliveryCity", "");
    fillField("input#deliveryState", "");
    fillField("input#deliveryZip", "");
    browser.findElementByCssSelector("form#orderForm").submit();

    assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

    List<String> validationErrors = getValidationErrorTexts();
    assertEquals(9, validationErrors.size());
    assertTrue(validationErrors.contains("Usuń wymienione problemy i spróbuj ponownie."));
    assertTrue(validationErrors.contains("Podanie imienia i nazwiska jest obowiązkowe"));
    assertTrue(validationErrors.contains("Podanie ulicy jest obowiązkowe"));
    assertTrue(validationErrors.contains("Podanie miejscowości jest obowiązkowe"));
    assertTrue(validationErrors.contains("Podanie województwa jest obowiązkowe"));
    assertTrue(validationErrors.contains("Podanie kodu pocztowego jest obowiązkowe"));
    assertTrue(validationErrors.contains("To nie jest prawidłowy numer karty kredytowej"));
    assertTrue(validationErrors.contains("Wartość musi być w formacie MM/RR"));
    assertTrue(validationErrors.contains("Nieprawidłowy kod CVV"));
  }

  private List<String> getValidationErrorTexts() {
    List<WebElement> validationErrorElements = browser.findElementsByClassName("validationError");
    List<String> validationErrors = validationErrorElements.stream()
        .map(el -> el.getText())
        .collect(Collectors.toList());
    return validationErrors;
  }

  private void submitInvalidOrderForm() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    fillField("input#deliveryName", "I");
    fillField("input#deliveryStreet", "1");
    fillField("input#deliveryCity", "F");
    fillField("input#deliveryState", "C");
    fillField("input#deliveryZip", "8");
    fillField("input#ccNumber", "1234432112344322");
    fillField("input#ccExpiration", "14/91");
    fillField("input#ccCVV", "1234");
    browser.findElementByCssSelector("form#orderForm").submit();

    assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

    List<String> validationErrors = getValidationErrorTexts();
    assertEquals(4, validationErrors.size());
    assertTrue(validationErrors.contains("Usuń wymienione problemy i spróbuj ponownie."));
    assertTrue(validationErrors.contains("To nie jest prawidłowy numer karty kredytowej"));
    assertTrue(validationErrors.contains("Wartość musi być w formacie MM/RR"));
    assertTrue(validationErrors.contains("Nieprawidłowy kod CVV"));
  }

  private void fillField(String fieldName, String value) {
    WebElement field = browser.findElementByCssSelector(fieldName);
    field.clear();
    field.sendKeys(value);
  }

  private void assertIngredient(WebElement ingredientGroup, int ingredientIdx, String id, String name) {
    List<WebElement> proteins = ingredientGroup.findElements(By.tagName("div"));
    WebElement ingredient = proteins.get(ingredientIdx);
    assertEquals(id, ingredient.findElement(By.tagName("input")).getAttribute("value"));
    assertEquals(name, ingredient.findElement(By.tagName("span")).getText());
  }

  private void clickDesignAPizza() {
    assertEquals(homePageUrl(), browser.getCurrentUrl());
    browser.findElementByCssSelector("a[id='design']").click();
  }

  private void clickBuildAnotherPizza() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    browser.findElementByCssSelector("a[id='another']").click();
  }

  //
  //Metody pomocnicze adresu URL
  //
  private String loginPageUrl() {
    return homePageUrl() + "login";
  }

  private String registrationPageUrl() {
    return homePageUrl() + "register";
  }

  private String designPageUrl() {
    return homePageUrl() + "design";
  }

  private String homePageUrl() {
    return "http://localhost:" + port + "/";
  }

  private String orderDetailsPageUrl() {
    return homePageUrl() + "orders";
  }

  private String currentOrderDetailsPageUrl() {
    return homePageUrl() + "orders/current";
  }

}

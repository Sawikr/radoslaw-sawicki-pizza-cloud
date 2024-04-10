package pizza;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DesignPizzaControllerBrowserTest {
  
  private static ChromeDriver browser;
  
  @LocalServerPort
  private int port;
  
  @Autowired
  TestRestTemplate rest;
  
  @BeforeClass
  public static void openBrowser() {
    WebDriverManager.chromedriver().setup();//TODO - zrobione!
    browser = new ChromeDriver();
    browser.manage().timeouts()
        .implicitlyWait(10, TimeUnit.SECONDS);
  }
  
  @AfterClass
  public static void closeBrowser() {
    browser.quit();
  }
  
  @Test
  //@Ignore("TODO: trzeba rozwiązać kwestię uwierzytelnienia dla tego testu")
  public void testDesignAPizzaPage() throws Exception {

    browser.get(homePageUrl());
    clickDesignAPizza();
    doRegistration("testuser2", "testpassword2");
    doLogin("testuser2", "testpassword2");

    List<WebElement> ingredientGroups = browser.findElementsByClassName("ingredient-group");
    assertEquals(5, ingredientGroups.size());
    
    WebElement wrapGroup = ingredientGroups.get(0);
    List<WebElement> wraps = wrapGroup.findElements(By.tagName("div"));
    assertEquals(2, wraps.size());
    assertIngredient(wrapGroup, 0, "FLTO", "pszenna");
    assertIngredient(wrapGroup, 1, "COTO", "kukurydziana");
    
    WebElement proteinGroup = ingredientGroups.get(1);
    List<WebElement> proteins = proteinGroup.findElements(By.tagName("div"));
    assertEquals(2, proteins.size());
    assertIngredient(proteinGroup, 0, "GRBF", "mielona wołowina");
    assertIngredient(proteinGroup, 1, "CARN", "kawałki mięsa");
  }
  
  private void assertIngredient(WebElement ingredientGroup, int ingredientIdx, String id, String name) {
    List<WebElement> proteins = ingredientGroup.findElements(By.tagName("div"));
    WebElement ingredient = proteins.get(ingredientIdx);
    assertEquals(id, ingredient.findElement(By.tagName("input")).getAttribute("value"));
    assertEquals(name, ingredient.findElement(By.tagName("span")).getText());
  }

  private void clickDesignAPizza() {
    browser.findElementByCssSelector("a[id='design']").click();
  }

  private void doRegistration(String username, String password) {
    browser.findElementByLinkText("tutaj").click();
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

  //Metoda pomocnicza adresu URL
  private String homePageUrl() {
    return "http://localhost:" + port + "/";
  }
}

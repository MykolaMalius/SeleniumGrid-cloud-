import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;

/**
 * Created by M.Malyus on 11/13/2017.
 */
public class Main {
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        //Just Gmail on @hub CrossBwroser SeleniumGridCloud
        RemoteWebDriver driver = SingletonDriver.getInstance();
        final Wait wait = new WebDriverWait(driver, 5, 1000);
        wait.until(ExpectedConditions.visibilityOf((WebElement) By.xpath("//input[@id=\"identifierId\"]")));
        driver.findElement(By.xpath("//input[@id=\"identifierId\"]")).sendKeys("login");
        driver.findElement(By.xpath("//content//span")).click();
        wait.until(ExpectedConditions.visibilityOf((WebElement) By.xpath("//input[@type=\"password\"]")));
        driver.findElement(By.xpath("//input[@type=\"password\"]")).sendKeys("password");
        driver.findElement(By.xpath("//content//span[@class=\"RveJvd snByac\"]")).click();
        SingletonDriver.endSession();
    }
}

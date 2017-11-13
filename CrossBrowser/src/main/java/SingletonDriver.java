import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.concurrent.TimeUnit;

public class SingletonDriver {
    private static ThreadLocal<RemoteWebDriver> driverThreadLocal = new ThreadLocal<>();
    static String username = ""; // Your username
    static String authkey = "";  // Your authkey
    String testScore = "unset";

    public SingletonDriver() {

    }
    public static RemoteWebDriver getInstance() throws MalformedURLException {
        if(driverThreadLocal.get() != null){
            return driverThreadLocal.get();
        }
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("name", "Login Form Example");
        caps.setCapability("build", "1.0");
        caps.setCapability("browserName", "Safari");
        caps.setCapability("version", "10");
        caps.setCapability("platform", "Mac OSX 10.12");
        caps.setCapability("screenResolution", "1366x768");
        caps.setCapability("record_video", "true");
        RemoteWebDriver instance;
        instance =  new RemoteWebDriver(new URL("http://" + username + ":" + authkey +"@hub.crossbrowsertesting.com:80/wd/hub"), caps);;
        EnvProperties envProperties = new EnvProperties();
        instance.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);
        instance.manage().window().maximize();
        instance.manage().deleteAllCookies();
        driverThreadLocal.set(instance);
        driverThreadLocal.get().get(envProperties.getBaseUrl());
        return driverThreadLocal.get();
    }
    public static void endSession(){
        try {
            driverThreadLocal.get().quit();
        }
        finally {
            driverThreadLocal.remove();
        }
    }

    public JsonNode setScore(String seleniumTestId, String score) throws UnirestException {
        // Mark a Selenium test as Pass/Fail
        HttpResponse<JsonNode> response = Unirest.put("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .field("action","set_score")
                .field("score", score)
                .asJson();
        return response.getBody();
    }

    public String takeSnapshot(String seleniumTestId) throws UnirestException {
        /*
         * Takes a snapshot of the screen for the specified test.
         * The output of this function can be used as a parameter for setDescription()
         */
        HttpResponse<JsonNode> response = Unirest.post("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}/snapshots")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .asJson();
        // grab out the snapshot "hash" from the response
        String snapshotHash = (String) response.getBody().getObject().get("hash");

        return snapshotHash;
    }

    public JsonNode setDescription(String seleniumTestId, String snapshotHash, String description) throws UnirestException{
        /*
         * sets the description for the given seleniemTestId and snapshotHash
         */
        HttpResponse<JsonNode> response = Unirest.put("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}/snapshots/{snapshotHash}")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .routeParam("snapshotHash", snapshotHash)
                .field("description", description)
                .asJson();
        return response.getBody();
    }
}
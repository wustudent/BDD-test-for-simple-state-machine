package rnd.statemachine;

import org.springframework.boot.SpringApplication;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;

/**
 * Definition of steps.
 */
public class Stepdefs {
    // context vaiables shared between steps
    private String serverUrl;
    private HttpClient httpClient;
    private ContentResponse response;
    private String orderId;
    private static ApplicationContext ctx;

    // Hook for step after each scenario
    @After
    public static void shutdownServer() {
        SpringApplication.exit(ctx);
    }

    // ==============================
    // Given steps
    // ==============================
    @Given("the service is running on localhost with port {int}")
    public void the_service_is_running_on_localhost_with_port(int port) throws InterruptedException, IOException {
        ctx = SpringApplication.run(StateMachineApplication.class, "--server.port=" + port);
        serverUrl = "http://localhost:" + port;
        Thread.sleep(5000);
        URL url = new URL(serverUrl);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();

        int responseCode = huc.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);
    }

    @Given("browser is ready to use")
    public void browser_is_ready_to_use() throws Exception {
        // use Jetty as a "browser"
        httpClient = new HttpClient();
        httpClient.start();
    }

    // ==============================
    // When steps
    // ==============================
    @When("user submit the order")
    public void user_submit_the_order() throws InterruptedException, ExecutionException, TimeoutException {
        // collect response
        response = httpClient.GET(serverUrl + "/order");
    }

    @When("user pays {float} Euros for the order")
    public void user_pays_Euros_for_the_order(float price)
            throws InterruptedException, ExecutionException, TimeoutException {
        payOrder(price, orderId);
    }

    @When("user pays {float} Euros for an order does not exist")
    public void user_pays_Euros_for_an_order_does_not_exist(float price) throws InterruptedException, TimeoutException, ExecutionException {
        payOrder(price, UUID.randomUUID().toString());
    }

    private void payOrder(float price, String orderId) throws InterruptedException, TimeoutException, ExecutionException {
        Request request = httpClient.newRequest(serverUrl + "/order/cart");
        request.param("payment", "" + price);
        request.param("orderId", "" + orderId);
        request.method("GET");
        response = request.send();
    }

    // ==============================
    // Then steps
    // ==============================
    @Then("the order is created with a given orderId")
    public void the_order_is_created_with_a_given_orderId() {
        assertEquals(200, response.getStatus());
        String responseContent = response.getContentAsString();
        assertNotEquals(0, responseContent.length());
        String[] splits = responseContent.split(", ");
        assertEquals(2, splits.length);
        assertEquals("orderSuccess", splits[0]);
        String[] orderIdContents = splits[1].split(" = ");
        orderId = orderIdContents[1];
    }

    @Then("order completed successfullly")
    public void order_completed_successfullly() {
        assertEquals(200, response.getStatus());
        assertEquals("paymentSuccess", response.getContentAsString());
    }

    @Then("order not completed")
    public void order_not_completed() {
        assertEquals(200, response.getStatus());
        assertEquals("paymentError", response.getContentAsString());
    }

    @Then("order failed")
    public void order_failed() {
        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().startsWith("No state exists for orderId="));
    }

}

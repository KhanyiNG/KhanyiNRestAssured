package basicTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import requestBuilder.UserRequestBuilder;

@Test
public class UserRegistrationTests {

    String BaseURL = "https://ndosiautomation.co.za/APIDEV";
    String registeredUserId;
    String adminToken;

    @Test (priority = 1)
    public void userRegistrationTest() {

        String registerUserPath = "/register";

        String userRegistrationPayload = "{\n" +
                "  \"firstName\": \"Khanyisa\",\n" +
                "  \"lastName\": \"Ndlovu\",\n" +
                "  \"email\": \"khanyisa.ndlovu@gmail.com\",\n" +
                "  \"password\": \"Khanyisa@2026!\",\n" +
                "  \"confirmPassword\": \"Khanyisa@2026!\",\n" +
                "  \"groupId\": \"c1ce77c4-bd1a-42ae-901f-fc3e534c55b8\"\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(registerUserPath)
                .header("Content-Type", "application/json")
                .body(userRegistrationPayload)
                .log().all()
                .post()
                .then().extract().response();

        int responseStatusCode = response.getStatusCode();
        System.out.println("Status Code: " + responseStatusCode);
        System.out.println(("Response Body: " + response.getBody().asString()));
       // Assert.assertEquals(responseStatusCode, 201, "Expected status code 200");

        registeredUserId = response.jsonPath().getString("data.id");
        // Code for user registration test
    }

    @Test (priority = 2)
    public void adminLoginTest() {

        String loginPath = "/login";
        String adminLoginPayload = "{\n" +
                "  \"email\": \"admin@gmail.com\",\n" +
                "  \"password\": \"@12345678\"\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(loginPath)
                .header("Content-Type", "application/json")
                .body(adminLoginPayload)
                .log().all()
                .post()
                .then().extract().response();

        int responseStatusCode = response.getStatusCode();
        System.out.println("Status Code: " + responseStatusCode);
        System.out.println(("Response Body: " + response.getBody().asString()));
        Assert.assertEquals(responseStatusCode, 200, "Expected status code 200");

        adminToken = response.jsonPath().getString("data.token");
        // Code for admin login test
    }

    @Test (priority = 3)
    public void approveUser(){

        String approveUserPath = "/admin/users/"+registeredUserId+"/approve";

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(approveUserPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .log().all()
                .put()
                .then().extract().response();

        System.out.println(("Response Body: " + response.getBody().asString()));

    }


}

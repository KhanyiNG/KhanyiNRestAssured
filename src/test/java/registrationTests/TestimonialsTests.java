package registrationTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requestBuilder.AdminRequestBuilder;

public class TestimonialsTests {

    String BaseURL = "https://ndosiautomation.co.za/APIDEV";
    static String adminToken;
    static String testimonialId;
    static String firstName;
    static String lastName;
    static String email;
    static String password;
    static String groupId;

    @BeforeClass
    public void setUp() {
        // Get admin token for authentication
       Response loginResponse = AdminRequestBuilder.adminLogin();
       adminToken = loginResponse.jsonPath().getString("data.token");

        // Test data
        firstName = "Khanyisa";
        lastName = "Ndlovu";
        email = "khanyisa.testimonial@test.com";
        password = "Khanyisa@2026!";
        groupId = "c1ce77c4-bd1a-42ae-901f-fc3e534c55b8";
    }

    // TEST 1: CREATE TESTIMONIAL
    @Test(priority = 1)
    public void createTestimonialTest() {
        String createPath = "/testimonials";

        String payload = "{\n" +
                "  \"title\": \"Khanyisa Ndlovu\",\n" +
                "  \"content\": \"This course was excellent! Very informative.\",\n" +
                "  \"rating\": 5\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(createPath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(payload)
                .log().all()
                .post();

        System.out.println("Create Status Code: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 201, "Testimonial should be created");

        // Store testimonial ID for update/delete
        testimonialId = response.jsonPath().getString("data.id");
        Assert.assertNotNull(testimonialId, "Testimonial ID should not be null");
        System.out.println("Created Testimonial ID: " + testimonialId);
    }

    // TEST 2: UPDATE TESTIMONIAL (uses PATH PARAMETER)
    @Test(priority = 2, dependsOnMethods = "createTestimonialTest")
    public void updateTestimonialTest() {
        String updatePath = "/testimonials/" + testimonialId;

        String payload = "{\n" +
                "  \"name\": \"Khanyisa Ndlovu\",\n" +
                "  \"message\": \"Updated: Even better than before! Highly recommend.\",\n" +
                "  \"rating\": 5\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(updatePath)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(payload)
                .log().all()
                .put();

        System.out.println("Update Status Code: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Testimonial should be updated");
    }

    // TEST 3: DELETE TESTIMONIAL (uses PATH PARAMETER)
    @Test(priority = 3, dependsOnMethods = "updateTestimonialTest")
    public void deleteTestimonialTest() {
        String deletePath = "/testimonials/" + testimonialId;

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(deletePath)
                .header("Authorization", "Bearer " + adminToken)
                .log().all()
                .delete();

        System.out.println("Delete Status Code: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Testimonial should be deleted");
    }

    // TEST 4: NEGATIVE TEST - Delete with invalid ID (expect 404)
    @Test(priority = 4)
    public void negativeDeleteTestimonialTest() {
        String invalidId = "invalid-id-12345";
        String deletePath = "/testimonials/" + invalidId;

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(deletePath)
                .header("Authorization", "Bearer " + adminToken)
                .log().all()
                .delete();

        System.out.println("Negative Delete Status Code: " + response.getStatusCode());

        // NEGATIVE ASSERTION - expect failure
        Assert.assertEquals(response.getStatusCode(), 404,
                "Should return 404 for invalid testimonial ID");
    }
}
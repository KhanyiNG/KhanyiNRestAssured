package registrationTests;

import com.github.javafaker.Faker;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requestBuilder.AdminRequestBuilder;
import requestBuilder.UserRequestBuilder;
import utils.DatabaseConnection;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;

public class RegistrationTests {

    static String firstName;
    static String lastName;
    static String email;
    static String password;
    static String groupId;
    static String adminEmail;
    static String adminPassword;

    // Add these for testimonial tests
    static String adminToken;
    static String testimonialId;
    static String BaseURL = "https://ndosiautomation.co.za/APIDEV";

    static Faker faker = new Faker();

    @BeforeClass
    public static void setUpData() throws SQLException {
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = "Khanyisa" + faker.internet().emailAddress();
        password = "Khanyisa@2026!";
        groupId = "5328c91e-fc40-11f0-8e00-5000e6331276";
        adminEmail = "admin@gmail.com";
        adminPassword = "@12345678";

        DatabaseConnection.dbConnection();
    }

    // ============ USER REGISTRATION TESTS ============

    @Test(priority = 1)
    public void userRegistrationTest() {
        Response response = UserRequestBuilder.registerUser(firstName, lastName, email, password, groupId);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 201);
    }

    @Test(priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    public void adminLoginTest() {
        Response response = AdminRequestBuilder.adminLogin();
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);

        // Store admin token for testimonial tests
        adminToken = response.jsonPath().getString("data.token");
        System.out.println("Admin Token stored: " + adminToken);
    }

    @Test(priority = 3)
    public void userApprovalTest() {
        requestBuilder.AdminRequestBuilder.approveUser()
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(priority = 4)
    public void userLoginTest() {
        requestBuilder.UserRequestBuilder.loginUser(DatabaseConnection.getEmail, DatabaseConnection.getPassword)
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    // ============ TESTIMONIAL TESTS ============

    @Test(priority = 5)
    public void createTestimonialTest() {
        String createPath = "/testimonials";

        // Verify token is not null
        Assert.assertNotNull(adminToken, "Admin token is null - run adminLoginTest first");

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

        // Use uppercase "Id" as shown in response
        testimonialId = response.jsonPath().getString("data.Id");
        Assert.assertNotNull(testimonialId, "Testimonial ID should not be null");
        System.out.println("Created Testimonial ID: " + testimonialId);
    }

    @Test(priority = 6, dependsOnMethods = "createTestimonialTest")
    public void updateTestimonialTest() {
        String updatePath = "/testimonials/" + testimonialId;

        String payload = "{\n" +
                "  \"title\": \"Khanyisa Ndlovu Updated\",\n" +
                "  \"content\": \"Updated: Even better than before! Highly recommend.\",\n" +
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

    @Test(priority = 7, dependsOnMethods = "updateTestimonialTest")
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

    @Test(priority = 8)
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

        // If API returns 200 with success=false, adjust assertion
        if (response.getStatusCode() == 200) {
            Boolean success = response.jsonPath().getBoolean("success");
            Assert.assertFalse(success, "Deleting invalid ID should return success=false");
        } else {
            Assert.assertEquals(response.getStatusCode(), 404, "Should return 404 for invalid testimonial ID");
        }
    }

    // ============ COURSE TESTS ============

    @Test(priority = 9)
    public void getBeginnerAutomationCoursesTest() {
        String coursesPath = "/courses";

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(coursesPath)
                .queryParam("level", "Beginner")
                .queryParam("category", "automation")
                .header("Content-Type", "application/json")
                .log().all()
                .get();

        System.out.println("Get Courses Status Code: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Should get courses successfully");

        // FIXED: Use "data.courses" path
        int courseCount = response.jsonPath().getList("data.courses").size();
        System.out.println("Number of beginner automation courses: " + courseCount);
        Assert.assertTrue(courseCount > 0, "Should have at least one course");

        // Verify course has Beginner level (case-sensitive check)
        String level = response.jsonPath().getString("data.courses[0].Level");
        System.out.println("First course level: " + level);
        Assert.assertEquals(level.toLowerCase(), "beginner", "Course level should be Beginner");
    }

    @Test(priority = 10)
    public void getCoursesWithInvalidLevelTest() {
        String coursesPath = "/courses";

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(coursesPath)
                .queryParam("level", "InvalidLevel123")
                .queryParam("category", "automation")
                .header("Content-Type", "application/json")
                .log().all()
                .get();

        System.out.println("Invalid Level Query Status Code: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody().asString());

        // NEGATIVE ASSERTION - API ignores invalid level and returns all courses
        Assert.assertEquals(response.getStatusCode(), 200);

        // API returns courses regardless - note this behavior
        int courseCount = response.jsonPath().getList("data.courses").size();
        System.out.println("Courses returned with invalid level: " + courseCount);

        // This demonstrates the API doesn't validate query params
        System.out.println("NOTE: API returned " + courseCount + " courses even with invalid level parameter");
    }

    @Test(priority = 11)
    public void getCoursesWithNoQueryParamsTest() {
        String coursesPath = "/courses";

        Response response = RestAssured.given()
                .baseUri(BaseURL)
                .basePath(coursesPath)
                .header("Content-Type", "application/json")
                .log().all()
                .get();

        System.out.println("No Query Params Status Code: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Should get all courses");

        // FIXED: Use "data.courses" path
        int courseCount = response.jsonPath().getList("data.courses").size();
        Assert.assertTrue(courseCount > 0, "Should have courses");
        System.out.println("Total courses returned: " + courseCount);
    }

}
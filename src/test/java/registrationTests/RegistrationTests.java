package registrationTests;

import com.github.javafaker.Faker;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
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

    @Test (priority = 1)
    public void userRegistrationTest() {
        Response response = UserRequestBuilder.registerUser(firstName, lastName, email, password, groupId);
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 201);

    }

    @Test (priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    public void adminLoginTest() {
        Response response = AdminRequestBuilder.adminLogin();
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);

    }


    @Test (priority = 3)
    public void userApprovalTest() {
        requestBuilder.AdminRequestBuilder.approveUser()
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));

    }

    @Test (priority = 4)
    public void userLoginTest() {
       requestBuilder.UserRequestBuilder.loginUser(DatabaseConnection.getEmail, DatabaseConnection.getPassword)
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));

    }

}

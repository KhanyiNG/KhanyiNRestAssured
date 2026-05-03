package requestBuilder;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payloadBuilder.UserPayload;

import static commons.Paths.BASE_URL;
import static io.restassured.RestAssured.given;

public class UserRequestBuilder {

    static String registeredUserId;
    static String userToken;

    public static Response registerUser(String firstName, String lastName, String email, String password, String groupId) {

        String apiPath = "/APIDEV/register";
        Response response = given()
                    .baseUri(BASE_URL)
                    .basePath(apiPath)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(UserPayload.registerUserPayload(firstName, lastName, email, password, groupId))
                .when()
                    .post()
                .then()
                    .extract().response();
        registeredUserId = response.jsonPath().getString("data.id");
        return response;

    }

    public static Response loginUser(String email, String password) {

        String apiPath = "/APIDEV/login";
        Response response = given()
                    .baseUri(BASE_URL)
                    .basePath(apiPath)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(UserPayload.userLoginPayload(email, password))
                    .post()
                .then()
                    .extract().response();
        userToken = response.jsonPath().getString("data.token");
        return response;

    }

}

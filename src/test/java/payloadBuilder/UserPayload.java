package payloadBuilder;

import org.json.simple.JSONObject;

public class UserPayload {


    public static JSONObject userLoginPayload(String email, String password) {
        JSONObject userLogin = new JSONObject(); //instantiate userLogin object of type JSONObject
        userLogin.put("email", email); //putting key-value pairs in the userLogin object
        userLogin.put("password", password);

        return userLogin;
    }

    public static JSONObject registerUserPayload(String firstName, String lastName, String email, String password, String groupId) {
        JSONObject registerUser = new JSONObject();
        registerUser.put("firstName", firstName);
        registerUser.put("lastName", lastName);
        registerUser.put("email", email);
        registerUser.put("password", password);
        registerUser.put("confirmPassword", password);
        registerUser.put("groupId", groupId);

        return registerUser;
    }

  
}

package Application.APILayer.controllers;

import Application.APILayer.TokenHandler;
import Application.Entities.users.User;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {
    UserService userService;
    TokenHandler tokenHandler;

    @Autowired
    public UserController(UserService userService, RepositoryFactory repositoryFactory){
        this.userService = userService;
        tokenHandler = TokenHandler.getInstance();
        tokenHandler.init(repositoryFactory);
    }

    @PostMapping(path = "/register")
    @ResponseBody
    public Response<User> register(@RequestBody String inputJson) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            Response<User> response = userService.register(jsonObj);
            return response;
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @PostMapping(path = "/login")
    @ResponseBody
    public Response<User> login(@RequestBody String inputJson) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            String username = jsonObj.getString("username");
            String password = jsonObj.getString("password");
            return userService.login(username, password);
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

}

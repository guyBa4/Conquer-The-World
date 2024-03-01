package Application.APILayer;

import Application.Entities.*;
import Application.Response;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;

public class JsonToInstance {
    private static JsonToInstance instance;

    // Private constructor to prevent instantiation
    private JsonToInstance() {
    }

    // Static method to get the single instance
    public static JsonToInstance getInstance() {
        if (instance == null) {
            instance = new JsonToInstance();
        }
        return instance;
    }

//    set the field named "key" of newInstance a value equals to json['key']
    public void injectFieldFromJson(Object newInstance, JSONObject json, String key) throws NoSuchFieldException, IllegalAccessException {
        if (json.opt(key) == JSONObject.NULL)
            return;
        Field field = newInstance.getClass().getDeclaredField(key);
        field.setAccessible(true); // Set the field to accessible
        field.set(newInstance, json.opt(key));
    }

    public Response<User> buildUser(JSONObject json) {
        User newUser = new User();
        for (String key : json.keySet()) {
            try {
                injectFieldFromJson(newUser, json, key);
            } catch (NoSuchFieldException e) {
                e.printStackTrace(); // Handle the exception as needed
                return Response.fail("Invalid field: " + key);
            } catch (Exception e) {
                e.printStackTrace(); // Handle other exceptions if needed
                return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        System.out.println("New User: " + newUser.getName());
        return Response.ok(newUser);
    }

    public Response<Map> buildMap(JSONObject json) {
        Map newMap = new Map();
        for (String key : json.keySet()) {
            try {
                injectFieldFromJson(newMap, json, key);
            } catch (NoSuchFieldException e) {
                e.printStackTrace(); // Handle the exception as needed
                return Response.fail("Invalid field: " + key);
            } catch (Exception e) {
                e.printStackTrace(); // Handle other exceptions if needed
                return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
//        System.out.println("New User: " + newMap());
        return Response.ok(newMap);
    }

    public Response<Question> buildQuestion(JSONObject json) {
        Question newQuestion = new Question();
        for (String key : json.keySet()) {
            try {
                injectFieldFromJson(newQuestion, json, key);
            } catch (NoSuchFieldException e) {
                e.printStackTrace(); // Handle the exception as needed
                return Response.fail("Invalid field: " + key);
            } catch (Exception e) {
                e.printStackTrace(); // Handle other exceptions if needed
                return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
//        System.out.println("New User: " + newMap());
        return Response.ok(newQuestion);
    }

    public Response<Questionnaire> buildQuestionnaire(JSONObject json) {
        Questionnaire newQuestionnaire = new Questionnaire();
        for (String key : json.keySet()) {
            try {
                injectFieldFromJson(newQuestionnaire, json, key);
            } catch (NoSuchFieldException e) {
                e.printStackTrace(); // Handle the exception as needed
                return Response.fail("Invalid field: " + key);
            } catch (Exception e) {
                e.printStackTrace(); // Handle other exceptions if needed
                return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
//        System.out.println("New User: " + newMap());
        return Response.ok(newQuestionnaire);
    }

    public Response<GameInstance> buildGameInstance(JSONObject json) {
        GameInstance newGameInstance = new GameInstance();
        for (String key : json.keySet()) {
            try {
                injectFieldFromJson(newGameInstance, json, key);
            } catch (NoSuchFieldException e) {
                e.printStackTrace(); // Handle the exception as needed
                return Response.fail("Invalid field: " + key);
            } catch (Exception e) {
                e.printStackTrace(); // Handle other exceptions if needed
                return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
//        System.out.println("New User: " + newMap());
        return Response.ok(newGameInstance);
    }

    public Response<Tile> buildTile(JSONObject json) {
        Tile newTile = new Tile();
        for (String key : json.keySet()) {
            try {
                injectFieldFromJson(newTile, json, key);
            } catch (NoSuchFieldException e) {
                e.printStackTrace(); // Handle the exception as needed
                return Response.fail("Invalid field: " + key);
            } catch (Exception e) {
                e.printStackTrace(); // Handle other exceptions if needed
                return Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
//        System.out.println("New User: " + newMap());
        return Response.ok(newTile);
    }
}

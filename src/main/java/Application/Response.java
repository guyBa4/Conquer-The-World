package Application;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class Response<T> {

    int status;
    private T value = null;
    private boolean error = false;
    private String message;

    public static <T> Response<T> ok(T value) {
        Response<T> response = new Response<>();
        response.setValue(value);
        response.setStatus(200);
        return response;
    }

    public static <T> Response<T> fail(String message) {
        Response<T> response = new Response<>();
        response.setMessage(message);
        response.setError(true);
        response.setStatus(400);
        return response;
    }

    public static <T> Response<T> fail(int status, String message) {
        Response<T> response = new Response<>();
        response.setMessage(message);
        response.setError(true);
        response.setStatus(status);
        return response;
    }

    public static <T> Response<T> fail(HttpStatus httpStatus, String message) {
        int status = httpStatus.value();
        Response<T> response = new Response<>();
        response.setMessage(message);
        response.setError(true);
        response.setStatus(status);
        return response;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isError() {
        return error;
    }
    public boolean isSuccessful() {
        return !error;
    }

    private void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    private void setStatus(int status) {
        this.status = status;
    }
    

    public String toString() {
        return "Response{" +
                "status=" + status +
                ", error=" + error +
                ", message='" + message + '\'' +
                ", value=" + value +
                '}';
    }
}

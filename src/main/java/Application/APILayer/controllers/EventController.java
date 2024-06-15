package Application.APILayer.controllers;

import Application.Response;
import Application.ServiceLayer.EventService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(path = "event")
public class EventController {
    
    EventService eventService;
    
    @GetMapping(path = "")
    public Response<SseEmitter> getEmitter(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return Response.fail("Missing authorization header.");
        }
        SseEmitter emitter = eventService.getEmitter(authorizationHeader);
        if  (emitter == null) {
            return Response.fail("No event emitters found for ID " + authorizationHeader);
        }
        return Response.ok(emitter);
    }
    
}

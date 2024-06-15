package Application.APILayer.controllers;

import Application.Response;
import Application.ServiceLayer.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(path = "event")
public class EventController {
    
    private EventService eventService;
    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping(path = "")
    public SseEmitter getEmitter(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            LOG.warn("Missing authorization header");
            return null;
        }
        SseEmitter emitter = eventService.getEmitter(authorizationHeader);
        if  (emitter == null) {
            LOG.warn("No event emitters found for ID " + authorizationHeader);
            return null;
        }
        return emitter;
    }
    
}

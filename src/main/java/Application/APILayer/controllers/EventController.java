package Application.APILayer.controllers;

import Application.Configurations.Configuration;
import Application.Response;
import Application.ServiceLayer.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping(path = "event")
public class EventController {
    
    private final EventService eventService;
    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping(path = "/get_emitter/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter getEmitter(@PathVariable(name = "id") String userId) {
        LOG.info("Get emitter called");
        if (userId == null || userId.isBlank()) {
            LOG.warn("Missing user ID");
            return null;
        }
        SseEmitter emitter = eventService.getOrCreateEmitter(userId);
        if (emitter == null) {
            LOG.warn("No event emitters found for ID " + userId);
            return null;
        }
        LOG.info("Emitter" + emitter);
        return emitter;
    }
    
}

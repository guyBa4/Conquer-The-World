package Application.APILayer.controllers;

import Application.Events.Event;
import Application.Response;
import Application.ServiceLayer.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "event")
public class EventController {
    
    private final EventService eventService;
    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);
    
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping(path = "/get_events/{runningId}/index/{index}")
    @ResponseBody
    public Response<List<Event>> getEvents(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                           @PathVariable(name = "runningId") String runningId, @PathVariable(name = "index") int eventIndex) {
        if (runningId == null || eventIndex < 0) {
            LOG.warn("Received bad variables in getEvents, runningId {} eventIndex {}", runningId, eventIndex);
            return Response.fail("Invalid request");
        }
        List<Event> eventList = eventService.getEventsFromIndex(UUID.fromString(runningId), eventIndex);
        return Response.ok(eventList);
    }
    
}

package Application.ServiceLayer;

import Application.Events.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Scope("singleton")
@Service
public class EventService {
    
    private final static Logger LOG = LoggerFactory.getLogger(EventService.class);
    private final Map<UUID, List<Event>> events;
    
    public EventService() {
        events = new HashMap<>();
    }
    
    public void addEvent(UUID runningId, Event event) {
        if (!events.containsKey(runningId)) {
            events.put(runningId, new ArrayList<>());
        }
        synchronized (events.get(runningId)) {
            int eventIndex = events.get(runningId).size();
            event.setEventIndex(eventIndex);
            events.get(runningId).add(event);
            LOG.debug("Added event of type {} to running game {}", event.getEventType(), runningId);
        }
    }
    
    public List<Event> getEventsFromIndex(UUID runningId, int eventIndex) {
        if (!events.containsKey(runningId)) {
            LOG.warn("No running game instance with ID {}", runningId.toString());
            throw new IllegalArgumentException("No running game instance with ID " + runningId);
        }
        List<Event> runningGameEventList = events.get(runningId);
        if (runningGameEventList.size() < eventIndex) {
            LOG.warn("Invalid event index {} for running game with ID {}", eventIndex, runningId);
            throw new IllegalArgumentException("Invalid event index " + eventIndex + " for running game with ID " + runningId.toString());
        }
        if (runningGameEventList.size() == eventIndex) {
            return new ArrayList<>();
        }
        return runningGameEventList.subList(eventIndex, runningGameEventList.size());
    }
    
    public void addNewEventList(UUID gameId) {
        if (!events.containsKey(gameId))
            events.put(gameId, new ArrayList<>());
    }
    
    public int getUpdatedEventIndex(UUID gameId) {
        if (events.containsKey(gameId))
            return events.get(gameId).size();
        return 0;
    }
}
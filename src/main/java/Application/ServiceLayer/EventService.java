package Application.ServiceLayer;

import Application.Configurations.Configuration;
import Application.Events.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

import static java.util.logging.Logger.getLogger;

@Scope("singleton")
@Service
public class EventService {
    
    private final static Logger LOG = LoggerFactory.getLogger(EventService.class);
    
    private final List<Event> awaitingEvents;
    private final Map<UUID, SseEmitter> emitters;
    
    public void addEvent(Event event) {
        if (event.getRecipients() == null || event.getRecipients().isEmpty())
            LOG.warn("Failed to add event");
        else awaitingEvents.add(event);
    }
    
    public EventService() {
        this.emitters = new HashMap<>();
        this.awaitingEvents = new ArrayList<>();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!awaitingEvents.isEmpty()) {
                    awaitingEvents.stream().peek((event) ->
                            event.getRecipients().stream().peek((recipient) -> {
                                try {
                                    SseEmitter emitter = emitters.get(recipient.getId());
                                    if (emitter != null)
                                        emitter.send(event);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    LOG.warn("Failed to send event to recipient");
                                }
                            }));
                    awaitingEvents.clear();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0L, 1000L);
    }
    
    public SseEmitter addEmitter(UUID recipientId) {
        if (recipientId != null) {
            SseEmitter emitter = new SseEmitter(Configuration.defaultSseEmitterTimeout);
            this.emitters.put(recipientId, emitter);
            return emitter;
        }
        return null;
    }
    
    public SseEmitter getEmitter(String emitterRefId) {
        UUID userId = UUID.fromString(emitterRefId);
        return emitters.get(userId);
    }
    
    public SseEmitter getOrCreateEmitter(String emitterRefId) {
        UUID userId = UUID.fromString(emitterRefId);
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null)
            LOG.debug("Emitter not found");
            emitter = addEmitter(userId);
        return emitter;
    }
}

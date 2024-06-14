package Application.ServiceLayer;

import Application.Events.Event;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@Service
public class EventService {
    
    private final static Logger LOG = getLogger(EventService.class.toString());
    
    private final List<Event> awaitingEvents;
    
    public void addEvent(Event event) {
        if (event.getRecipients() == null || event.getRecipients().isEmpty())
            LOG.warning("Failed to add event");
        else awaitingEvents.add(event);
    }
    
    public EventService() {
        awaitingEvents = new ArrayList<>();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!awaitingEvents.isEmpty()) {
                    awaitingEvents.stream().peek((event) ->
                            event.getRecipients().stream().peek((recipient) -> {
                                try {
                                    recipient.getEventEmitter().send(event);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    LOG.warning("Failed to send event to recipient");
                                }
                            }));
                    awaitingEvents.clear();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0L, 1000L);
    }
}

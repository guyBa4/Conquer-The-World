package Application.Events;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EventRecipient {
    
    public SseEmitter getEventEmitter();
}

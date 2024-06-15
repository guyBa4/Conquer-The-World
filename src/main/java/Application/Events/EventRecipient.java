package Application.Events;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface EventRecipient {
    
    public UUID getId();
}

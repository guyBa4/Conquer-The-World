package Application.Events;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

public class Event {
    private EventType eventType;
    private String message;
    private Object body;
    private Date timestamp;
    
    @JsonIgnore
    private List<EventRecipient> recipients;
    
    public EventType getEventType() {
        return eventType;
    }
    
    public Event setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Event setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public Object getBody() {
        return body;
    }
    
    public Event setBody(Object body) {
        this.body = body;
        return this;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public Event setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    public List<EventRecipient> getRecipients() {
        return recipients;
    }
    
    public Event setRecipients(List<EventRecipient> recipients) {
        this.recipients = recipients;
        return this;
    }
}

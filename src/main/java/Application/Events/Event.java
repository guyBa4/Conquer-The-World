package Application.Events;

import java.util.Date;

public class Event {
    private EventType eventType;
    private String message;
    private Object body;
    private Date timestamp;
    private int eventIndex;
    
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
    
    public int getEventIndex() {
        return eventIndex;
    }
    
    public Event setEventIndex(int eventIndex) {
        this.eventIndex = eventIndex;
        return this;
    }
}
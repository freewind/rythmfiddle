package demo;

import java.util.UUID;

public class Bar {
    public String id;
    
    public Bar() {
        id = UUID.randomUUID().toString();
    }

    public Bar(String id) {
        if (null == id) {
            throw new NullPointerException();
        }
        this.id = id;
    }
    
    public String toString() {
        return String.format("Bar[%s]", id);
    }
}

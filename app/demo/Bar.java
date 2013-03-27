package demo;

import com.greenlaw110.rythm.Rythm;

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
        return Rythm.substitute("Bar[@1]", id);
    }
}

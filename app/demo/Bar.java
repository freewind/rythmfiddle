package demo;

import com.greenlaw110.rythm.play.RythmPlugin;

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
        return RythmPlugin.substitute("Bar[@1]", id);
    }
}

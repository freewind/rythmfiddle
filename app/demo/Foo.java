package demo;

import com.greenlaw110.rythm.play.RythmPlugin;

public class Foo {
    public Bar bar;
    
    public Foo() {
        bar = new Bar();
    }
    
    public Foo(Bar bar) {
        this.bar = new Bar(bar.id);
    }

    public Foo(String barId) {
        this.bar = new Bar(barId);
    }
    
    public String toString() {
        return RythmPlugin.substitute("Foo[@1]", bar);
    }
}

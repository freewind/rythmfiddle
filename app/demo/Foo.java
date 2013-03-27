package demo;

import com.greenlaw110.rythm.Rythm;

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
        return Rythm.substitute("Foo[@1]", bar);
    }
}

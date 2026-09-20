package p000;

import java.util.List;

/* renamed from: Instantiation */
public class Instantiation implements Expression {
    private List args;
    private String name;

    public Instantiation(String str, List list) {
        this.name = str;
        this.args = list;
    }

    public Object eval(Environment environment) {
        return World.instantiate(this.name, this.args, environment);
    }
}

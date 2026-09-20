package p000;

/* renamed from: Variable */
public class Variable implements Expression {
    private String name;

    public Variable(String str) {
        this.name = str;
    }

    public Object eval(Environment environment) {
        return environment.get(this.name);
    }
}

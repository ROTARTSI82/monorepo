package p000;

/* renamed from: This */
public class This implements Expression {
    public Object eval(Environment environment) {
        return environment.getOwner();
    }
}

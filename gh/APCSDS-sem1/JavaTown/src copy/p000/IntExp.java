package p000;

/* renamed from: IntExp */
public class IntExp implements Expression {
    private int value;

    public IntExp(int i) {
        this.value = i;
    }

    public Object eval(Environment environment) {
        return new Integer(this.value);
    }
}

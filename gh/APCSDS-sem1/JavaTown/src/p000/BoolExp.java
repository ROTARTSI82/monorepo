package p000;

/* renamed from: BoolExp */
public class BoolExp implements Expression {
    private boolean value;

    public BoolExp(boolean z) {
        this.value = z;
    }

    public Object eval(Environment environment) {
        return this.value ? Boolean.TRUE : Boolean.FALSE;
    }
}

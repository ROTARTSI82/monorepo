package p000;

/* renamed from: StringExp */
public class StringExp implements Expression {
    private String value;

    public StringExp(String str) {
        this.value = str;
    }

    public Object eval(Environment environment) {
        return this.value;
    }
}

package p000;

/* renamed from: ReturnException */
public class ReturnException extends RuntimeException {
    private Object value;

    public ReturnException(Object obj) {
        this.value = obj;
    }

    public Object getValue() {
        return this.value;
    }
}

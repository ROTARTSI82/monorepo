/*
 * Decompiled with CFR 0.150.
 */
public class ReturnException
extends RuntimeException {
    private Object value;

    public ReturnException(Object object) {
        this.value = object;
    }

    public Object getValue() {
        return this.value;
    }
}


// 
// Decompiled by Procyon v0.5.36
// 

public class ReturnException extends RuntimeException
{
    private Object value;
    
    public ReturnException(final Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }
}

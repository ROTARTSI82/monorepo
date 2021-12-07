// 
// Decompiled by Procyon v0.5.36
// 

public class IntExp implements Expression
{
    private int value;
    
    public IntExp(final int value) {
        this.value = value;
    }
    
    public Object eval(final Environment environment) {
        return new Integer(this.value);
    }
}

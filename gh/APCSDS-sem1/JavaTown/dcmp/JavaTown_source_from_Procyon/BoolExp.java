// 
// Decompiled by Procyon v0.5.36
// 

public class BoolExp implements Expression
{
    private boolean value;
    
    public BoolExp(final boolean value) {
        this.value = value;
    }
    
    public Object eval(final Environment environment) {
        return this.value ? Boolean.TRUE : Boolean.FALSE;
    }
}

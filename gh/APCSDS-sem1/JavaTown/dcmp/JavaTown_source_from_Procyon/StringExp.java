// 
// Decompiled by Procyon v0.5.36
// 

public class StringExp implements Expression
{
    private String value;
    
    public StringExp(final String value) {
        this.value = value;
    }
    
    public Object eval(final Environment environment) {
        return this.value;
    }
}

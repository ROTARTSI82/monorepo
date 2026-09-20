// 
// Decompiled by Procyon v0.5.36
// 

public class Variable implements Expression
{
    private String name;
    
    public Variable(final String name) {
        this.name = name;
    }
    
    public Object eval(final Environment environment) {
        return environment.get(this.name);
    }
}

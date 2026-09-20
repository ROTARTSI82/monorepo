import java.util.List;

// 
// Decompiled by Procyon v0.5.36
// 

public class Instantiation implements Expression
{
    private String name;
    private List args;
    
    public Instantiation(final String name, final List args) {
        this.name = name;
        this.args = args;
    }
    
    public Object eval(final Environment environment) {
        return World.instantiate(this.name, this.args, environment);
    }
}

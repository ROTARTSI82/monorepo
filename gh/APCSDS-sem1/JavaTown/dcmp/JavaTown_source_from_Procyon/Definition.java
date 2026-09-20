import java.util.List;

// 
// Decompiled by Procyon v0.5.36
// 

public class Definition implements Statement
{
    private String name;
    private List decls;
    
    public Definition(final String name, final List decls) {
        this.name = name;
        this.decls = decls;
    }
    
    public void exec(final Environment environment) {
        World.addClass(new Type(this.name, this.decls));
    }
}

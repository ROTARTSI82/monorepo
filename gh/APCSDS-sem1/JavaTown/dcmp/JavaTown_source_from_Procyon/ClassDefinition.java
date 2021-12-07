import java.util.Set;

// 
// Decompiled by Procyon v0.5.36
// 

public class ClassDefinition extends Statement
{
    private String name;
    
    public ClassDefinition(final String name, final Set set) {
        this.name = name;
    }
    
    public void exec(final Environment environment) {
        Office.addClass(new WorkerClass(this.name));
    }
}

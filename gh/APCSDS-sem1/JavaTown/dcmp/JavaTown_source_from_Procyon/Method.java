import java.util.Iterator;
import java.util.List;

// 
// Decompiled by Procyon v0.5.36
// 

public class Method extends Declaration
{
    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;
    private int access;
    private String name;
    private List params;
    private Block body;
    
    public Method(final int access, final String name, final List params, final Block body) {
        this.access = access;
        this.name = name;
        this.params = params;
        this.body = body;
    }
    
    public String getParam(final int n) {
        return this.params.get(n);
    }
    
    public Iterator body() {
        return this.body.getStatements().iterator();
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getParamCount() {
        return this.params.size();
    }
    
    public boolean isPublic() {
        return this.access == 0;
    }
}

import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

// 
// Decompiled by Procyon v0.5.36
// 

public class Type
{
    private String name;
    private List<Field> fields;
    private Set<Method> methods;
    
    public Type(final String s, final List list) {
        this.fields = new ArrayList<Field>();
        this.methods = new HashSet<Method>();
        this.name = s;
        final Iterator<Declaration> iterator = list.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            final Declaration obj = iterator.next();
            if (obj instanceof Field) {
                this.fields.add((Field)obj);
            }
            else {
                if (!(obj instanceof Method)) {
                    throw new JavaTownException("Invalid declaration:  " + obj);
                }
                final Method method = (Method)obj;
                if (method.getName().equals(s)) {
                    b = true;
                }
                this.methods.add(method);
            }
        }
        if (!b) {
            this.methods.add(new Method(0, s, new ArrayList(), new Block(new ArrayList<Statement>())));
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public Iterator<Field> fields() {
        return this.fields.iterator();
    }
    
    public Method getMethod(final String s, final int i) {
        for (final Method method : this.methods) {
            if (method.getName().equals(s) && method.getParamCount() == i) {
                return method;
            }
        }
        throw new JavaTownException("No method " + s + " with " + i + " parameters");
    }
}

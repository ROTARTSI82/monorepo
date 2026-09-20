import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

// 
// Decompiled by Procyon v0.5.36
// 

public class Environment
{
    private List<String> vars;
    private Map<String, Object> values;
    private Environment parent;
    private String name;
    private Person owner;
    
    public Environment(final String name, final Environment parent, final Person owner) {
        this.vars = new ArrayList<String>();
        this.values = new HashMap<String, Object>();
        this.name = name;
        this.parent = parent;
        this.owner = owner;
    }
    
    public void set(final String s, final Object o) {
        if (this.values.containsKey(s)) {
            this.values.put(s, o);
        }
        else if (this.parent != null && this.parent.containsVar(s)) {
            this.parent.set(s, o);
        }
        else {
            this.vars.add(s);
            this.values.put(s, o);
        }
    }
    
    public void define(final String s, final Object o) {
        this.vars.add(s);
        this.values.put(s, o);
    }
    
    public Iterator variables() {
        return this.vars.iterator();
    }
    
    public Object get(final String str) {
        if (this.containsVar(str)) {
            return this.values.get(str);
        }
        if (this.parent == null) {
            throw new JavaTownException("Unbound variable:  " + str);
        }
        return this.parent.get(str);
    }
    
    public boolean containsVar(final String s) {
        return this.values.containsKey(s);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Person getOwner() {
        return this.owner;
    }
    
    public void clear() {
        this.vars = new ArrayList<String>();
        this.values = new HashMap<String, Object>();
    }
}

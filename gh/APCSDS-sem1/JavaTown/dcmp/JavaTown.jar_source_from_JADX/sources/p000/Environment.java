package p000;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* renamed from: Environment */
public class Environment {
    private String name;
    private Person owner;
    private Environment parent;
    private Map<String, Object> values = new HashMap();
    private List<String> vars = new ArrayList();

    public Environment(String str, Environment environment, Person person) {
        this.name = str;
        this.parent = environment;
        this.owner = person;
    }

    public void set(String str, Object obj) {
        if (this.values.containsKey(str)) {
            this.values.put(str, obj);
        } else if (this.parent == null || !this.parent.containsVar(str)) {
            this.vars.add(str);
            this.values.put(str, obj);
        } else {
            this.parent.set(str, obj);
        }
    }

    public void define(String str, Object obj) {
        this.vars.add(str);
        this.values.put(str, obj);
    }

    public Iterator variables() {
        return this.vars.iterator();
    }

    public Object get(String str) {
        if (containsVar(str)) {
            return this.values.get(str);
        }
        if (this.parent != null) {
            return this.parent.get(str);
        }
        throw new JavaTownException("Unbound variable:  " + str);
    }

    public boolean containsVar(String str) {
        return this.values.containsKey(str);
    }

    public String getName() {
        return this.name;
    }

    public Person getOwner() {
        return this.owner;
    }

    public void clear() {
        this.vars = new ArrayList();
        this.values = new HashMap();
    }
}

/*
 * Decompiled with CFR 0.150.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Environment {
    private List<String> vars = new ArrayList<String>();
    private Map<String, Object> values = new HashMap<String, Object>();
    private Environment parent;
    private String name;
    private Person owner;

    public Environment(String string, Environment environment, Person person) {
        this.name = string;
        this.parent = environment;
        this.owner = person;
    }

    public void set(String string, Object object) {
        if (this.values.containsKey(string)) {
            this.values.put(string, object);
        } else if (this.parent != null && this.parent.containsVar(string)) {
            this.parent.set(string, object);
        } else {
            this.vars.add(string);
            this.values.put(string, object);
        }
    }

    public void define(String string, Object object) {
        this.vars.add(string);
        this.values.put(string, object);
    }

    public Iterator variables() {
        return this.vars.iterator();
    }

    public Object get(String string) {
        if (this.containsVar(string)) {
            return this.values.get(string);
        }
        if (this.parent == null) {
            throw new JavaTownException("Unbound variable:  " + string);
        }
        return this.parent.get(string);
    }

    public boolean containsVar(String string) {
        return this.values.containsKey(string);
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


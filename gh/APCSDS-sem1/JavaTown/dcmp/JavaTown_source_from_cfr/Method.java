/*
 * Decompiled with CFR 0.150.
 */
import java.util.Iterator;
import java.util.List;

public class Method
extends Declaration {
    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;
    private int access;
    private String name;
    private List params;
    private Block body;

    public Method(int n, String string, List list, Block block) {
        this.access = n;
        this.name = string;
        this.params = list;
        this.body = block;
    }

    public String getParam(int n) {
        return (String)this.params.get(n);
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


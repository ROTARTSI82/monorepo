package p000;

import java.util.Iterator;
import java.util.List;

/* renamed from: Method */
public class Method extends Declaration {
    public static final int PRIVATE = 1;
    public static final int PUBLIC = 0;
    private int access;
    private Block body;
    private String name;
    private List params;

    public Method(int i, String str, List list, Block block) {
        this.access = i;
        this.name = str;
        this.params = list;
        this.body = block;
    }

    public String getParam(int i) {
        return (String) this.params.get(i);
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

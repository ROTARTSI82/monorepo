package p000;

import java.util.List;

/* renamed from: Definition */
public class Definition implements Statement {
    private List decls;
    private String name;

    public Definition(String str, List list) {
        this.name = str;
        this.decls = list;
    }

    public void exec(Environment environment) {
        World.addClass(new Type(this.name, this.decls));
    }
}

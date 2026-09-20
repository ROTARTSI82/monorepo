/*
 * Decompiled with CFR 0.150.
 */
import java.util.List;

public class Definition
implements Statement {
    private String name;
    private List decls;

    public Definition(String string, List list) {
        this.name = string;
        this.decls = list;
    }

    public void exec(Environment environment) {
        World.addClass(new Type(this.name, this.decls));
    }
}


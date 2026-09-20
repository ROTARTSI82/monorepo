package p000;

import java.util.Set;

/* renamed from: ClassDefinition */
public class ClassDefinition implements Statement {
    private String name;

    public ClassDefinition(String str, Set set) {
        this.name = str;
    }

    public void exec(Environment environment) {
        // Office.addClass(new WorkerClass(this.name));
    }
}

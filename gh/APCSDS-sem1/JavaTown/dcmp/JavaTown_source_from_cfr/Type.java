/*
 * Decompiled with CFR 0.150.
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Type {
    private String name;
    private List<Field> fields = new ArrayList<Field>();
    private Set<Method> methods = new HashSet<Method>();

    public Type(String string, List list) {
        this.name = string;
        Iterator iterator = list.iterator();
        boolean bl = false;
        while (iterator.hasNext()) {
            Declaration declaration = (Declaration)iterator.next();
            if (declaration instanceof Field) {
                this.fields.add((Field)declaration);
                continue;
            }
            if (declaration instanceof Method) {
                Method method = (Method)declaration;
                if (method.getName().equals(string)) {
                    bl = true;
                }
                this.methods.add(method);
                continue;
            }
            throw new JavaTownException("Invalid declaration:  " + declaration);
        }
        if (!bl) {
            this.methods.add(new Method(0, string, new ArrayList(), new Block(new ArrayList<Statement>())));
        }
    }

    public String getName() {
        return this.name;
    }

    public Iterator<Field> fields() {
        return this.fields.iterator();
    }

    public Method getMethod(String string, int n) {
        for (Method method : this.methods) {
            if (!method.getName().equals(string) || method.getParamCount() != n) continue;
            return method;
        }
        throw new JavaTownException("No method " + string + " with " + n + " parameters");
    }
}


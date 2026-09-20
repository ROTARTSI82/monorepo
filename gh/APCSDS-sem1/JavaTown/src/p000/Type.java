package p000;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* renamed from: Type */
public class Type {
    private List<Field> fields = new ArrayList();
    private Set<Method> methods = new HashSet();
    private String name;

    public Type(String str, List list) {
        this.name = str;
        Iterator it = list.iterator();
        boolean z = false;
        while (it.hasNext()) {
            Declaration declaration = (Declaration) it.next();
            if (declaration instanceof Field) {
                this.fields.add((Field) declaration);
            } else if (declaration instanceof Method) {
                Method method = (Method) declaration;
                z = method.getName().equals(str) ? true : z;
                this.methods.add(method);
            } else {
                throw new JavaTownException("Invalid declaration:  " + declaration);
            }
            z = z;
        }
        if (!z) {
            this.methods.add(new Method(0, str, new ArrayList(), new Block(new ArrayList())));
        }
    }

    public String getName() {
        return this.name;
    }

    public Iterator<Field> fields() {
        return this.fields.iterator();
    }

    public Method getMethod(String str, int i) {
        for (Method next : this.methods) {
            if (next.getName().equals(str) && next.getParamCount() == i) {
                return next;
            }
        }
        throw new JavaTownException("No method " + str + " with " + i + " parameters");
    }
}

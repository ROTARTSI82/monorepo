package p000;

import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: Block */
public class Block implements Statement {
    private ArrayList<Statement> statements;

    public Block(ArrayList<Statement> arrayList) {
        this.statements = arrayList;
    }

    public ArrayList<Statement> getStatements() {
        return this.statements;
    }

    public void exec(Environment environment) {
        Iterator<Statement> it = this.statements.iterator();
        while (it.hasNext()) {
            it.next().exec(environment);
        }
    }
}

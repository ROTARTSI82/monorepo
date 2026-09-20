/*
 * Decompiled with CFR 0.151.
 */
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Block
implements Statement {
    private ArrayList<Statement> statements;

    public Block(ArrayList<Statement> arrayList) {
        this.statements = arrayList;
    }

    public ArrayList<Statement> getStatements() {
        return this.statements;
    }

    @Override
    public void exec(Environment environment) {
        for (Statement statement : this.statements) {
            statement.exec(environment);
        }
    }
}


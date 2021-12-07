import java.util.Iterator;
import java.util.ArrayList;

// 
// Decompiled by Procyon v0.5.36
// 

public class Block implements Statement
{
    private ArrayList<Statement> statements;
    
    public Block(final ArrayList<Statement> statements) {
        this.statements = statements;
    }
    
    public ArrayList<Statement> getStatements() {
        return this.statements;
    }
    
    public void exec(final Environment environment) {
        final Iterator<Statement> iterator = this.statements.iterator();
        while (iterator.hasNext()) {
            iterator.next().exec(environment);
        }
    }
}

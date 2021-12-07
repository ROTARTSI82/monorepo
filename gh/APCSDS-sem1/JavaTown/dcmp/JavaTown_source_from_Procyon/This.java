// 
// Decompiled by Procyon v0.5.36
// 

public class This implements Expression
{
    public Object eval(final Environment environment) {
        return environment.getOwner();
    }
}

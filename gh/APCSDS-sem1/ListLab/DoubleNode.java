/**
 * A node in a doubly-linked list.
 * @author Grant Yang
 * @version 2021.10.09
 */
public class DoubleNode
{
    private Object value;
    private DoubleNode previous;
    private DoubleNode next;

    public DoubleNode(Object v)
    {
        value = v;
        previous = null;
        next = null;
    }

    public Object getValue()
    {
        return value;
    }

    public DoubleNode getPrevious()
    {
        return previous;
    }

    public DoubleNode getNext()
    {
        return next;
    }

    public void setValue(Object v)
    {
        value = v;
    }

    public void setPrevious(DoubleNode p)
    {
        previous = p;
    }

    public void setNext(DoubleNode n)
    {
        next = n;
    }
}
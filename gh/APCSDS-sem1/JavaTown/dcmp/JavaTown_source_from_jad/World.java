// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   World.java

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class World extends JComponent
    implements ActionListener, DocumentListener
{

    public World()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        frame = new JFrame();
        frame.setTitle("JavaTown 0.6");
        frame.setDefaultCloseOperation(3);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), 2));
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, 3));
        frame.getContentPane().add(jpanel);
        transcript = new JTextArea(15, 15);
        transcript.setEditable(false);
        transcript.setTabSize(2);
        transcript.setText("Transcript:\n");
        Font font = transcript.getFont();
        font = new Font(font.getName(), font.getStyle(), 14);
        transcript.setFont(font);
        JScrollPane jscrollpane = new JScrollPane(transcript);
        jpanel.add(jscrollpane);
        world = new World();
        codeArea = new JTextArea(15, 15);
        codeArea.setTabSize(2);
        codeArea.setText("[Enter code here]");
        codeArea.getDocument().addDocumentListener(world);
        codeArea.setFont(font);
        jscrollpane = new JScrollPane(codeArea);
        jpanel.add(codeArea);
        JPanel jpanel1 = new JPanel();
        jpanel1.setLayout(new BoxLayout(jpanel1, 2));
        jpanel.add(jpanel1);
        jpanel1.add(Box.createHorizontalGlue());
        execButton = new JButton("Execute");
        execButton.setMnemonic(69);
        execButton.setActionCommand("execute");
        execButton.addActionListener(world);
        execButton.setEnabled(false);
        jpanel1.add(execButton);
        frame.getRootPane().setDefaultButton(execButton);
        stepButton = new JButton("Step");
        stepButton.setMnemonic(83);
        stepButton.setActionCommand("step");
        stepButton.addActionListener(world);
        stepButton.setEnabled(false);
        jpanel1.add(stepButton);
        JButton jbutton = new JButton("Load");
        jbutton.setMnemonic(76);
        jbutton.setActionCommand("load");
        jbutton.addActionListener(world);
        jpanel1.add(jbutton);
        JButton jbutton1 = new JButton("Clear");
        jbutton1.setMnemonic(67);
        jbutton1.setActionCommand("clear");
        jbutton1.addActionListener(world);
        jpanel1.add(jbutton1);
        world.setPreferredSize(new Dimension(540, 500));
        userEnv = new Environment("User Variables", null, null);
        frame.getContentPane().add(world);
        frame.pack();
        frame.setVisible(true);
        codeArea.selectAll();
        codeArea.requestFocusInWindow();
    }

    protected void paintComponent(Graphics g)
    {
        Graphics2D graphics2d = (Graphics2D)g;
        graphics2d.setPaint(Color.CYAN);
        graphics2d.fill(new java.awt.geom.Rectangle2D.Double(0.0D, 0.0D, getWidth(), 50D));
        graphics2d.setPaint(Color.GREEN.darker());
        graphics2d.fill(new java.awt.geom.Rectangle2D.Double(0.0D, 50D, getWidth(), getHeight() - 50));
        Font font = graphics2d.getFont();
        graphics2d.setFont(new Font(font.getName(), font.getSize(), 14));
        for(int i = 0; i < memory.length; i++)
        {
            for(int j = 0; j < memory[i].length; j++)
            {
                int k = colToLeft(j);
                int l = rowToTop(i);
                graphics2d.setPaint(Color.LIGHT_GRAY);
                graphics2d.fill(new java.awt.geom.Rectangle2D.Double(k, l + 20, 200D, 70D));
                GeneralPath generalpath = new GeneralPath(0, 3);
                generalpath.moveTo(k, l + 20);
                generalpath.lineTo(k + 100, l);
                generalpath.lineTo(k + 200, l + 20);
                generalpath.closePath();
                graphics2d.setPaint(Color.DARK_GRAY);
                graphics2d.fill(generalpath);
                graphics2d.setPaint(Color.BLACK);
                graphics2d.draw(generalpath);
                graphics2d.setPaint(Color.WHITE);
                graphics2d.drawString((new Location(i, j)).toString(), k + 95, l + 15);
                graphics2d.setPaint(Color.BLACK);
                graphics2d.draw(new java.awt.geom.Line2D.Double(k, l + 20, k, l + 90));
                graphics2d.draw(new java.awt.geom.Line2D.Double(k + 200, l + 20, k + 200, l + 90));
                if(memory[i][j] != null)
                    memory[i][j].draw(graphics2d);
            }

        }

        TextBox textbox = new TextBox(1, Color.ORANGE.darker().darker(), 0, 450, getWidth(), getHeight() - 450);
        for(Iterator iterator = userEnv.variables(); iterator.hasNext();)
        {
            String s = (String)iterator.next();
            try
            {
                Object obj = userEnv.get(s);
                if(obj instanceof String)
                    obj = (new StringBuilder()).append("\"").append(obj).append("\"").toString();
                textbox.addText(Color.WHITE, 0, 14, (new StringBuilder()).append(s).append(":  ").append(obj).toString());
            }
            catch(JavaTownException javatownexception) { }
        }

        textbox.draw(graphics2d);
        graphics2d.setColor(Color.BLUE);
        graphics2d.fill(new java.awt.geom.RoundRectangle2D.Double(postmanLeft, postmanTop + 5, 14D, 5D, 5D, 5D));
        graphics2d.setColor(Color.BLACK);
        graphics2d.draw(new java.awt.geom.RoundRectangle2D.Double(postmanLeft, postmanTop + 5, 14D, 5D, 5D, 5D));
        graphics2d.setPaint(Color.YELLOW);
        graphics2d.fill(new java.awt.geom.Ellipse2D.Double(postmanLeft, postmanTop + 10, 14D, 14D));
        graphics2d.setPaint(Color.BLACK);
        graphics2d.draw(new java.awt.geom.Ellipse2D.Double(postmanLeft, postmanTop + 10, 14D, 14D));
        graphics2d.fill(new java.awt.geom.Rectangle2D.Double(postmanLeft + 4, postmanTop + 16, 2D, 2D));
        graphics2d.fill(new java.awt.geom.Rectangle2D.Double(postmanLeft + 9, postmanTop + 16, 2D, 2D));
        graphics2d.draw(new java.awt.geom.Line2D.Double(postmanLeft + 5, postmanTop + 20, postmanLeft + 9, postmanTop + 20));
        graphics2d.draw(new java.awt.geom.Line2D.Double(postmanLeft + 7, postmanTop + 24, postmanLeft + 7, postmanTop + 40));
        graphics2d.draw(new java.awt.geom.Line2D.Double(postmanLeft + 7, postmanTop + 24, postmanLeft, postmanTop + 31));
        graphics2d.draw(new java.awt.geom.Line2D.Double(postmanLeft + 7, postmanTop + 24, postmanLeft + 14, postmanTop + 31));
        if(carryingPerson)
        {
            graphics2d.setPaint(Color.YELLOW);
            graphics2d.fill(new java.awt.geom.Ellipse2D.Double(postmanLeft - 3, postmanTop + 31, 7D, 7D));
            graphics2d.setPaint(Color.BLACK);
            graphics2d.draw(new java.awt.geom.Ellipse2D.Double(postmanLeft - 3, postmanTop + 31, 7D, 7D));
            graphics2d.draw(new java.awt.geom.Line2D.Double(postmanLeft, postmanTop + 38, postmanLeft, postmanTop + 46));
        }
        if(message != null)
        {
            message.setPosition(postmanLeft + 14, postmanTop + 31);
            message.draw(graphics2d);
        }
    }

    public static Type getClass(String s)
    {
        Type type = (Type)classes.get(s);
        if(type == null)
            throw new JavaTownException((new StringBuilder()).append("Class not found:  ").append(s).toString());
        else
            return type;
    }

    public static void addClass(Type type)
    {
        classes.put(type.getName(), type);
        addToTranscript((new StringBuilder()).append("Defined class ").append(type.getName()).toString());
    }

    public static void redraw()
    {
        world.repaint();
        try
        {
            Thread.sleep(250L);
        }
        catch(InterruptedException interruptedexception) { }
        if(isStepping)
        {
            clicked = false;
            execButton.setEnabled(true);
            stepButton.setEnabled(true);
            while(!clicked) 
                try
                {
                    Thread.sleep(1L);
                }
                catch(InterruptedException interruptedexception1) { }
        }
    }

    public static void gc()
    {
        HashSet hashset = new HashSet();
        findPeople(userEnv, hashset);
        for(int i = 0; i < memory.length; i++)
        {
            for(int j = 0; j < memory[i].length; j++)
                if(memory[i][j] != null && !hashset.contains(memory[i][j]))
                {
                    movePostmanTo(i, j);
                    memory[i][j] = null;
                    carryingPerson = true;
                    movePostmanTo(-1, -1);
                    carryingPerson = false;
                }

        }

        movePostmanTo(-1, -1);
    }

    private static void findPeople(Environment environment, Set set)
    {
        Iterator iterator = environment.variables();
        do
        {
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();
            try
            {
                Object obj = environment.get(s);
                if(obj != null && (obj instanceof Person) && !set.contains(obj))
                {
                    Person person = (Person)obj;
                    set.add(person);
                    findPeople(person.getEnvironment(), set);
                    Iterator iterator1 = person.stack();
                    while(iterator1.hasNext()) 
                    {
                        Environment environment1 = (Environment)iterator1.next();
                        findPeople(environment1, set);
                    }
                }
            }
            catch(JavaTownException javatownexception) { }
        } while(true);
    }

    public static Location allocateMemory()
    {
        ArrayList arraylist = new ArrayList();
        for(int i = 0; i < memory.length; i++)
        {
            for(int k = 0; k < memory[i].length; k++)
                if(memory[i][k] == null)
                    arraylist.add(new Location(i, k));

        }

        if(arraylist.size() == 0)
        {
            throw new OutOfMemoryError();
        } else
        {
            int j = random.nextInt(arraylist.size());
            Location location = (Location)arraylist.get(j);
            return location;
        }
    }

    private static String dump()
    {
        String s = "";
        for(int i = 0; i < memory.length; i++)
        {
            for(int j = 0; j < memory[i].length; j++)
                s = (new StringBuilder()).append(s).append(memory[i][j]).append(" ").toString();

        }

        return s;
    }

    private static int rowToTop(int i)
    {
        return 20 + i * 100;
    }

    private static int colToLeft(int i)
    {
        return 20 + i * 300;
    }

    private static void movePostmanTo(int i, int j)
    {
        int k;
        int l;
        if(i == -1 && j == -1)
        {
            k = 400;
            l = 250;
        } else
        {
            k = rowToTop(i) + 20;
            l = colToLeft(j);
            if(j == 0)
                l += 205;
            else
                l -= 20;
        }
        while(postmanTop != k || postmanLeft != l) 
        {
            byte byte0 = 10;
            if(postmanTop > k)
            {
                postmanTop -= byte0;
                if(postmanTop < k)
                    postmanTop = k;
            } else
            if(postmanTop < k)
            {
                postmanTop += byte0;
                if(postmanTop > k)
                    postmanTop = k;
            }
            if(postmanLeft > l)
            {
                postmanLeft -= byte0;
                if(postmanLeft < l)
                    postmanLeft = l;
            } else
            if(postmanLeft < l)
            {
                postmanLeft += byte0;
                if(postmanLeft > l)
                    postmanLeft = l;
            }
            world.repaint();
            try
            {
                Thread.sleep(20L);
            }
            catch(InterruptedException interruptedexception) { }
        }
    }

    public static Person instantiate(String s, java.util.List list, Environment environment)
    {
        ArrayList arraylist = new ArrayList();
        Object obj;
        for(Iterator iterator = list.iterator(); iterator.hasNext(); arraylist.add(obj))
        {
            Expression expression = (Expression)iterator.next();
            obj = expression.eval(environment);
        }

        Person person = environment.getOwner();
        if(person == null)
        {
            movePostmanTo(-1, -1);
        } else
        {
            person.setWaiting(true);
            Location location = person.getLocation();
            movePostmanTo(location.row(), location.col());
        }
        message = new TextBox(1, Color.WHITE);
        message.addText(Color.BLACK, 0, 14, (new StringBuilder()).append("new ").append(s).toString());
        Object obj1;
        for(Iterator iterator1 = arraylist.iterator(); iterator1.hasNext(); message.addText(Color.BLACK, 0, 12, (new StringBuilder()).append(obj1).append("").toString()))
        {
            obj1 = iterator1.next();
            if(obj1 instanceof String)
                obj1 = (new StringBuilder()).append("\"").append(obj1).append("\"").toString();
        }

        redraw();
        movePostmanTo(-1, -1);
        carryingPerson = true;
        Location location1 = allocateMemory();
        movePostmanTo(location1.row(), location1.col());
        carryingPerson = false;
        int i = colToLeft(location1.col()) + 5;
        int j = rowToTop(location1.row()) + 25;
        Person person1 = new Person(s, location1, i, j);
        memory[location1.row()][location1.col()] = person1;
        redraw();
        Environment environment1 = new Environment((new StringBuilder()).append("new ").append(s).toString(), person1.getEnvironment(), person1);
        person1.pushFrame(environment1);
        Type type = getClass(s);
        Method method = type.getMethod(s, list.size());
        for(int k = 0; k < arraylist.size(); k++)
            environment1.define(method.getParam(k), arraylist.get(k));

        message = null;
        redraw();
        Statement statement;
        for(Iterator iterator2 = method.body(); iterator2.hasNext(); statement.exec(environment1))
            statement = (Statement)iterator2.next();

        person1.popFrame();
        person1.say((new StringBuilder()).append("I live at ").append(person1.getLocation().toString()).append(".").toString());
        redraw();
        person1.sayNothing();
        message = new TextBox(1, Color.WHITE);
        message.addText(Color.BLUE, 0, 14, person1.getLocation().toString());
        redraw();
        if(person == null)
        {
            movePostmanTo(-1, -1);
        } else
        {
            Location location2 = person.getLocation();
            movePostmanTo(location2.row(), location2.col());
            person.setWaiting(false);
        }
        message = null;
        return person1;
    }

    public static Object invoke(Person person, String s, java.util.List list, Environment environment)
    {
        ArrayList arraylist = new ArrayList();
        Object obj;
        for(Iterator iterator = list.iterator(); iterator.hasNext(); arraylist.add(obj))
        {
            Expression expression = (Expression)iterator.next();
            obj = expression.eval(environment);
        }

        Person person1 = environment.getOwner();
        if(person1 == null)
        {
            movePostmanTo(-1, -1);
        } else
        {
            person1.setWaiting(true);
            Location location = person1.getLocation();
            movePostmanTo(location.row(), location.col());
        }
        message = new TextBox(1, Color.WHITE);
        message.addText(Color.BLACK, 0, 14, s);
        Object obj1;
        for(Iterator iterator1 = arraylist.iterator(); iterator1.hasNext(); message.addText(Color.BLACK, 0, 12, (new StringBuilder()).append(obj1).append("").toString()))
        {
            obj1 = iterator1.next();
            if(obj1 instanceof String)
                obj1 = (new StringBuilder()).append("\"").append(obj1).append("\"").toString();
        }

        redraw();
        Location location1 = person.getLocation();
        movePostmanTo(location1.row(), location1.col());
        Environment environment1 = new Environment(s, person.getEnvironment(), person);
        person.pushFrame(environment1);
        boolean flag = person.isWaiting();
        person.setWaiting(false);
        String s1 = person.getType();
        if(s1.equals(s))
            throw new JavaTownException((new StringBuilder()).append("No method ").append(s).append(" with ").append(list.size()).append(" parameters").toString());
        Method method = getClass(s1).getMethod(s, list.size());
        if(person1 != person && !method.isPublic())
            throw new JavaTownException((new StringBuilder()).append("Method ").append(s).append(" with ").append(list.size()).append(" parameters is private").toString());
        for(int i = 0; i < arraylist.size(); i++)
            environment1.define(method.getParam(i), arraylist.get(i));

        message = null;
        redraw();
        try
        {
            Statement statement;
            for(Iterator iterator2 = method.body(); iterator2.hasNext(); statement.exec(environment1))
                statement = (Statement)iterator2.next();

        }
        catch(ReturnException returnexception)
        {
            Object obj2 = returnexception.getValue();
            person.popFrame();
            String s2 = (new StringBuilder()).append("").append(obj2).toString();
            if(obj2 instanceof String)
                s2 = (new StringBuilder()).append("\"").append(obj2).append("\"").toString();
            person.say((new StringBuilder()).append("It's ").append(s2).append(".").toString());
            person.setWaiting(flag);
            redraw();
            person.sayNothing();
            message = new TextBox(1, Color.WHITE);
            message.addText(Color.BLUE, 0, 14, s2);
            redraw();
            if(person1 == null)
            {
                movePostmanTo(-1, -1);
            } else
            {
                Location location3 = person1.getLocation();
                movePostmanTo(location3.row(), location3.col());
                person1.setWaiting(false);
            }
            redraw();
            message = null;
            return obj2;
        }
        person.popFrame();
        person.setWaiting(flag);
        person.say("OK.");
        redraw();
        person.sayNothing();
        if(person1 == null)
        {
            movePostmanTo(-1, -1);
        } else
        {
            Location location2 = person1.getLocation();
            movePostmanTo(location2.row(), location2.col());
            person1.setWaiting(false);
        }
        redraw();
        return null;
    }

    public static void exec(Statement statement)
    {
        statement.exec(userEnv);
        gc();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if(s.equals("execute") || s.equals("step"))
        {
            isStepping = s.equals("step");
            clicked = true;
            execButton.setEnabled(false);
            stepButton.setEnabled(false);
            if(!isRunning)
                (new Thread() {

                    public void run()
                    {
                        World.isRunning = true;
                        String s1 = World.codeArea.getText().trim();
                        Scanner scanner1 = null;
                        try
                        {
                            scanner1 = new Scanner(new StringReader(s1));
                            Parser parser1 = new Parser(scanner1);
                            Statement statement1;
                            for(Iterator iterator1 = parser1.parseStatements().iterator(); iterator1.hasNext(); statement1.exec(World.userEnv))
                                statement1 = (Statement)iterator1.next();

                        }
                        catch(Throwable throwable1)
                        {
                            throwable1.printStackTrace();
                            if(throwable1 instanceof JavaTownParseException)
                                JOptionPane.showMessageDialog(World.frame, (new StringBuilder()).append(throwable1.getMessage()).append("\n\n").append(scanner1.getRecent()).toString(), "Parsing Error", 0);
                            else
                            if(throwable1 instanceof JavaTownException)
                            {
                                JOptionPane.showMessageDialog(World.frame, throwable1.getMessage(), "Run-time Error", 0);
                            } else
                            {
                                JOptionPane.showMessageDialog(World.frame, (new StringBuilder()).append("Report to JavaTown Developer Team\n\n").append(throwable1).toString(), "Unknown Error", 0);
                                throwable1.printStackTrace();
                            }
                        }
                        World.resetComputation();
                        World.codeArea.setText("");
                        World.addToTranscript(s1);
                        World.isRunning = false;
                        World.execButton.setEnabled(true);
                        World.stepButton.setEnabled(true);
                    }

                    final World this$0;

            
            {
                this$0 = World.this;
                super();
            }
                }).start();
        } else
        if(s.equals("load"))
        {
            isStepping = false;
            execButton.setEnabled(false);
            stepButton.setEnabled(false);
            int i = fileChooser.showOpenDialog(frame);
            if(i != 0)
            {
                codeArea.requestFocusInWindow();
                execButton.setEnabled(true);
                stepButton.setEnabled(true);
                return;
            }
            java.io.File file = fileChooser.getSelectedFile();
            try
            {
                BufferedReader bufferedreader = new BufferedReader(new FileReader(file));
                Scanner scanner = null;
                try
                {
                    scanner = new Scanner(bufferedreader);
                    Parser parser = new Parser(scanner);
                    Statement statement;
                    for(Iterator iterator = parser.parseStatements().iterator(); iterator.hasNext(); statement.exec(userEnv))
                        statement = (Statement)iterator.next();

                }
                catch(Throwable throwable)
                {
                    throwable.printStackTrace();
                    if(throwable instanceof JavaTownParseException)
                        JOptionPane.showMessageDialog(frame, (new StringBuilder()).append(throwable.getMessage()).append("\n\n").append(scanner.getRecent()).toString(), "Parsing Error", 0);
                    else
                    if(throwable instanceof JavaTownException)
                    {
                        JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Run-time Error", 0);
                    } else
                    {
                        JOptionPane.showMessageDialog(frame, (new StringBuilder()).append("Report to JavaTown Developer Team\n\n").append(throwable).toString(), "Unknown Error", 0);
                        throwable.printStackTrace();
                    }
                }
                resetComputation();
                codeArea.setText("");
                bufferedreader.close();
            }
            catch(IOException ioexception)
            {
                ioexception.printStackTrace();
                JOptionPane.showMessageDialog(frame, (new StringBuilder()).append("Unable to read ").append(file).toString(), "Load Error", 0);
            }
        } else
        if(s.equals("clear"))
        {
            int j = JOptionPane.showConfirmDialog(frame, "Are you sure you wish to destroy all objects and global variables?", "Confirm Clear", 0);
            if(j == 0)
                (new Thread() {

                    public void run()
                    {
                        World.userEnv.clear();
                        World.gc();
                    }

                    final World this$0;

            
            {
                this$0 = World.this;
                super();
            }
                }).start();
        } else
        {
            throw new UnsupportedOperationException(s);
        }
        codeArea.requestFocusInWindow();
    }

    private static void resetComputation()
    {
        for(int i = 0; i < memory.length; i++)
        {
            for(int j = 0; j < memory[i].length; j++)
                if(memory[i][j] != null)
                    memory[i][j].resetComputation();

        }

        postmanLeft = 250;
        postmanTop = 400;
        carryingPerson = false;
        message = null;
        world.repaint();
        try
        {
            Thread.sleep(1L);
        }
        catch(InterruptedException interruptedexception) { }
        gc();
    }

    public void removeUpdate(DocumentEvent documentevent)
    {
        codeAreaUpdate();
    }

    public void insertUpdate(DocumentEvent documentevent)
    {
        codeAreaUpdate();
    }

    public void changedUpdate(DocumentEvent documentevent)
    {
        codeAreaUpdate();
    }

    private void codeAreaUpdate()
    {
        execButton.setEnabled(true);
        stepButton.setEnabled(true);
    }

    private static void addToTranscript(String s)
    {
        transcript.append((new StringBuilder()).append("\n").append(s).toString());
        transcript.setCaretPosition(transcript.getDocument().getLength());
        codeArea.requestFocusInWindow();
    }

    private static Map classes = new HashMap();
    private static Environment userEnv;
    private static World world;
    private static Person memory[][] = new Person[4][2];
    private static Random random = new Random();
    private static int postmanLeft = 250;
    private static int postmanTop = 400;
    private static boolean carryingPerson = false;
    private static TextBox message;
    private static JTextArea codeArea;
    private static JTextArea transcript;
    private static boolean clicked;
    private static boolean isStepping = false;
    private static boolean isRunning = false;
    private static JFileChooser fileChooser = new JFileChooser();
    private static JFrame frame;
    private static JButton execButton;
    private static JButton stepButton;









}

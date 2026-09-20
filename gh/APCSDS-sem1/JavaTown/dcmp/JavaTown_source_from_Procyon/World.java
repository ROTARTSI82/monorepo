import java.util.HashMap;
import javax.swing.event.DocumentEvent;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JOptionPane;
import java.io.Reader;
import java.io.StringReader;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.GeneralPath;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JPanel;
import java.awt.LayoutManager;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import java.util.Random;
import java.util.Map;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionListener;
import javax.swing.JComponent;

// 
// Decompiled by Procyon v0.5.36
// 

public class World extends JComponent implements ActionListener, DocumentListener
{
    private static Map<String, Type> classes;
    private static Environment userEnv;
    private static World world;
    private static Person[][] memory;
    private static Random random;
    private static int postmanLeft;
    private static int postmanTop;
    private static boolean carryingPerson;
    private static TextBox message;
    private static JTextArea codeArea;
    private static JTextArea transcript;
    private static boolean clicked;
    private static boolean isStepping;
    private static boolean isRunning;
    private static JFileChooser fileChooser;
    private static JFrame frame;
    private static JButton execButton;
    private static JButton stepButton;
    
    public static void main(final String[] array) throws Exception {
        (World.frame = new JFrame()).setTitle("JavaTown 0.6");
        World.frame.setDefaultCloseOperation(3);
        World.frame.getContentPane().setLayout(new BoxLayout(World.frame.getContentPane(), 2));
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, 3));
        World.frame.getContentPane().add(panel);
        (World.transcript = new JTextArea(15, 15)).setEditable(false);
        World.transcript.setTabSize(2);
        World.transcript.setText("Transcript:\n");
        final Font font = World.transcript.getFont();
        final Font font2 = new Font(font.getName(), font.getStyle(), 14);
        World.transcript.setFont(font2);
        panel.add(new JScrollPane(World.transcript));
        World.world = new World();
        (World.codeArea = new JTextArea(15, 15)).setTabSize(2);
        World.codeArea.setText("[Enter code here]");
        World.codeArea.getDocument().addDocumentListener(World.world);
        World.codeArea.setFont(font2);
        final JScrollPane scrollPane = new JScrollPane(World.codeArea);
        panel.add(World.codeArea);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, 2));
        panel.add(panel2);
        panel2.add(Box.createHorizontalGlue());
        (World.execButton = new JButton("Execute")).setMnemonic(69);
        World.execButton.setActionCommand("execute");
        World.execButton.addActionListener(World.world);
        World.execButton.setEnabled(false);
        panel2.add(World.execButton);
        World.frame.getRootPane().setDefaultButton(World.execButton);
        (World.stepButton = new JButton("Step")).setMnemonic(83);
        World.stepButton.setActionCommand("step");
        World.stepButton.addActionListener(World.world);
        World.stepButton.setEnabled(false);
        panel2.add(World.stepButton);
        final JButton comp = new JButton("Load");
        comp.setMnemonic(76);
        comp.setActionCommand("load");
        comp.addActionListener(World.world);
        panel2.add(comp);
        final JButton comp2 = new JButton("Clear");
        comp2.setMnemonic(67);
        comp2.setActionCommand("clear");
        comp2.addActionListener(World.world);
        panel2.add(comp2);
        World.world.setPreferredSize(new Dimension(540, 500));
        World.userEnv = new Environment("User Variables", null, null);
        World.frame.getContentPane().add(World.world);
        World.frame.pack();
        World.frame.setVisible(true);
        World.codeArea.selectAll();
        World.codeArea.requestFocusInWindow();
    }
    
    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setPaint(Color.CYAN);
        graphics2D.fill(new Rectangle2D.Double(0.0, 0.0, this.getWidth(), 50.0));
        graphics2D.setPaint(Color.GREEN.darker());
        graphics2D.fill(new Rectangle2D.Double(0.0, 50.0, this.getWidth(), this.getHeight() - 50));
        final Font font = graphics2D.getFont();
        graphics2D.setFont(new Font(font.getName(), font.getSize(), 14));
        for (int i = 0; i < World.memory.length; ++i) {
            for (int j = 0; j < World.memory[i].length; ++j) {
                final int colToLeft = colToLeft(j);
                final int rowToTop = rowToTop(i);
                graphics2D.setPaint(Color.LIGHT_GRAY);
                graphics2D.fill(new Rectangle2D.Double(colToLeft, rowToTop + 20, 200.0, 70.0));
                final GeneralPath generalPath = new GeneralPath(0, 3);
                generalPath.moveTo((float)colToLeft, (float)(rowToTop + 20));
                generalPath.lineTo((float)(colToLeft + 100), (float)rowToTop);
                generalPath.lineTo((float)(colToLeft + 200), (float)(rowToTop + 20));
                generalPath.closePath();
                graphics2D.setPaint(Color.DARK_GRAY);
                graphics2D.fill(generalPath);
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw(generalPath);
                graphics2D.setPaint(Color.WHITE);
                graphics2D.drawString(new Location(i, j).toString(), colToLeft + 95, rowToTop + 15);
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw(new Line2D.Double(colToLeft, rowToTop + 20, colToLeft, rowToTop + 90));
                graphics2D.draw(new Line2D.Double(colToLeft + 200, rowToTop + 20, colToLeft + 200, rowToTop + 90));
                if (World.memory[i][j] != null) {
                    World.memory[i][j].draw(graphics2D);
                }
            }
        }
        final TextBox textBox = new TextBox(1, Color.ORANGE.darker().darker(), 0, 450, this.getWidth(), this.getHeight() - 450);
        final Iterator variables = World.userEnv.variables();
        while (variables.hasNext()) {
            final String str = variables.next();
            try {
                Object o = World.userEnv.get(str);
                if (o instanceof String) {
                    o = "\"" + o + "\"";
                }
                textBox.addText(Color.WHITE, 0, 14, str + ":  " + o);
            }
            catch (JavaTownException ex) {}
        }
        textBox.draw(graphics2D);
        graphics2D.setColor(Color.BLUE);
        graphics2D.fill(new RoundRectangle2D.Double(World.postmanLeft, World.postmanTop + 5, 14.0, 5.0, 5.0, 5.0));
        graphics2D.setColor(Color.BLACK);
        graphics2D.draw(new RoundRectangle2D.Double(World.postmanLeft, World.postmanTop + 5, 14.0, 5.0, 5.0, 5.0));
        graphics2D.setPaint(Color.YELLOW);
        graphics2D.fill(new Ellipse2D.Double(World.postmanLeft, World.postmanTop + 10, 14.0, 14.0));
        graphics2D.setPaint(Color.BLACK);
        graphics2D.draw(new Ellipse2D.Double(World.postmanLeft, World.postmanTop + 10, 14.0, 14.0));
        graphics2D.fill(new Rectangle2D.Double(World.postmanLeft + 4, World.postmanTop + 16, 2.0, 2.0));
        graphics2D.fill(new Rectangle2D.Double(World.postmanLeft + 9, World.postmanTop + 16, 2.0, 2.0));
        graphics2D.draw(new Line2D.Double(World.postmanLeft + 5, World.postmanTop + 20, World.postmanLeft + 9, World.postmanTop + 20));
        graphics2D.draw(new Line2D.Double(World.postmanLeft + 7, World.postmanTop + 24, World.postmanLeft + 7, World.postmanTop + 40));
        graphics2D.draw(new Line2D.Double(World.postmanLeft + 7, World.postmanTop + 24, World.postmanLeft, World.postmanTop + 31));
        graphics2D.draw(new Line2D.Double(World.postmanLeft + 7, World.postmanTop + 24, World.postmanLeft + 14, World.postmanTop + 31));
        if (World.carryingPerson) {
            graphics2D.setPaint(Color.YELLOW);
            graphics2D.fill(new Ellipse2D.Double(World.postmanLeft - 3, World.postmanTop + 31, 7.0, 7.0));
            graphics2D.setPaint(Color.BLACK);
            graphics2D.draw(new Ellipse2D.Double(World.postmanLeft - 3, World.postmanTop + 31, 7.0, 7.0));
            graphics2D.draw(new Line2D.Double(World.postmanLeft, World.postmanTop + 38, World.postmanLeft, World.postmanTop + 46));
        }
        if (World.message != null) {
            World.message.setPosition(World.postmanLeft + 14, World.postmanTop + 31);
            World.message.draw(graphics2D);
        }
    }
    
    public static Type getClass(final String str) {
        final Type type = World.classes.get(str);
        if (type == null) {
            throw new JavaTownException("Class not found:  " + str);
        }
        return type;
    }
    
    public static void addClass(final Type type) {
        World.classes.put(type.getName(), type);
        addToTranscript("Defined class " + type.getName());
    }
    
    public static void redraw() {
        World.world.repaint();
        try {
            Thread.sleep(250L);
        }
        catch (InterruptedException ex) {}
        if (World.isStepping) {
            World.clicked = false;
            World.execButton.setEnabled(true);
            World.stepButton.setEnabled(true);
            while (!World.clicked) {
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException ex2) {}
            }
        }
    }
    
    public static void gc() {
        final HashSet<Person> set = new HashSet<Person>();
        findPeople(World.userEnv, set);
        for (int i = 0; i < World.memory.length; ++i) {
            for (int j = 0; j < World.memory[i].length; ++j) {
                if (World.memory[i][j] != null && !set.contains(World.memory[i][j])) {
                    movePostmanTo(i, j);
                    World.memory[i][j] = null;
                    World.carryingPerson = true;
                    movePostmanTo(-1, -1);
                    World.carryingPerson = false;
                }
            }
        }
        movePostmanTo(-1, -1);
    }
    
    private static void findPeople(final Environment environment, final Set<Person> set) {
        final Iterator variables = environment.variables();
        while (variables.hasNext()) {
            final String s = variables.next();
            try {
                final Object value = environment.get(s);
                if (value == null || !(value instanceof Person) || set.contains(value)) {
                    continue;
                }
                final Person person = (Person)value;
                set.add(person);
                findPeople(person.getEnvironment(), set);
                final Iterator stack = person.stack();
                while (stack.hasNext()) {
                    findPeople(stack.next(), set);
                }
            }
            catch (JavaTownException ex) {}
        }
    }
    
    public static Location allocateMemory() {
        final ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < World.memory.length; ++i) {
            for (int j = 0; j < World.memory[i].length; ++j) {
                if (World.memory[i][j] == null) {
                    list.add(new Location(i, j));
                }
            }
        }
        if (list.size() == 0) {
            throw new OutOfMemoryError();
        }
        return (Location)list.get(World.random.nextInt(list.size()));
    }
    
    private static String dump() {
        String string = "";
        for (int i = 0; i < World.memory.length; ++i) {
            for (int j = 0; j < World.memory[i].length; ++j) {
                string = string + World.memory[i][j] + " ";
            }
        }
        return string;
    }
    
    private static int rowToTop(final int n) {
        return 20 + n * 100;
    }
    
    private static int colToLeft(final int n) {
        return 20 + n * 300;
    }
    
    private static void movePostmanTo(final int n, final int n2) {
        int n3;
        int colToLeft;
        if (n == -1 && n2 == -1) {
            n3 = 400;
            colToLeft = 250;
        }
        else {
            n3 = rowToTop(n) + 20;
            colToLeft = colToLeft(n2);
            if (n2 == 0) {
                colToLeft += 205;
            }
            else {
                colToLeft -= 20;
            }
        }
        while (World.postmanTop != n3 || World.postmanLeft != colToLeft) {
            final int n4 = 10;
            if (World.postmanTop > n3) {
                World.postmanTop -= n4;
                if (World.postmanTop < n3) {
                    World.postmanTop = n3;
                }
            }
            else if (World.postmanTop < n3) {
                World.postmanTop += n4;
                if (World.postmanTop > n3) {
                    World.postmanTop = n3;
                }
            }
            if (World.postmanLeft > colToLeft) {
                World.postmanLeft -= n4;
                if (World.postmanLeft < colToLeft) {
                    World.postmanLeft = colToLeft;
                }
            }
            else if (World.postmanLeft < colToLeft) {
                World.postmanLeft += n4;
                if (World.postmanLeft > colToLeft) {
                    World.postmanLeft = colToLeft;
                }
            }
            World.world.repaint();
            try {
                Thread.sleep(20L);
            }
            catch (InterruptedException ex) {}
        }
    }
    
    public static Person instantiate(final String s, final List list, final Environment environment) {
        final ArrayList<Object> list2 = new ArrayList<Object>();
        final Iterator<Expression> iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add(iterator.next().eval(environment));
        }
        final Person owner = environment.getOwner();
        if (owner == null) {
            movePostmanTo(-1, -1);
        }
        else {
            owner.setWaiting(true);
            final Location location = owner.getLocation();
            movePostmanTo(location.row(), location.col());
        }
        (World.message = new TextBox(1, Color.WHITE)).addText(Color.BLACK, 0, 14, "new " + s);
        for (Object o : list2) {
            if (o instanceof String) {
                o = "\"" + o + "\"";
            }
            World.message.addText(Color.BLACK, 0, 12, o + "");
        }
        redraw();
        movePostmanTo(-1, -1);
        World.carryingPerson = true;
        final Location allocateMemory = allocateMemory();
        movePostmanTo(allocateMemory.row(), allocateMemory.col());
        World.carryingPerson = false;
        final Person person = new Person(s, allocateMemory, colToLeft(allocateMemory.col()) + 5, rowToTop(allocateMemory.row()) + 25);
        World.memory[allocateMemory.row()][allocateMemory.col()] = person;
        redraw();
        final Environment environment2 = new Environment("new " + s, person.getEnvironment(), person);
        person.pushFrame(environment2);
        final Method method = getClass(s).getMethod(s, list.size());
        for (int i = 0; i < list2.size(); ++i) {
            environment2.define(method.getParam(i), list2.get(i));
        }
        World.message = null;
        redraw();
        final Iterator body = method.body();
        while (body.hasNext()) {
            body.next().exec(environment2);
        }
        person.popFrame();
        person.say("I live at " + person.getLocation().toString() + ".");
        redraw();
        person.sayNothing();
        (World.message = new TextBox(1, Color.WHITE)).addText(Color.BLUE, 0, 14, person.getLocation().toString());
        redraw();
        if (owner == null) {
            movePostmanTo(-1, -1);
        }
        else {
            final Location location2 = owner.getLocation();
            movePostmanTo(location2.row(), location2.col());
            owner.setWaiting(false);
        }
        World.message = null;
        return person;
    }
    
    public static Object invoke(final Person person, final String str, final List list, final Environment environment) {
        final ArrayList<Object> list2 = new ArrayList<Object>();
        final Iterator<Expression> iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add(iterator.next().eval(environment));
        }
        final Person owner = environment.getOwner();
        if (owner == null) {
            movePostmanTo(-1, -1);
        }
        else {
            owner.setWaiting(true);
            final Location location = owner.getLocation();
            movePostmanTo(location.row(), location.col());
        }
        (World.message = new TextBox(1, Color.WHITE)).addText(Color.BLACK, 0, 14, str);
        for (Object o : list2) {
            if (o instanceof String) {
                o = "\"" + o + "\"";
            }
            World.message.addText(Color.BLACK, 0, 12, o + "");
        }
        redraw();
        final Location location2 = person.getLocation();
        movePostmanTo(location2.row(), location2.col());
        final Environment environment2 = new Environment(str, person.getEnvironment(), person);
        person.pushFrame(environment2);
        final boolean waiting = person.isWaiting();
        person.setWaiting(false);
        final String type = person.getType();
        if (type.equals(str)) {
            throw new JavaTownException("No method " + str + " with " + list.size() + " parameters");
        }
        final Method method = getClass(type).getMethod(str, list.size());
        if (owner != person && !method.isPublic()) {
            throw new JavaTownException("Method " + str + " with " + list.size() + " parameters is private");
        }
        for (int i = 0; i < list2.size(); ++i) {
            environment2.define(method.getParam(i), list2.get(i));
        }
        World.message = null;
        redraw();
        try {
            final Iterator body = method.body();
            while (body.hasNext()) {
                body.next().exec(environment2);
            }
        }
        catch (ReturnException ex) {
            final Object value = ex.getValue();
            person.popFrame();
            String str2 = "" + value;
            if (value instanceof String) {
                str2 = "\"" + value + "\"";
            }
            person.say("It's " + str2 + ".");
            person.setWaiting(waiting);
            redraw();
            person.sayNothing();
            (World.message = new TextBox(1, Color.WHITE)).addText(Color.BLUE, 0, 14, str2);
            redraw();
            if (owner == null) {
                movePostmanTo(-1, -1);
            }
            else {
                final Location location3 = owner.getLocation();
                movePostmanTo(location3.row(), location3.col());
                owner.setWaiting(false);
            }
            redraw();
            World.message = null;
            return value;
        }
        person.popFrame();
        person.setWaiting(waiting);
        person.say("OK.");
        redraw();
        person.sayNothing();
        if (owner == null) {
            movePostmanTo(-1, -1);
        }
        else {
            final Location location4 = owner.getLocation();
            movePostmanTo(location4.row(), location4.col());
            owner.setWaiting(false);
        }
        redraw();
        return null;
    }
    
    public static void exec(final Statement statement) {
        statement.exec(World.userEnv);
        gc();
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        final String actionCommand = actionEvent.getActionCommand();
        if (actionCommand.equals("execute") || actionCommand.equals("step")) {
            World.isStepping = actionCommand.equals("step");
            World.clicked = true;
            World.execButton.setEnabled(false);
            World.stepButton.setEnabled(false);
            if (!World.isRunning) {
                new Thread() {
                    @Override
                    public void run() {
                        World.isRunning = true;
                        final String trim = World.codeArea.getText().trim();
                        Scanner scanner = null;
                        try {
                            scanner = new Scanner(new StringReader(trim));
                            final Iterator<Statement> iterator = new Parser(scanner).parseStatements().iterator();
                            while (iterator.hasNext()) {
                                iterator.next().exec(World.userEnv);
                            }
                        }
                        catch (Throwable obj) {
                            obj.printStackTrace();
                            if (obj instanceof JavaTownParseException) {
                                JOptionPane.showMessageDialog(World.frame, obj.getMessage() + "\n\n" + scanner.getRecent(), "Parsing Error", 0);
                            }
                            else if (obj instanceof JavaTownException) {
                                JOptionPane.showMessageDialog(World.frame, obj.getMessage(), "Run-time Error", 0);
                            }
                            else {
                                JOptionPane.showMessageDialog(World.frame, "Report to JavaTown Developer Team\n\n" + obj, "Unknown Error", 0);
                                obj.printStackTrace();
                            }
                        }
                        resetComputation();
                        World.codeArea.setText("");
                        addToTranscript(trim);
                        World.isRunning = false;
                        World.execButton.setEnabled(true);
                        World.stepButton.setEnabled(true);
                    }
                }.start();
            }
        }
        else if (actionCommand.equals("load")) {
            World.isStepping = false;
            World.execButton.setEnabled(false);
            World.stepButton.setEnabled(false);
            if (World.fileChooser.showOpenDialog(World.frame) != 0) {
                World.codeArea.requestFocusInWindow();
                World.execButton.setEnabled(true);
                World.stepButton.setEnabled(true);
                return;
            }
            final File selectedFile = World.fileChooser.getSelectedFile();
            try {
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(selectedFile));
                Scanner scanner = null;
                try {
                    scanner = new Scanner(bufferedReader);
                    final Iterator<Statement> iterator = new Parser(scanner).parseStatements().iterator();
                    while (iterator.hasNext()) {
                        iterator.next().exec(World.userEnv);
                    }
                }
                catch (Throwable obj) {
                    obj.printStackTrace();
                    if (obj instanceof JavaTownParseException) {
                        JOptionPane.showMessageDialog(World.frame, obj.getMessage() + "\n\n" + scanner.getRecent(), "Parsing Error", 0);
                    }
                    else if (obj instanceof JavaTownException) {
                        JOptionPane.showMessageDialog(World.frame, obj.getMessage(), "Run-time Error", 0);
                    }
                    else {
                        JOptionPane.showMessageDialog(World.frame, "Report to JavaTown Developer Team\n\n" + obj, "Unknown Error", 0);
                        obj.printStackTrace();
                    }
                }
                resetComputation();
                World.codeArea.setText("");
                bufferedReader.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(World.frame, "Unable to read " + selectedFile, "Load Error", 0);
            }
        }
        else {
            if (!actionCommand.equals("clear")) {
                throw new UnsupportedOperationException(actionCommand);
            }
            if (JOptionPane.showConfirmDialog(World.frame, "Are you sure you wish to destroy all objects and global variables?", "Confirm Clear", 0) == 0) {
                new Thread() {
                    @Override
                    public void run() {
                        World.userEnv.clear();
                        World.gc();
                    }
                }.start();
            }
        }
        World.codeArea.requestFocusInWindow();
    }
    
    private static void resetComputation() {
        for (int i = 0; i < World.memory.length; ++i) {
            for (int j = 0; j < World.memory[i].length; ++j) {
                if (World.memory[i][j] != null) {
                    World.memory[i][j].resetComputation();
                }
            }
        }
        World.postmanLeft = 250;
        World.postmanTop = 400;
        World.carryingPerson = false;
        World.message = null;
        World.world.repaint();
        try {
            Thread.sleep(1L);
        }
        catch (InterruptedException ex) {}
        gc();
    }
    
    public void removeUpdate(final DocumentEvent documentEvent) {
        this.codeAreaUpdate();
    }
    
    public void insertUpdate(final DocumentEvent documentEvent) {
        this.codeAreaUpdate();
    }
    
    public void changedUpdate(final DocumentEvent documentEvent) {
        this.codeAreaUpdate();
    }
    
    private void codeAreaUpdate() {
        World.execButton.setEnabled(true);
        World.stepButton.setEnabled(true);
    }
    
    private static void addToTranscript(final String str) {
        World.transcript.append("\n" + str);
        World.transcript.setCaretPosition(World.transcript.getDocument().getLength());
        World.codeArea.requestFocusInWindow();
    }
    
    static {
        World.classes = new HashMap<String, Type>();
        World.memory = new Person[4][2];
        World.random = new Random();
        World.postmanLeft = 250;
        World.postmanTop = 400;
        World.carryingPerson = false;
        World.isStepping = false;
        World.isRunning = false;
        World.fileChooser = new JFileChooser();
    }
}

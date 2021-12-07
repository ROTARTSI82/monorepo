/*
 * Decompiled with CFR 0.151.
 */
import java.awt.Color;
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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class World
extends JComponent
implements ActionListener,
DocumentListener {
    private static Map<String, Type> classes = new HashMap<String, Type>();
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

    public static void main(String[] stringArray) throws Exception {
        frame = new JFrame();
        frame.setTitle("JavaTown 0.6");
        frame.setDefaultCloseOperation(3);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), 2));
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 3));
        frame.getContentPane().add(jPanel);
        transcript = new JTextArea(15, 15);
        transcript.setEditable(false);
        transcript.setTabSize(2);
        transcript.setText("Transcript:\n");
        Font font = transcript.getFont();
        font = new Font(font.getName(), font.getStyle(), 14);
        transcript.setFont(font);
        JScrollPane jScrollPane = new JScrollPane(transcript);
        jPanel.add(jScrollPane);
        world = new World();
        codeArea = new JTextArea(15, 15);
        codeArea.setTabSize(2);
        codeArea.setText("[Enter code here]");
        codeArea.getDocument().addDocumentListener(world);
        codeArea.setFont(font);
        jScrollPane = new JScrollPane(codeArea);
        jPanel.add(codeArea);
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new BoxLayout(jPanel2, 2));
        jPanel.add(jPanel2);
        jPanel2.add(Box.createHorizontalGlue());
        execButton = new JButton("Execute");
        execButton.setMnemonic(69);
        execButton.setActionCommand("execute");
        execButton.addActionListener(world);
        execButton.setEnabled(false);
        jPanel2.add(execButton);
        frame.getRootPane().setDefaultButton(execButton);
        stepButton = new JButton("Step");
        stepButton.setMnemonic(83);
        stepButton.setActionCommand("step");
        stepButton.addActionListener(world);
        stepButton.setEnabled(false);
        jPanel2.add(stepButton);
        JButton jButton = new JButton("Load");
        jButton.setMnemonic(76);
        jButton.setActionCommand("load");
        jButton.addActionListener(world);
        jPanel2.add(jButton);
        JButton jButton2 = new JButton("Clear");
        jButton2.setMnemonic(67);
        jButton2.setActionCommand("clear");
        jButton2.addActionListener(world);
        jPanel2.add(jButton2);
        world.setPreferredSize(new Dimension(540, 500));
        userEnv = new Environment("User Variables", null, null);
        frame.getContentPane().add(world);
        frame.pack();
        frame.setVisible(true);
        codeArea.selectAll();
        codeArea.requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setPaint(Color.CYAN);
        graphics2D.fill(new Rectangle2D.Double(0.0, 0.0, this.getWidth(), 50.0));
        graphics2D.setPaint(Color.GREEN.darker());
        graphics2D.fill(new Rectangle2D.Double(0.0, 50.0, this.getWidth(), this.getHeight() - 50));
        Font font = graphics2D.getFont();
        graphics2D.setFont(new Font(font.getName(), font.getSize(), 14));
        for (int i = 0; i < memory.length; ++i) {
            for (int j = 0; j < memory[i].length; ++j) {
                int n = World.colToLeft(j);
                int n2 = World.rowToTop(i);
                graphics2D.setPaint(Color.LIGHT_GRAY);
                graphics2D.fill(new Rectangle2D.Double(n, n2 + 20, 200.0, 70.0));
                GeneralPath generalPath = new GeneralPath(0, 3);
                generalPath.moveTo(n, n2 + 20);
                generalPath.lineTo(n + 100, n2);
                generalPath.lineTo(n + 200, n2 + 20);
                generalPath.closePath();
                graphics2D.setPaint(Color.DARK_GRAY);
                graphics2D.fill(generalPath);
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw(generalPath);
                graphics2D.setPaint(Color.WHITE);
                graphics2D.drawString(new Location(i, j).toString(), n + 95, n2 + 15);
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw(new Line2D.Double(n, n2 + 20, n, n2 + 90));
                graphics2D.draw(new Line2D.Double(n + 200, n2 + 20, n + 200, n2 + 90));
                if (memory[i][j] == null) continue;
                memory[i][j].draw(graphics2D);
            }
        }
        TextBox textBox = new TextBox(1, Color.ORANGE.darker().darker(), 0, 450, this.getWidth(), this.getHeight() - 450);
        Iterator iterator = userEnv.variables();
        while (iterator.hasNext()) {
            String string = (String)iterator.next();
            try {
                Object object = userEnv.get(string);
                if (object instanceof String) {
                    object = "\"" + object + "\"";
                }
                textBox.addText(Color.WHITE, 0, 14, string + ":  " + object);
            }
            catch (JavaTownException javaTownException) {}
        }
        textBox.draw(graphics2D);
        graphics2D.setColor(Color.BLUE);
        graphics2D.fill(new RoundRectangle2D.Double(postmanLeft, postmanTop + 5, 14.0, 5.0, 5.0, 5.0));
        graphics2D.setColor(Color.BLACK);
        graphics2D.draw(new RoundRectangle2D.Double(postmanLeft, postmanTop + 5, 14.0, 5.0, 5.0, 5.0));
        graphics2D.setPaint(Color.YELLOW);
        graphics2D.fill(new Ellipse2D.Double(postmanLeft, postmanTop + 10, 14.0, 14.0));
        graphics2D.setPaint(Color.BLACK);
        graphics2D.draw(new Ellipse2D.Double(postmanLeft, postmanTop + 10, 14.0, 14.0));
        graphics2D.fill(new Rectangle2D.Double(postmanLeft + 4, postmanTop + 16, 2.0, 2.0));
        graphics2D.fill(new Rectangle2D.Double(postmanLeft + 9, postmanTop + 16, 2.0, 2.0));
        graphics2D.draw(new Line2D.Double(postmanLeft + 5, postmanTop + 20, postmanLeft + 9, postmanTop + 20));
        graphics2D.draw(new Line2D.Double(postmanLeft + 7, postmanTop + 24, postmanLeft + 7, postmanTop + 40));
        graphics2D.draw(new Line2D.Double(postmanLeft + 7, postmanTop + 24, postmanLeft, postmanTop + 31));
        graphics2D.draw(new Line2D.Double(postmanLeft + 7, postmanTop + 24, postmanLeft + 14, postmanTop + 31));
        if (carryingPerson) {
            graphics2D.setPaint(Color.YELLOW);
            graphics2D.fill(new Ellipse2D.Double(postmanLeft - 3, postmanTop + 31, 7.0, 7.0));
            graphics2D.setPaint(Color.BLACK);
            graphics2D.draw(new Ellipse2D.Double(postmanLeft - 3, postmanTop + 31, 7.0, 7.0));
            graphics2D.draw(new Line2D.Double(postmanLeft, postmanTop + 38, postmanLeft, postmanTop + 46));
        }
        if (message != null) {
            message.setPosition(postmanLeft + 14, postmanTop + 31);
            message.draw(graphics2D);
        }
    }

    public static Type getClass(String string) {
        Type type = classes.get(string);
        if (type == null) {
            throw new JavaTownException("Class not found:  " + string);
        }
        return type;
    }

    public static void addClass(Type type) {
        classes.put(type.getName(), type);
        World.addToTranscript("Defined class " + type.getName());
    }

    public static void redraw() {
        world.repaint();
        try {
            Thread.sleep(250L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        if (isStepping) {
            clicked = false;
            execButton.setEnabled(true);
            stepButton.setEnabled(true);
            while (!clicked) {
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }
    }

    public static void gc() {
        HashSet<Person> hashSet = new HashSet<Person>();
        World.findPeople(userEnv, hashSet);
        for (int i = 0; i < memory.length; ++i) {
            for (int j = 0; j < memory[i].length; ++j) {
                if (memory[i][j] == null || hashSet.contains(memory[i][j])) continue;
                World.movePostmanTo(i, j);
                World.memory[i][j] = null;
                carryingPerson = true;
                World.movePostmanTo(-1, -1);
                carryingPerson = false;
            }
        }
        World.movePostmanTo(-1, -1);
    }

    private static void findPeople(Environment environment, Set<Person> set) {
        Iterator iterator = environment.variables();
        while (iterator.hasNext()) {
            String string = (String)iterator.next();
            try {
                Object object = environment.get(string);
                if (object == null || !(object instanceof Person) || set.contains(object)) continue;
                Person person = (Person)object;
                set.add(person);
                World.findPeople(person.getEnvironment(), set);
                Iterator iterator2 = person.stack();
                while (iterator2.hasNext()) {
                    Environment environment2 = (Environment)iterator2.next();
                    World.findPeople(environment2, set);
                }
            }
            catch (JavaTownException javaTownException) {
            }
        }
    }

    public static Location allocateMemory() {
        int n;
        ArrayList<Location> arrayList = new ArrayList<Location>();
        for (n = 0; n < memory.length; ++n) {
            for (int i = 0; i < memory[n].length; ++i) {
                if (memory[n][i] != null) continue;
                arrayList.add(new Location(n, i));
            }
        }
        if (arrayList.size() == 0) {
            throw new OutOfMemoryError();
        }
        n = random.nextInt(arrayList.size());
        Location location = (Location)arrayList.get(n);
        return location;
    }

    private static String dump() {
        String string = "";
        for (int i = 0; i < memory.length; ++i) {
            for (int j = 0; j < memory[i].length; ++j) {
                string = string + memory[i][j] + " ";
            }
        }
        return string;
    }

    private static int rowToTop(int n) {
        return 20 + n * 100;
    }

    private static int colToLeft(int n) {
        return 20 + n * 300;
    }

    private static void movePostmanTo(int n, int n2) {
        int n3;
        int n4;
        if (n == -1 && n2 == -1) {
            n4 = 400;
            n3 = 250;
        } else {
            n4 = World.rowToTop(n) + 20;
            n3 = World.colToLeft(n2);
            n3 = n2 == 0 ? (n3 += 205) : (n3 -= 20);
        }
        while (postmanTop != n4 || postmanLeft != n3) {
            int n5 = 10;
            if (postmanTop > n4) {
                if ((postmanTop -= n5) < n4) {
                    postmanTop = n4;
                }
            } else if (postmanTop < n4 && (postmanTop += n5) > n4) {
                postmanTop = n4;
            }
            if (postmanLeft > n3) {
                if ((postmanLeft -= n5) < n3) {
                    postmanLeft = n3;
                }
            } else if (postmanLeft < n3 && (postmanLeft += n5) > n3) {
                postmanLeft = n3;
            }
            world.repaint();
            try {
                Thread.sleep(20L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    public static Person instantiate(String string, List list, Environment environment) {
        Person person;
        Object object3;
        ArrayList<Object> arrayList = new ArrayList<Object>();
        for (Object object22 : list) {
            object3 = object22.eval(environment);
            arrayList.add(object3);
        }
        Object object22 = environment.getOwner();
        if (object22 == null) {
            World.movePostmanTo(-1, -1);
        } else {
            ((Person)object22).setWaiting(true);
            object3 = ((Person)object22).getLocation();
            World.movePostmanTo(((Location)object3).row(), ((Location)object3).col());
        }
        message = new TextBox(1, Color.WHITE);
        message.addText(Color.BLACK, 0, 14, "new " + string);
        for (Object object3 : arrayList) {
            if (object3 instanceof String) {
                object3 = "\"" + object3 + "\"";
            }
            message.addText(Color.BLACK, 0, 12, object3 + "");
        }
        World.redraw();
        World.movePostmanTo(-1, -1);
        carryingPerson = true;
        object3 = World.allocateMemory();
        World.movePostmanTo(((Location)object3).row(), ((Location)object3).col());
        carryingPerson = false;
        int n = World.colToLeft(((Location)object3).col()) + 5;
        int n2 = World.rowToTop(((Location)object3).row()) + 25;
        World.memory[object3.row()][object3.col()] = person = new Person(string, (Location)object3, n, n2);
        World.redraw();
        Environment environment2 = new Environment("new " + string, person.getEnvironment(), person);
        person.pushFrame(environment2);
        Type type = World.getClass(string);
        Method method = type.getMethod(string, list.size());
        for (int i = 0; i < arrayList.size(); ++i) {
            environment2.define(method.getParam(i), arrayList.get(i));
        }
        message = null;
        World.redraw();
        Iterator iterator = method.body();
        while (iterator.hasNext()) {
            Statement statement = (Statement)iterator.next();
            statement.exec(environment2);
        }
        person.popFrame();
        person.say("I live at " + person.getLocation().toString() + ".");
        World.redraw();
        person.sayNothing();
        message = new TextBox(1, Color.WHITE);
        message.addText(Color.BLUE, 0, 14, person.getLocation().toString());
        World.redraw();
        if (object22 == null) {
            World.movePostmanTo(-1, -1);
        } else {
            Location location = ((Person)object22).getLocation();
            World.movePostmanTo(location.row(), location.col());
            ((Person)object22).setWaiting(false);
        }
        message = null;
        return person;
    }

    public static Object invoke(Person person, String string, List list, Environment environment) {
        Object object3;
        Object object22;
        ArrayList<Object> arrayList = new ArrayList<Object>();
        for (Object object22 : list) {
            object3 = object22.eval(environment);
            arrayList.add(object3);
        }
        object22 = environment.getOwner();
        if (object22 == null) {
            World.movePostmanTo(-1, -1);
        } else {
            ((Person)object22).setWaiting(true);
            object3 = ((Person)object22).getLocation();
            World.movePostmanTo(((Location)object3).row(), ((Location)object3).col());
        }
        message = new TextBox(1, Color.WHITE);
        message.addText(Color.BLACK, 0, 14, string);
        for (Object object3 : arrayList) {
            if (object3 instanceof String) {
                object3 = "\"" + object3 + "\"";
            }
            message.addText(Color.BLACK, 0, 12, object3 + "");
        }
        World.redraw();
        object3 = person.getLocation();
        World.movePostmanTo(((Location)object3).row(), ((Location)object3).col());
        Environment environment2 = new Environment(string, person.getEnvironment(), person);
        person.pushFrame(environment2);
        boolean bl = person.isWaiting();
        person.setWaiting(false);
        String string2 = person.getType();
        if (string2.equals(string)) {
            throw new JavaTownException("No method " + string + " with " + list.size() + " parameters");
        }
        Method method = World.getClass(string2).getMethod(string, list.size());
        if (object22 != person && !method.isPublic()) {
            throw new JavaTownException("Method " + string + " with " + list.size() + " parameters is private");
        }
        for (int i = 0; i < arrayList.size(); ++i) {
            environment2.define(method.getParam(i), arrayList.get(i));
        }
        message = null;
        World.redraw();
        try {
            Iterator iterator = method.body();
            while (iterator.hasNext()) {
                Statement statement = (Statement)iterator.next();
                statement.exec(environment2);
            }
        }
        catch (ReturnException returnException) {
            Object object4 = returnException.getValue();
            person.popFrame();
            String string3 = "" + object4;
            if (object4 instanceof String) {
                string3 = "\"" + object4 + "\"";
            }
            person.say("It's " + string3 + ".");
            person.setWaiting(bl);
            World.redraw();
            person.sayNothing();
            message = new TextBox(1, Color.WHITE);
            message.addText(Color.BLUE, 0, 14, string3);
            World.redraw();
            if (object22 == null) {
                World.movePostmanTo(-1, -1);
            } else {
                Location location = ((Person)object22).getLocation();
                World.movePostmanTo(location.row(), location.col());
                ((Person)object22).setWaiting(false);
            }
            World.redraw();
            message = null;
            return object4;
        }
        person.popFrame();
        person.setWaiting(bl);
        person.say("OK.");
        World.redraw();
        person.sayNothing();
        if (object22 == null) {
            World.movePostmanTo(-1, -1);
        } else {
            Location location = ((Person)object22).getLocation();
            World.movePostmanTo(location.row(), location.col());
            ((Person)object22).setWaiting(false);
        }
        World.redraw();
        return null;
    }

    public static void exec(Statement statement) {
        statement.exec(userEnv);
        World.gc();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String string = actionEvent.getActionCommand();
        if (string.equals("execute") || string.equals("step")) {
            isStepping = string.equals("step");
            clicked = true;
            execButton.setEnabled(false);
            stepButton.setEnabled(false);
            if (!isRunning) {
                new Thread(){

                    public void run() {
                        isRunning = true;
                        String string = codeArea.getText().trim();
                        Scanner scanner = null;
                        try {
                            scanner = new Scanner(new StringReader(string));
                            Parser parser2 = new Parser(scanner);
                            for (Statement statement : parser2.parseStatements()) {
                                statement.exec(userEnv);
                            }
                        }
                        catch (Throwable throwable) {
                            throwable.printStackTrace();
                            if (throwable instanceof JavaTownParseException) {
                                JOptionPane.showMessageDialog(frame, throwable.getMessage() + "\n\n" + scanner.getRecent(), "Parsing Error", 0);
                            }
                            if (throwable instanceof JavaTownException) {
                                JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Run-time Error", 0);
                            }
                            JOptionPane.showMessageDialog(frame, "Report to JavaTown Developer Team\n\n" + throwable, "Unknown Error", 0);
                            throwable.printStackTrace();
                        }
                        World.resetComputation();
                        codeArea.setText("");
                        World.addToTranscript(string);
                        isRunning = false;
                        execButton.setEnabled(true);
                        stepButton.setEnabled(true);
                    }
                }.start();
            }
        } else if (string.equals("load")) {
            isStepping = false;
            execButton.setEnabled(false);
            stepButton.setEnabled(false);
            int n = fileChooser.showOpenDialog(frame);
            if (n != 0) {
                codeArea.requestFocusInWindow();
                execButton.setEnabled(true);
                stepButton.setEnabled(true);
                return;
            }
            File file = fileChooser.getSelectedFile();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                Scanner scanner = null;
                try {
                    scanner = new Scanner(bufferedReader);
                    Parser parser2 = new Parser(scanner);
                    for (Statement statement : parser2.parseStatements()) {
                        statement.exec(userEnv);
                    }
                }
                catch (Throwable throwable) {
                    throwable.printStackTrace();
                    if (throwable instanceof JavaTownParseException) {
                        JOptionPane.showMessageDialog(frame, throwable.getMessage() + "\n\n" + scanner.getRecent(), "Parsing Error", 0);
                    }
                    if (throwable instanceof JavaTownException) {
                        JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Run-time Error", 0);
                    }
                    JOptionPane.showMessageDialog(frame, "Report to JavaTown Developer Team\n\n" + throwable, "Unknown Error", 0);
                    throwable.printStackTrace();
                }
                World.resetComputation();
                codeArea.setText("");
                bufferedReader.close();
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Unable to read " + file, "Load Error", 0);
            }
        } else if (string.equals("clear")) {
            int n = JOptionPane.showConfirmDialog(frame, "Are you sure you wish to destroy all objects and global variables?", "Confirm Clear", 0);
            if (n == 0) {
                new Thread(){

                    public void run() {
                        userEnv.clear();
                        World.gc();
                    }
                }.start();
            }
        } else {
            throw new UnsupportedOperationException(string);
        }
        codeArea.requestFocusInWindow();
    }

    private static void resetComputation() {
        for (int i = 0; i < memory.length; ++i) {
            for (int j = 0; j < memory[i].length; ++j) {
                if (memory[i][j] == null) continue;
                memory[i][j].resetComputation();
            }
        }
        postmanLeft = 250;
        postmanTop = 400;
        carryingPerson = false;
        message = null;
        world.repaint();
        try {
            Thread.sleep(1L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        World.gc();
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        this.codeAreaUpdate();
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        this.codeAreaUpdate();
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        this.codeAreaUpdate();
    }

    private void codeAreaUpdate() {
        execButton.setEnabled(true);
        stepButton.setEnabled(true);
    }

    private static void addToTranscript(String string) {
        transcript.append("\n" + string);
        transcript.setCaretPosition(transcript.getDocument().getLength());
        codeArea.requestFocusInWindow();
    }

    static {
        memory = new Person[4][2];
        random = new Random();
        postmanLeft = 250;
        postmanTop = 400;
        carryingPerson = false;
        isStepping = false;
        isRunning = false;
        fileChooser = new JFileChooser();
    }
}


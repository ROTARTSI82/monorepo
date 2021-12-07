package p000;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Array;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/* renamed from: World */
public class World extends JComponent implements ActionListener, DocumentListener {
    private static boolean carryingPerson = false;
    private static Map<String, Type> classes = new HashMap();
    private static boolean clicked;
    /* access modifiers changed from: private */
    public static JTextArea codeArea;
    /* access modifiers changed from: private */
    public static JButton execButton;
    private static JFileChooser fileChooser = new JFileChooser();
    /* access modifiers changed from: private */
    public static JFrame frame;
    /* access modifiers changed from: private */
    public static boolean isRunning = false;
    private static boolean isStepping = false;
    private static Person[][] memory = ((Person[][]) Array.newInstance(Person.class, new int[]{4, 2}));
    private static TextBox message;
    private static int postmanLeft = 250;
    private static int postmanTop = 400;
    private static Random random = new Random();
    /* access modifiers changed from: private */
    public static JButton stepButton;
    private static JTextArea transcript;
    /* access modifiers changed from: private */
    public static Environment userEnv;
    private static World world;

    public static void main(String[] strArr) throws Exception {
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
        Font font2 = new Font(font.getName(), font.getStyle(), 14);
        transcript.setFont(font2);
        jPanel.add(new JScrollPane(transcript));
        world = new World();
        codeArea = new JTextArea(15, 15);
        codeArea.setTabSize(2);
        codeArea.setText("[Enter code here]");
        codeArea.getDocument().addDocumentListener(world);
        codeArea.setFont(font2);
        new JScrollPane(codeArea);
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
        userEnv = new Environment("User Variables", (Environment) null, (Person) null);
        frame.getContentPane().add(world);
        frame.pack();
        frame.setVisible(true);
        codeArea.selectAll();
        codeArea.requestFocusInWindow();
    }

    /* access modifiers changed from: protected */
    public void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setPaint(Color.CYAN);
        graphics2D.fill(new Rectangle2D.Double(0.0d, 0.0d, (double) getWidth(), 50.0d));
        graphics2D.setPaint(Color.GREEN.darker());
        graphics2D.fill(new Rectangle2D.Double(0.0d, 50.0d, (double) getWidth(), (double) (getHeight() - 50)));
        Font font = graphics2D.getFont();
        graphics2D.setFont(new Font(font.getName(), font.getSize(), 14));
        for (int i = 0; i < memory.length; i++) {
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= memory[i].length) {
                    break;
                }
                int colToLeft = colToLeft(i3);
                int rowToTop = rowToTop(i);
                graphics2D.setPaint(Color.LIGHT_GRAY);
                graphics2D.fill(new Rectangle2D.Double((double) colToLeft, (double) (rowToTop + 20), 200.0d, 70.0d));
                GeneralPath generalPath = new GeneralPath(0, 3);
                generalPath.moveTo((float) colToLeft, (float) (rowToTop + 20));
                generalPath.lineTo((float) (colToLeft + 100), (float) rowToTop);
                generalPath.lineTo((float) (colToLeft + 200), (float) (rowToTop + 20));
                generalPath.closePath();
                graphics2D.setPaint(Color.DARK_GRAY);
                graphics2D.fill(generalPath);
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw(generalPath);
                graphics2D.setPaint(Color.WHITE);
                graphics2D.drawString(new Location(i, i3).toString(), colToLeft + 95, rowToTop + 15);
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw(new Line2D.Double((double) colToLeft, (double) (rowToTop + 20), (double) colToLeft, (double) (rowToTop + 90)));
                graphics2D.draw(new Line2D.Double((double) (colToLeft + 200), (double) (rowToTop + 20), (double) (colToLeft + 200), (double) (rowToTop + 90)));
                if (memory[i][i3] != null) {
                    memory[i][i3].draw(graphics2D);
                }
                i2 = i3 + 1;
            }
        }
        TextBox textBox = new TextBox(1, Color.ORANGE.darker().darker(), 0, 450, getWidth(), getHeight() - 450);
        Iterator variables = userEnv.variables();
        while (variables.hasNext()) {
            String str = (String) variables.next();
            try {
                Object obj = userEnv.get(str);
                if (obj instanceof String) {
                    obj = "\"" + obj + "\"";
                }
                textBox.addText(Color.WHITE, 0, 14, str + ":  " + obj);
            } catch (JavaTownException e) {
            }
        }
        textBox.draw(graphics2D);
        graphics2D.setColor(Color.BLUE);
        graphics2D.fill(new RoundRectangle2D.Double((double) postmanLeft, (double) (postmanTop + 5), 14.0d, 5.0d, 5.0d, 5.0d));
        graphics2D.setColor(Color.BLACK);
        graphics2D.draw(new RoundRectangle2D.Double((double) postmanLeft, (double) (postmanTop + 5), 14.0d, 5.0d, 5.0d, 5.0d));
        graphics2D.setPaint(Color.YELLOW);
        graphics2D.fill(new Ellipse2D.Double((double) postmanLeft, (double) (postmanTop + 10), 14.0d, 14.0d));
        graphics2D.setPaint(Color.BLACK);
        graphics2D.draw(new Ellipse2D.Double((double) postmanLeft, (double) (postmanTop + 10), 14.0d, 14.0d));
        graphics2D.fill(new Rectangle2D.Double((double) (postmanLeft + 4), (double) (postmanTop + 16), 2.0d, 2.0d));
        graphics2D.fill(new Rectangle2D.Double((double) (postmanLeft + 9), (double) (postmanTop + 16), 2.0d, 2.0d));
        graphics2D.draw(new Line2D.Double((double) (postmanLeft + 5), (double) (postmanTop + 20), (double) (postmanLeft + 9), (double) (postmanTop + 20)));
        graphics2D.draw(new Line2D.Double((double) (postmanLeft + 7), (double) (postmanTop + 24), (double) (postmanLeft + 7), (double) (postmanTop + 40)));
        graphics2D.draw(new Line2D.Double((double) (postmanLeft + 7), (double) (postmanTop + 24), (double) postmanLeft, (double) (postmanTop + 31)));
        graphics2D.draw(new Line2D.Double((double) (postmanLeft + 7), (double) (postmanTop + 24), (double) (postmanLeft + 14), (double) (postmanTop + 31)));
        if (carryingPerson) {
            graphics2D.setPaint(Color.YELLOW);
            graphics2D.fill(new Ellipse2D.Double((double) (postmanLeft - 3), (double) (postmanTop + 31), 7.0d, 7.0d));
            graphics2D.setPaint(Color.BLACK);
            graphics2D.draw(new Ellipse2D.Double((double) (postmanLeft - 3), (double) (postmanTop + 31), 7.0d, 7.0d));
            graphics2D.draw(new Line2D.Double((double) postmanLeft, (double) (postmanTop + 38), (double) postmanLeft, (double) (postmanTop + 46)));
        }
        if (message != null) {
            message.setPosition(postmanLeft + 14, postmanTop + 31);
            message.draw(graphics2D);
        }
    }

    public static Type getClass(String str) {
        Type type = classes.get(str);
        if (type != null) {
            return type;
        }
        throw new JavaTownException("Class not found:  " + str);
    }

    public static void addClass(Type type) {
        classes.put(type.getName(), type);
        addToTranscript("Defined class " + type.getName());
    }

    public static void redraw() {
        world.repaint();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
        }
        if (isStepping) {
            clicked = false;
            execButton.setEnabled(true);
            stepButton.setEnabled(true);
            while (!clicked) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e2) {
                }
            }
        }
    }

    /* renamed from: gc */
    public static void m0gc() {
        HashSet hashSet = new HashSet();
        findPeople(userEnv, hashSet);
        for (int i = 0; i < memory.length; i++) {
            for (int i2 = 0; i2 < memory[i].length; i2++) {
                if (memory[i][i2] != null && !hashSet.contains(memory[i][i2])) {
                    movePostmanTo(i, i2);
                    memory[i][i2] = null;
                    carryingPerson = true;
                    movePostmanTo(-1, -1);
                    carryingPerson = false;
                }
            }
        }
        movePostmanTo(-1, -1);
    }

    private static void findPeople(Environment environment, Set<Person> set) {
        Iterator variables = environment.variables();
        while (variables.hasNext()) {
            try {
                Object obj = environment.get((String) variables.next());
                if (obj != null && (obj instanceof Person) && !set.contains(obj)) {
                    Person person = (Person) obj;
                    set.add(person);
                    findPeople(person.getEnvironment(), set);
                    Iterator stack = person.stack();
                    while (stack.hasNext()) {
                        findPeople((Environment) stack.next(), set);
                    }
                }
            } catch (JavaTownException e) {
            }
        }
    }

    public static Location allocateMemory() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < memory.length; i++) {
            for (int i2 = 0; i2 < memory[i].length; i2++) {
                if (memory[i][i2] == null) {
                    arrayList.add(new Location(i, i2));
                }
            }
        }
        if (arrayList.size() != 0) {
            return (Location) arrayList.get(random.nextInt(arrayList.size()));
        }
        throw new OutOfMemoryError();
    }

    private static String dump() {
        String str = "";
        for (int i = 0; i < memory.length; i++) {
            for (int i2 = 0; i2 < memory[i].length; i2++) {
                str = str + memory[i][i2] + " ";
            }
        }
        return str;
    }

    private static int rowToTop(int i) {
        return (i * 100) + 20;
    }

    private static int colToLeft(int i) {
        return (i * 300) + 20;
    }

    private static void movePostmanTo(int i, int i2) {
        int i3;
        int i4;
        if (i == -1 && i2 == -1) {
            i3 = 400;
            i4 = 250;
        } else {
            i3 = rowToTop(i) + 20;
            int colToLeft = colToLeft(i2);
            if (i2 == 0) {
                i4 = colToLeft + 205;
            } else {
                i4 = colToLeft - 20;
            }
        }
        while (true) {
            if (postmanTop != i3 || postmanLeft != i4) {
                if (postmanTop > i3) {
                    postmanTop -= 10;
                    if (postmanTop < i3) {
                        postmanTop = i3;
                    }
                } else if (postmanTop < i3) {
                    postmanTop += 10;
                    if (postmanTop > i3) {
                        postmanTop = i3;
                    }
                }
                if (postmanLeft > i4) {
                    postmanLeft -= 10;
                    if (postmanLeft < i4) {
                        postmanLeft = i4;
                    }
                } else if (postmanLeft < i4) {
                    postmanLeft = 10 + postmanLeft;
                    if (postmanLeft > i4) {
                        postmanLeft = i4;
                    }
                }
                world.repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            } else {
                return;
            }
        }
    }

    public static Person instantiate(String str, List list, Environment environment) {
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(((Expression) it.next()).eval(environment));
        }
        Person owner = environment.getOwner();
        if (owner == null) {
            movePostmanTo(-1, -1);
        } else {
            owner.setWaiting(true);
            Location location = owner.getLocation();
            movePostmanTo(location.row(), location.col());
        }
        message = new TextBox(1, Color.WHITE);
        message.addText(Color.BLACK, 0, 14, "new " + str);
        for (Object next : arrayList) {
            if (next instanceof String) {
                next = "\"" + next + "\"";
            }
            message.addText(Color.BLACK, 0, 12, next + "");
        }
        redraw();
        movePostmanTo(-1, -1);
        carryingPerson = true;
        Location allocateMemory = allocateMemory();
        movePostmanTo(allocateMemory.row(), allocateMemory.col());
        carryingPerson = false;
        Person person = new Person(str, allocateMemory, colToLeft(allocateMemory.col()) + 5, rowToTop(allocateMemory.row()) + 25);
        memory[allocateMemory.row()][allocateMemory.col()] = person;
        redraw();
        Environment environment2 = new Environment("new " + str, person.getEnvironment(), person);
        person.pushFrame(environment2);
        Method method = getClass(str).getMethod(str, list.size());
        for (int i = 0; i < arrayList.size(); i++) {
            environment2.define(method.getParam(i), arrayList.get(i));
        }
        message = null;
        redraw();
        Iterator body = method.body();
        while (body.hasNext()) {
            ((Statement) body.next()).exec(environment2);
        }
        person.popFrame();
        person.say("I live at " + person.getLocation().toString() + ".");
        redraw();
        person.sayNothing();
        message = new TextBox(1, Color.WHITE);
        message.addText(Color.BLUE, 0, 14, person.getLocation().toString());
        redraw();
        if (owner == null) {
            movePostmanTo(-1, -1);
        } else {
            Location location2 = owner.getLocation();
            movePostmanTo(location2.row(), location2.col());
            owner.setWaiting(false);
        }
        message = null;
        return person;
    }

    public static Object invoke(Person person, String str, List list, Environment environment) {
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(((Expression) it.next()).eval(environment));
        }
        Person owner = environment.getOwner();
        if (owner == null) {
            movePostmanTo(-1, -1);
        } else {
            owner.setWaiting(true);
            Location location = owner.getLocation();
            movePostmanTo(location.row(), location.col());
        }
        message = new TextBox(1, Color.WHITE);
        message.addText(Color.BLACK, 0, 14, str);
        for (Object next : arrayList) {
            if (next instanceof String) {
                next = "\"" + next + "\"";
            }
            message.addText(Color.BLACK, 0, 12, next + "");
        }
        redraw();
        Location location2 = person.getLocation();
        movePostmanTo(location2.row(), location2.col());
        Environment environment2 = new Environment(str, person.getEnvironment(), person);
        person.pushFrame(environment2);
        boolean isWaiting = person.isWaiting();
        person.setWaiting(false);
        String type = person.getType();
        if (type.equals(str)) {
            throw new JavaTownException("No method " + str + " with " + list.size() + " parameters");
        }
        Method method = getClass(type).getMethod(str, list.size());
        if (owner == person || method.isPublic()) {
            for (int i = 0; i < arrayList.size(); i++) {
                environment2.define(method.getParam(i), arrayList.get(i));
            }
            message = null;
            redraw();
            try {
                Iterator body = method.body();
                while (body.hasNext()) {
                    ((Statement) body.next()).exec(environment2);
                }
                person.popFrame();
                person.setWaiting(isWaiting);
                person.say("OK.");
                redraw();
                person.sayNothing();
                if (owner == null) {
                    movePostmanTo(-1, -1);
                } else {
                    Location location3 = owner.getLocation();
                    movePostmanTo(location3.row(), location3.col());
                    owner.setWaiting(false);
                }
                redraw();
                return null;
            } catch (ReturnException e) {
                Object value = e.getValue();
                person.popFrame();
                String str2 = "" + value;
                if (value instanceof String) {
                    str2 = "\"" + value + "\"";
                }
                person.say("It's " + str2 + ".");
                person.setWaiting(isWaiting);
                redraw();
                person.sayNothing();
                message = new TextBox(1, Color.WHITE);
                message.addText(Color.BLUE, 0, 14, str2);
                redraw();
                if (owner == null) {
                    movePostmanTo(-1, -1);
                } else {
                    Location location4 = owner.getLocation();
                    movePostmanTo(location4.row(), location4.col());
                    owner.setWaiting(false);
                }
                redraw();
                message = null;
                return value;
            }
        } else {
            throw new JavaTownException("Method " + str + " with " + list.size() + " parameters is private");
        }
    }

    public static void exec(Statement statement) {
        statement.exec(userEnv);
        m0gc();
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a7 A[Catch:{ IOException -> 0x00dd }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00fd A[SYNTHETIC, Splitter:B:32:0x00fd] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void actionPerformed(java.awt.event.ActionEvent r9) {
        /*
            r8 = this;
            r2 = 1
            r7 = 0
            java.lang.String r0 = r9.getActionCommand()
            java.lang.String r1 = "execute"
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L_0x0016
            java.lang.String r1 = "step"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x003c
        L_0x0016:
            java.lang.String r1 = "step"
            boolean r0 = r0.equals(r1)
            isStepping = r0
            clicked = r2
            javax.swing.JButton r0 = execButton
            r0.setEnabled(r7)
            javax.swing.JButton r0 = stepButton
            r0.setEnabled(r7)
            boolean r0 = isRunning
            if (r0 != 0) goto L_0x0036
            World$1 r0 = new World$1
            r0.<init>()
            r0.start()
        L_0x0036:
            javax.swing.JTextArea r0 = codeArea
            r0.requestFocusInWindow()
        L_0x003b:
            return
        L_0x003c:
            java.lang.String r1 = "load"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x012d
            isStepping = r7
            javax.swing.JButton r0 = execButton
            r0.setEnabled(r7)
            javax.swing.JButton r0 = stepButton
            r0.setEnabled(r7)
            javax.swing.JFileChooser r0 = fileChooser
            javax.swing.JFrame r1 = frame
            int r0 = r0.showOpenDialog(r1)
            if (r0 == 0) goto L_0x006a
            javax.swing.JTextArea r0 = codeArea
            r0.requestFocusInWindow()
            javax.swing.JButton r0 = execButton
            r0.setEnabled(r2)
            javax.swing.JButton r0 = stepButton
            r0.setEnabled(r2)
            goto L_0x003b
        L_0x006a:
            javax.swing.JFileChooser r0 = fileChooser
            java.io.File r3 = r0.getSelectedFile()
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ IOException -> 0x00dd }
            java.io.FileReader r0 = new java.io.FileReader     // Catch:{ IOException -> 0x00dd }
            r0.<init>(r3)     // Catch:{ IOException -> 0x00dd }
            r4.<init>(r0)     // Catch:{ IOException -> 0x00dd }
            r2 = 0
            Scanner r1 = new Scanner     // Catch:{ Throwable -> 0x0151 }
            r1.<init>(r4)     // Catch:{ Throwable -> 0x0151 }
            Parser r0 = new Parser     // Catch:{ Throwable -> 0x009f }
            r0.<init>(r1)     // Catch:{ Throwable -> 0x009f }
            java.util.ArrayList r0 = r0.parseStatements()     // Catch:{ Throwable -> 0x009f }
            java.util.Iterator r2 = r0.iterator()     // Catch:{ Throwable -> 0x009f }
        L_0x008d:
            boolean r0 = r2.hasNext()     // Catch:{ Throwable -> 0x009f }
            if (r0 == 0) goto L_0x00ce
            java.lang.Object r0 = r2.next()     // Catch:{ Throwable -> 0x009f }
            Statement r0 = (p000.Statement) r0     // Catch:{ Throwable -> 0x009f }
            Environment r5 = userEnv     // Catch:{ Throwable -> 0x009f }
            r0.exec(r5)     // Catch:{ Throwable -> 0x009f }
            goto L_0x008d
        L_0x009f:
            r0 = move-exception
        L_0x00a0:
            r0.printStackTrace()     // Catch:{ IOException -> 0x00dd }
            boolean r2 = r0 instanceof p000.JavaTownParseException     // Catch:{ IOException -> 0x00dd }
            if (r2 == 0) goto L_0x00fd
            javax.swing.JFrame r2 = frame     // Catch:{ IOException -> 0x00dd }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00dd }
            r5.<init>()     // Catch:{ IOException -> 0x00dd }
            java.lang.String r0 = r0.getMessage()     // Catch:{ IOException -> 0x00dd }
            java.lang.StringBuilder r0 = r5.append(r0)     // Catch:{ IOException -> 0x00dd }
            java.lang.String r5 = "\n\n"
            java.lang.StringBuilder r0 = r0.append(r5)     // Catch:{ IOException -> 0x00dd }
            java.lang.String r1 = r1.getRecent()     // Catch:{ IOException -> 0x00dd }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ IOException -> 0x00dd }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x00dd }
            java.lang.String r1 = "Parsing Error"
            r5 = 0
            javax.swing.JOptionPane.showMessageDialog(r2, r0, r1, r5)     // Catch:{ IOException -> 0x00dd }
        L_0x00ce:
            resetComputation()     // Catch:{ IOException -> 0x00dd }
            javax.swing.JTextArea r0 = codeArea     // Catch:{ IOException -> 0x00dd }
            java.lang.String r1 = ""
            r0.setText(r1)     // Catch:{ IOException -> 0x00dd }
            r4.close()     // Catch:{ IOException -> 0x00dd }
            goto L_0x0036
        L_0x00dd:
            r0 = move-exception
            r0.printStackTrace()
            javax.swing.JFrame r0 = frame
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unable to read "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "Load Error"
            javax.swing.JOptionPane.showMessageDialog(r0, r1, r2, r7)
            goto L_0x0036
        L_0x00fd:
            boolean r1 = r0 instanceof p000.JavaTownException     // Catch:{ IOException -> 0x00dd }
            if (r1 == 0) goto L_0x010e
            javax.swing.JFrame r1 = frame     // Catch:{ IOException -> 0x00dd }
            java.lang.String r0 = r0.getMessage()     // Catch:{ IOException -> 0x00dd }
            java.lang.String r2 = "Run-time Error"
            r5 = 0
            javax.swing.JOptionPane.showMessageDialog(r1, r0, r2, r5)     // Catch:{ IOException -> 0x00dd }
            goto L_0x00ce
        L_0x010e:
            javax.swing.JFrame r1 = frame     // Catch:{ IOException -> 0x00dd }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00dd }
            r2.<init>()     // Catch:{ IOException -> 0x00dd }
            java.lang.String r5 = "Report to JavaTown Developer Team\n\n"
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ IOException -> 0x00dd }
            java.lang.StringBuilder r2 = r2.append(r0)     // Catch:{ IOException -> 0x00dd }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x00dd }
            java.lang.String r5 = "Unknown Error"
            r6 = 0
            javax.swing.JOptionPane.showMessageDialog(r1, r2, r5, r6)     // Catch:{ IOException -> 0x00dd }
            r0.printStackTrace()     // Catch:{ IOException -> 0x00dd }
            goto L_0x00ce
        L_0x012d:
            java.lang.String r1 = "clear"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x014b
            javax.swing.JFrame r0 = frame
            java.lang.String r1 = "Are you sure you wish to destroy all objects and global variables?"
            java.lang.String r2 = "Confirm Clear"
            int r0 = javax.swing.JOptionPane.showConfirmDialog(r0, r1, r2, r7)
            if (r0 != 0) goto L_0x0036
            World$2 r0 = new World$2
            r0.<init>()
            r0.start()
            goto L_0x0036
        L_0x014b:
            java.lang.UnsupportedOperationException r1 = new java.lang.UnsupportedOperationException
            r1.<init>(r0)
            throw r1
        L_0x0151:
            r0 = move-exception
            r1 = r2
            goto L_0x00a0
        */
        throw new UnsupportedOperationException("Method not decompiled: p000.World.actionPerformed(java.awt.event.ActionEvent):void");
    }

    /* access modifiers changed from: private */
    public static void resetComputation() {
        for (int i = 0; i < memory.length; i++) {
            for (int i2 = 0; i2 < memory[i].length; i2++) {
                if (memory[i][i2] != null) {
                    memory[i][i2].resetComputation();
                }
            }
        }
        postmanLeft = 250;
        postmanTop = 400;
        carryingPerson = false;
        message = null;
        world.repaint();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
        m0gc();
    }

    public void removeUpdate(DocumentEvent documentEvent) {
        codeAreaUpdate();
    }

    public void insertUpdate(DocumentEvent documentEvent) {
        codeAreaUpdate();
    }

    public void changedUpdate(DocumentEvent documentEvent) {
        codeAreaUpdate();
    }

    private void codeAreaUpdate() {
        execButton.setEnabled(true);
        stepButton.setEnabled(true);
    }

    /* access modifiers changed from: private */
    public static void addToTranscript(String str) {
        transcript.append("\n" + str);
        transcript.setCaretPosition(transcript.getDocument().getLength());
        codeArea.requestFocusInWindow();
    }
}

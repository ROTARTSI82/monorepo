import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class World extends javax.swing.JComponent implements java.awt.event.ActionListener, javax.swing.event.DocumentListener
{
  private static java.util.Map<String, Type> classes = new java.util.HashMap();
  private static Environment userEnv;
  private static World world;
  private static Person[][] memory = new Person[4][2];
  private static java.util.Random random = new java.util.Random();
  private static int postmanLeft = 250;
  private static int postmanTop = 400;
  private static boolean carryingPerson = false;
  private static TextBox message;
  private static JTextArea codeArea;
  private static JTextArea transcript;
  private static boolean clicked;
  private static boolean isStepping = false;
  private static boolean isRunning = false;
  private static javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
  private static javax.swing.JFrame frame;
  private static JButton execButton;
  private static JButton stepButton;
  
  public World() {}
  
  public static void main(String[] paramArrayOfString) throws Exception { frame = new javax.swing.JFrame();
    frame.setTitle("JavaTown 0.6");
    frame.setDefaultCloseOperation(3);
    frame.getContentPane().setLayout(new javax.swing.BoxLayout(frame.getContentPane(), 2));
    
    JPanel localJPanel1 = new JPanel();
    localJPanel1.setLayout(new javax.swing.BoxLayout(localJPanel1, 3));
    frame.getContentPane().add(localJPanel1);
    
    transcript = new JTextArea(15, 15);
    transcript.setEditable(false);
    transcript.setTabSize(2);
    transcript.setText("Transcript:\n");
    java.awt.Font localFont = transcript.getFont();
    localFont = new java.awt.Font(localFont.getName(), localFont.getStyle(), 14);
    transcript.setFont(localFont);
    javax.swing.JScrollPane localJScrollPane = new javax.swing.JScrollPane(transcript);
    localJPanel1.add(localJScrollPane);
    
    world = new World();
    
    codeArea = new JTextArea(15, 15);
    codeArea.setTabSize(2);
    codeArea.setText("[Enter code here]");
    codeArea.getDocument().addDocumentListener(world);
    codeArea.setFont(localFont);
    localJScrollPane = new javax.swing.JScrollPane(codeArea);
    localJPanel1.add(codeArea);
    
    JPanel localJPanel2 = new JPanel();
    localJPanel2.setLayout(new javax.swing.BoxLayout(localJPanel2, 2));
    localJPanel1.add(localJPanel2);
    localJPanel2.add(javax.swing.Box.createHorizontalGlue());
    
    execButton = new JButton("Execute");
    execButton.setMnemonic(69);
    execButton.setActionCommand("execute");
    execButton.addActionListener(world);
    execButton.setEnabled(false);
    localJPanel2.add(execButton);
    frame.getRootPane().setDefaultButton(execButton);
    
    stepButton = new JButton("Step");
    stepButton.setMnemonic(83);
    stepButton.setActionCommand("step");
    stepButton.addActionListener(world);
    stepButton.setEnabled(false);
    localJPanel2.add(stepButton);
    
    JButton localJButton1 = new JButton("Load");
    localJButton1.setMnemonic(76);
    localJButton1.setActionCommand("load");
    localJButton1.addActionListener(world);
    localJPanel2.add(localJButton1);
    
    JButton localJButton2 = new JButton("Clear");
    localJButton2.setMnemonic(67);
    localJButton2.setActionCommand("clear");
    localJButton2.addActionListener(world);
    localJPanel2.add(localJButton2);
    
    world.setPreferredSize(new java.awt.Dimension(540, 500));
    userEnv = new Environment("User Variables", null, null);
    frame.getContentPane().add(world);
    
    frame.pack();
    frame.setVisible(true);
    
    codeArea.selectAll();
    codeArea.requestFocusInWindow();
  }
  
  protected void paintComponent(java.awt.Graphics paramGraphics)
  {
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    
    localGraphics2D.setPaint(Color.CYAN);
    localGraphics2D.fill(new java.awt.geom.Rectangle2D.Double(0.0D, 0.0D, getWidth(), 50.0D));
    localGraphics2D.setPaint(Color.GREEN.darker());
    localGraphics2D.fill(new java.awt.geom.Rectangle2D.Double(0.0D, 50.0D, getWidth(), getHeight() - 50));
    
    java.awt.Font localFont = localGraphics2D.getFont();
    localGraphics2D.setFont(new java.awt.Font(localFont.getName(), localFont.getSize(), 14));
    
    for (int i = 0; i < memory.length; i++) {
      for (int j = 0; j < memory[i].length; j++)
      {
        int k = colToLeft(j);
        int m = rowToTop(i);
        
        localGraphics2D.setPaint(Color.LIGHT_GRAY);
        localGraphics2D.fill(new java.awt.geom.Rectangle2D.Double(k, m + 20, 200.0D, 70.0D));
        java.awt.geom.GeneralPath localGeneralPath = new java.awt.geom.GeneralPath(0, 3);
        localGeneralPath.moveTo(k, m + 20);
        localGeneralPath.lineTo(k + 100, m);
        localGeneralPath.lineTo(k + 200, m + 20);
        localGeneralPath.closePath();
        localGraphics2D.setPaint(Color.DARK_GRAY);
        localGraphics2D.fill(localGeneralPath);
        localGraphics2D.setPaint(Color.BLACK);
        localGraphics2D.draw(localGeneralPath);
        localGraphics2D.setPaint(Color.WHITE);
        localGraphics2D.drawString(new Location(i, j).toString(), k + 95, m + 15);
        localGraphics2D.setPaint(Color.BLACK);
        localGraphics2D.draw(new java.awt.geom.Line2D.Double(k, m + 20, k, m + 90));
        localGraphics2D.draw(new java.awt.geom.Line2D.Double(k + 200, m + 20, k + 200, m + 90));
        
        if (memory[i][j] != null) {
          memory[i][j].draw(localGraphics2D);
        }
      }
    }
    TextBox localTextBox = new TextBox(1, Color.ORANGE.darker().darker(), 0, 450, getWidth(), getHeight() - 450);
    Iterator localIterator = userEnv.variables();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      try
      {
        Object localObject = userEnv.get(str);
        if ((localObject instanceof String))
          localObject = "\"" + localObject + "\"";
        localTextBox.addText(Color.WHITE, 0, 14, str + ":  " + localObject);
      }
      catch (JavaTownException localJavaTownException) {}
    }
    


    localTextBox.draw(localGraphics2D);
    

    localGraphics2D.setColor(Color.BLUE);
    localGraphics2D.fill(new java.awt.geom.RoundRectangle2D.Double(postmanLeft, postmanTop + 5, 14.0D, 5.0D, 5.0D, 5.0D));
    localGraphics2D.setColor(Color.BLACK);
    localGraphics2D.draw(new java.awt.geom.RoundRectangle2D.Double(postmanLeft, postmanTop + 5, 14.0D, 5.0D, 5.0D, 5.0D));
    localGraphics2D.setPaint(Color.YELLOW);
    localGraphics2D.fill(new java.awt.geom.Ellipse2D.Double(postmanLeft, postmanTop + 10, 14.0D, 14.0D));
    localGraphics2D.setPaint(Color.BLACK);
    localGraphics2D.draw(new java.awt.geom.Ellipse2D.Double(postmanLeft, postmanTop + 10, 14.0D, 14.0D));
    localGraphics2D.fill(new java.awt.geom.Rectangle2D.Double(postmanLeft + 4, postmanTop + 16, 2.0D, 2.0D));
    localGraphics2D.fill(new java.awt.geom.Rectangle2D.Double(postmanLeft + 9, postmanTop + 16, 2.0D, 2.0D));
    localGraphics2D.draw(new java.awt.geom.Line2D.Double(postmanLeft + 5, postmanTop + 20, postmanLeft + 9, postmanTop + 20));
    localGraphics2D.draw(new java.awt.geom.Line2D.Double(postmanLeft + 7, postmanTop + 24, postmanLeft + 7, postmanTop + 40));
    localGraphics2D.draw(new java.awt.geom.Line2D.Double(postmanLeft + 7, postmanTop + 24, postmanLeft, postmanTop + 31));
    localGraphics2D.draw(new java.awt.geom.Line2D.Double(postmanLeft + 7, postmanTop + 24, postmanLeft + 14, postmanTop + 31));
    

    if (carryingPerson)
    {
      localGraphics2D.setPaint(Color.YELLOW);
      localGraphics2D.fill(new java.awt.geom.Ellipse2D.Double(postmanLeft - 3, postmanTop + 31, 7.0D, 7.0D));
      localGraphics2D.setPaint(Color.BLACK);
      localGraphics2D.draw(new java.awt.geom.Ellipse2D.Double(postmanLeft - 3, postmanTop + 31, 7.0D, 7.0D));
      localGraphics2D.draw(new java.awt.geom.Line2D.Double(postmanLeft, postmanTop + 38, postmanLeft, postmanTop + 46));
    }
    

    if (message != null)
    {
      message.setPosition(postmanLeft + 14, postmanTop + 31);
      message.draw(localGraphics2D);
    }
  }
  
  public static Type getClass(String paramString)
  {
    Type localType = (Type)classes.get(paramString);
    if (localType == null)
      throw new JavaTownException("Class not found:  " + paramString);
    return localType;
  }
  
  public static void addClass(Type paramType)
  {
    classes.put(paramType.getName(), paramType);
    addToTranscript("Defined class " + paramType.getName());
  }
  
  public static void redraw()
  {
    world.repaint();
    try {
      Thread.sleep(250L);
    } catch (InterruptedException localInterruptedException1) {}
    if (isStepping)
    {
      clicked = false;
      execButton.setEnabled(true);
      stepButton.setEnabled(true);
      while (!clicked) {
        try {
          Thread.sleep(1L);
        } catch (InterruptedException localInterruptedException2) {}
      }
    }
  }
  
  public static void gc() {
    java.util.HashSet localHashSet = new java.util.HashSet();
    findPeople(userEnv, localHashSet);
    for (int i = 0; i < memory.length; i++) {
      for (int j = 0; j < memory[i].length; j++) {
        if ((memory[i][j] != null) && (!localHashSet.contains(memory[i][j])))
        {
          movePostmanTo(i, j);
          memory[i][j] = null;
          carryingPerson = true;
          movePostmanTo(-1, -1);
          carryingPerson = false;
        }
      }
    }
    movePostmanTo(-1, -1);
  }
  
  private static void findPeople(Environment paramEnvironment, java.util.Set<Person> paramSet)
  {
    Iterator localIterator1 = paramEnvironment.variables();
    while (localIterator1.hasNext())
    {
      String str = (String)localIterator1.next();
      try
      {
        Object localObject = paramEnvironment.get(str);
        if ((localObject != null) && ((localObject instanceof Person)) && (!paramSet.contains(localObject)))
        {
          Person localPerson = (Person)localObject;
          paramSet.add(localPerson);
          findPeople(localPerson.getEnvironment(), paramSet);
          Iterator localIterator2 = localPerson.stack();
          while (localIterator2.hasNext())
          {
            Environment localEnvironment = (Environment)localIterator2.next();
            findPeople(localEnvironment, paramSet);
          }
        }
      }
      catch (JavaTownException localJavaTownException) {}
    }
  }
  



  public static Location allocateMemory()
  {
    java.util.ArrayList localArrayList = new java.util.ArrayList();
    for (int i = 0; i < memory.length; i++)
      for (int j = 0; j < memory[i].length; j++)
        if (memory[i][j] == null)
          localArrayList.add(new Location(i, j));
    if (localArrayList.size() == 0)
      throw new OutOfMemoryError();
    i = random.nextInt(localArrayList.size());
    Location localLocation = (Location)localArrayList.get(i);
    return localLocation;
  }
  
  private static String dump()
  {
    String str = "";
    for (int i = 0; i < memory.length; i++)
      for (int j = 0; j < memory[i].length; j++)
        str = str + memory[i][j] + " ";
    return str;
  }
  
  private static int rowToTop(int paramInt)
  {
    return 20 + paramInt * 100;
  }
  
  private static int colToLeft(int paramInt)
  {
    return 20 + paramInt * 300;
  }
  

  private static void movePostmanTo(int paramInt1, int paramInt2)
  {
    int i;
    
    int j;
    if ((paramInt1 == -1) && (paramInt2 == -1))
    {
      i = 400;
      j = 250;
    }
    else
    {
      i = rowToTop(paramInt1) + 20;
      j = colToLeft(paramInt2);
      if (paramInt2 == 0) {
        j += 205;
      } else {
        j -= 20;
      }
    }
    while ((postmanTop != i) || (postmanLeft != j))
    {
      int k = 10;
      
      if (postmanTop > i)
      {
        postmanTop -= k;
        if (postmanTop < i) {
          postmanTop = i;
        }
      } else if (postmanTop < i)
      {
        postmanTop += k;
        if (postmanTop > i)
          postmanTop = i;
      }
      if (postmanLeft > j)
      {
        postmanLeft -= k;
        if (postmanLeft < j) {
          postmanLeft = j;
        }
      } else if (postmanLeft < j)
      {
        postmanLeft += k;
        if (postmanLeft > j)
          postmanLeft = j;
      }
      world.repaint();
      try { Thread.sleep(20L);
      } catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public static Person instantiate(String paramString, List paramList, Environment paramEnvironment) {
    java.util.ArrayList localArrayList = new java.util.ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (Expression)localIterator.next();
      localObject2 = ((Expression)localObject1).eval(paramEnvironment);
      localArrayList.add(localObject2);
    }
    
    Object localObject1 = paramEnvironment.getOwner();
    if (localObject1 == null) {
      movePostmanTo(-1, -1);
    }
    else {
      ((Person)localObject1).setWaiting(true);
      localObject2 = ((Person)localObject1).getLocation();
      movePostmanTo(((Location)localObject2).row(), ((Location)localObject2).col());
    }
    
    message = new TextBox(1, Color.WHITE);
    message.addText(Color.BLACK, 0, 14, "new " + paramString);
    localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      localObject2 = localIterator.next();
      if ((localObject2 instanceof String))
        localObject2 = "\"" + localObject2 + "\"";
      message.addText(Color.BLACK, 0, 12, localObject2 + "");
    }
    redraw();
    
    movePostmanTo(-1, -1);
    carryingPerson = true;
    Object localObject2 = allocateMemory();
    movePostmanTo(((Location)localObject2).row(), ((Location)localObject2).col());
    carryingPerson = false;
    
    int i = colToLeft(((Location)localObject2).col()) + 5;
    int j = rowToTop(((Location)localObject2).row()) + 25;
    Person localPerson = new Person(paramString, (Location)localObject2, i, j);
    memory[localObject2.row()][localObject2.col()] = localPerson;
    redraw();
    
    Environment localEnvironment = new Environment("new " + paramString, localPerson.getEnvironment(), localPerson);
    localPerson.pushFrame(localEnvironment);
    Type localType = getClass(paramString);
    Method localMethod = localType.getMethod(paramString, paramList.size());
    for (int k = 0; k < localArrayList.size(); k++)
      localEnvironment.define(localMethod.getParam(k), localArrayList.get(k));
    message = null;
    redraw();
    
    localIterator = localMethod.body();
    Object localObject3; while (localIterator.hasNext())
    {
      localObject3 = (Statement)localIterator.next();
      ((Statement)localObject3).exec(localEnvironment);
    }
    localPerson.popFrame();
    localPerson.say("I live at " + localPerson.getLocation().toString() + ".");
    redraw();
    
    localPerson.sayNothing();
    message = new TextBox(1, Color.WHITE);
    message.addText(Color.BLUE, 0, 14, localPerson.getLocation().toString());
    redraw();
    if (localObject1 == null) {
      movePostmanTo(-1, -1);
    }
    else {
      localObject3 = ((Person)localObject1).getLocation();
      movePostmanTo(((Location)localObject3).row(), ((Location)localObject3).col());
      ((Person)localObject1).setWaiting(false);
    }
    
    message = null;
    
    return localPerson;
  }
  
  public static Object invoke(Person paramPerson, String paramString, List paramList, Environment paramEnvironment)
  {
    java.util.ArrayList localArrayList = new java.util.ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (Expression)localIterator.next();
      localObject2 = ((Expression)localObject1).eval(paramEnvironment);
      localArrayList.add(localObject2);
    }
    
    Object localObject1 = paramEnvironment.getOwner();
    if (localObject1 == null) {
      movePostmanTo(-1, -1);
    }
    else {
      ((Person)localObject1).setWaiting(true);
      localObject2 = ((Person)localObject1).getLocation();
      movePostmanTo(((Location)localObject2).row(), ((Location)localObject2).col());
    }
    
    message = new TextBox(1, Color.WHITE);
    message.addText(Color.BLACK, 0, 14, paramString);
    localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      localObject2 = localIterator.next();
      if ((localObject2 instanceof String))
        localObject2 = "\"" + localObject2 + "\"";
      message.addText(Color.BLACK, 0, 12, localObject2 + "");
    }
    redraw();
    
    Object localObject2 = paramPerson.getLocation();
    movePostmanTo(((Location)localObject2).row(), ((Location)localObject2).col());
    
    Environment localEnvironment = new Environment(paramString, paramPerson.getEnvironment(), paramPerson);
    paramPerson.pushFrame(localEnvironment);
    

    boolean bool = paramPerson.isWaiting();
    paramPerson.setWaiting(false);
    
    String str1 = paramPerson.getType();
    if (str1.equals(paramString))
      throw new JavaTownException("No method " + paramString + " with " + paramList.size() + " parameters");
    Method localMethod = getClass(str1).getMethod(paramString, paramList.size());
    if ((localObject1 != paramPerson) && (!localMethod.isPublic())) {
      throw new JavaTownException("Method " + paramString + " with " + paramList.size() + " parameters is private");
    }
    for (int i = 0; i < localArrayList.size(); i++)
      localEnvironment.define(localMethod.getParam(i), localArrayList.get(i));
    message = null;
    redraw();
    
    try
    {
      localIterator = localMethod.body();
      while (localIterator.hasNext())
      {
        Statement localStatement = (Statement)localIterator.next();
        localStatement.exec(localEnvironment);
      }
    }
    catch (ReturnException localReturnException)
    {
      Object localObject3 = localReturnException.getValue();
      paramPerson.popFrame();
      String str2 = "" + localObject3;
      if ((localObject3 instanceof String))
        str2 = "\"" + localObject3 + "\"";
      paramPerson.say("It's " + str2 + ".");
      

      paramPerson.setWaiting(bool);
      
      redraw();
      
      paramPerson.sayNothing();
      message = new TextBox(1, Color.WHITE);
      message.addText(Color.BLUE, 0, 14, str2);
      redraw();
      if (localObject1 == null) {
        movePostmanTo(-1, -1);
      }
      else {
        Location localLocation2 = ((Person)localObject1).getLocation();
        movePostmanTo(localLocation2.row(), localLocation2.col());
        ((Person)localObject1).setWaiting(false);
      }
      
      redraw();
      
      message = null;
      
      return localObject3;
    }
    
    paramPerson.popFrame();
    

    paramPerson.setWaiting(bool);
    
    paramPerson.say("OK.");
    redraw();
    
    paramPerson.sayNothing();
    if (localObject1 == null) {
      movePostmanTo(-1, -1);
    }
    else {
      Location localLocation1 = ((Person)localObject1).getLocation();
      movePostmanTo(localLocation1.row(), localLocation1.col());
      ((Person)localObject1).setWaiting(false);
    }
    
    redraw();
    
    return null;
  }
  
  public static void exec(Statement paramStatement)
  {
    paramStatement.exec(userEnv);
    gc();
  }
  
  public void actionPerformed(java.awt.event.ActionEvent paramActionEvent)
  {
    String str = paramActionEvent.getActionCommand();
    
    if ((str.equals("execute")) || (str.equals("step")))
    {
      isStepping = str.equals("step");
      clicked = true;
      
      execButton.setEnabled(false);
      stepButton.setEnabled(false);
      
      if (!isRunning)
      {
        new Thread()
        {
          public void run()
          {
            World.access$002(true);
            
            String str = World.codeArea.getText().trim();
            
            Scanner localScanner = null;
            
            try
            {
              localScanner = new Scanner(new java.io.StringReader(str));
              
              Parser localParser = new Parser(localScanner);
              
              for (Statement localStatement : localParser.parseStatements()) {
                localStatement.exec(World.userEnv);
              }
            }
            catch (Throwable localThrowable) {
              localThrowable.printStackTrace();
              
              if ((localThrowable instanceof JavaTownParseException))
              {
                javax.swing.JOptionPane.showMessageDialog(World.frame, localThrowable.getMessage() + "\n\n" + localScanner.getRecent(), "Parsing Error", 0);




              }
              else if ((localThrowable instanceof JavaTownException))
              {
                javax.swing.JOptionPane.showMessageDialog(World.frame, localThrowable.getMessage(), "Run-time Error", 0);


              }
              else
              {


                javax.swing.JOptionPane.showMessageDialog(World.frame, "Report to JavaTown Developer Team\n\n" + localThrowable, "Unknown Error", 0);
                



                localThrowable.printStackTrace();
              }
            }
            
            World.access$400();
            
            World.codeArea.setText("");
            World.addToTranscript(str);
            
            World.access$002(false);
            
            World.execButton.setEnabled(true);
            World.stepButton.setEnabled(true);
          }
        }.start(); }
    } else {
      int i;
      if (str.equals("load"))
      {
        isStepping = false;
        execButton.setEnabled(false);
        stepButton.setEnabled(false);
        
        i = fileChooser.showOpenDialog(frame);
        
        if (i != 0)
        {
          codeArea.requestFocusInWindow();
          execButton.setEnabled(true);
          stepButton.setEnabled(true);
          return;
        }
        java.io.File localFile = fileChooser.getSelectedFile();
        try
        {
          java.io.BufferedReader localBufferedReader = new java.io.BufferedReader(new java.io.FileReader(localFile));
          Scanner localScanner = null;
          
          try
          {
            localScanner = new Scanner(localBufferedReader);
            Parser localParser = new Parser(localScanner);
            for (Statement localStatement : localParser.parseStatements()) {
              localStatement.exec(userEnv);
            }
          }
          catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
            
            if ((localThrowable instanceof JavaTownParseException))
            {
              javax.swing.JOptionPane.showMessageDialog(frame, localThrowable.getMessage() + "\n\n" + localScanner.getRecent(), "Parsing Error", 0);




            }
            else if ((localThrowable instanceof JavaTownException))
            {
              javax.swing.JOptionPane.showMessageDialog(frame, localThrowable.getMessage(), "Run-time Error", 0);


            }
            else
            {


              javax.swing.JOptionPane.showMessageDialog(frame, "Report to JavaTown Developer Team\n\n" + localThrowable, "Unknown Error", 0);
              



              localThrowable.printStackTrace();
            }
          }
          
          resetComputation();
          
          codeArea.setText("");
          
          localBufferedReader.close();
        }
        catch (java.io.IOException localIOException)
        {
          localIOException.printStackTrace();
          javax.swing.JOptionPane.showMessageDialog(frame, "Unable to read " + localFile, "Load Error", 0);

        }
        

      }
      else if (str.equals("clear"))
      {
        i = javax.swing.JOptionPane.showConfirmDialog(frame, "Are you sure you wish to destroy all objects and global variables?", "Confirm Clear", 0);
        


        if (i == 0)
        {

          new Thread() {
            public void run() {
              World.userEnv.clear();
              World.gc();
            }
          }.start();
        }
      }
      else {
        throw new UnsupportedOperationException(str);
      } }
    codeArea.requestFocusInWindow();
  }
  
  private static void resetComputation()
  {
    for (int i = 0; i < memory.length; i++) {
      for (int j = 0; j < memory[i].length; j++)
        if (memory[i][j] != null)
          memory[i][j].resetComputation();
    }
    postmanLeft = 250;
    postmanTop = 400;
    carryingPerson = false;
    message = null;
    
    world.repaint();
    try { Thread.sleep(1L);
    } catch (InterruptedException localInterruptedException) {}
    gc();
  }
  
  public void removeUpdate(javax.swing.event.DocumentEvent paramDocumentEvent)
  {
    codeAreaUpdate();
  }
  
  public void insertUpdate(javax.swing.event.DocumentEvent paramDocumentEvent)
  {
    codeAreaUpdate();
  }
  
  public void changedUpdate(javax.swing.event.DocumentEvent paramDocumentEvent)
  {
    codeAreaUpdate();
  }
  
  private void codeAreaUpdate()
  {
    execButton.setEnabled(true);
    stepButton.setEnabled(true);
  }
  
  private static void addToTranscript(String paramString)
  {
    transcript.append("\n" + paramString);
    transcript.setCaretPosition(transcript.getDocument().getLength());
    codeArea.requestFocusInWindow();
  }
}

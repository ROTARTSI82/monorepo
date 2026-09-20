Code Quality
=

+ Do not include package statements in your code.
+ Always include `public` or `private` access modifiers on everything.
+ When wrapping lines, the line continuation must be indented twice or lined up visually. Prefer indenting twice.
+ Use wildcard import statements instead of specific imports. e.g. use statements like `import java.util.*;` over `import java.util.ArrayList;`
+ Place field declarations at the top of your class.
+ No line should exceed 100 characters in length.
+ Exception code should come at the top of the method. Everything related to checking for and throwing the exception (computing a value to be used in the condition, the if statement itself) counts as exception code. Any code unrelated to the exception conditions / throwing should be placed lower than exception code.
  + If an exception check cannot be checked at the beginning of the method (e.g. requires significant computation / additional statements), check for it when it can be more easily detected as the method continues.
  + Do not attach else statements onto exception checks to connect regular behavior code.
    + Note: else ifs that check for more exceptions are fine.
+ Initialize fields inside the constructor. Do not initialize fields at declaration.
+ Fields should always be declared with the private access modifier.
  + Note: There is one exception to this rule for node classes (ListNode, AssassinNode, IntTreeNode, QuestionNode, HuffmanNode).
+ Class constants should be declared with static and final modifiers.
  + Note: Class constants have to be initialized at declaration unlike how fields should be initialized.
  + Avoid "magic numbers".
+ Use descriptive names.
+ Always include the type parameters when dealing with generic classes like ArrayList, TreeSet, etc. Use the diamond operator.
+ Always declare variables (and fields, parameters, etc.) of a specific interface type instead of the class type, where applicable.
+ If you have lines of repeated or very similar code that need to be executed in different places, group the code of the task into a private helper method.
+ Avoid re-implementing the functionality of already existing methods and standard library functions by just calling those existing methods.
    + For example, prefer isEmpty() to length() == 0, and equalsIgnoreCase() to toLower().equals(toUpper()).

Forbidden features: The following features should NEVER be used:

+ break or continue
+ return from a void method
+ the System.exit method
+ the switch statement
+ try/catch
+ the protected keyword
+ the var keyword
+ Java 8 functional features (e.g., anonymous classes, lambdas, streams, method references)
+ Java 11 features (e.g., local variable type inference, String methods like repeat(n))
+ the toCharArray, join, and matches methods of String
+ the StringBuilder, StringBuffer, StringJoiner, StringTokenizer classes
+ the methods Arrays.fill, Arrays.copyOf, Arrays.copyOfRange, and Arrays.sort
+ the toArray and clone methods of ArrayList
+ the methods Collections.copy and Collections.sort
+ package declarations
+ System.console
+ LinkedHashMap


Commenting
=

At the top of all programs, there should be a header comment of the following form:
```java
/**
 * @author Grant Yang
 * @version {today's date in YYYY.MM.DD}
 * CSE 123
 * X0: Something
 * TA: Benoit Le
 */
```

+ Class comments are comments that describe a class as a whole. The purpose of a class comment is to give a client an understanding of what a class accomplishes at a high level. As such, class comments should be present in all classes and should describe all relevant functionality of a class. Some questions to guide your thinking are what does the class do abstractly? What behaviors can an instance of the class do? Is it an interface? Does it implement an interface? Is it a subclass or superclass?

Thinking of your program as a really detailed book, the class comment would be a summary that describes things without too much detail.

Example:
```java
/**
 * This class is a budgeter. It takes in monthly or daily expenses and income. It then draws
 * conclusions about your spending habits.
 */
public class Budgeter {
    // CODE HERE
}
```

+ Method comments are comments that describe what a method accomplishes, the inputs to the method, and the output of the method. You can think of method comments as mini-specs of a method! All method comments should include a description of the method’s behavior, the parameters of the method, and the outcomes of the method (Does it return a value? Does it manipulate a shared object?).

Make sure your javadocs follow BERP convention:
+ Behavior - What the method does
+ Exceptions - Documenting any errors the method might raise on invalid input or state
+ Returns - What the returned value(s) are
+ Parameters - What the parameter(s) are

These are all the things that should be mentioned in a method comment if applicable. If a method doesn’t have any returns for example, that part of the comment can be omitted. Continuing the book analogy, you can think of a method comment as a chapter summary.

Also think about pre/post conditions:
+ Pre-Condition: A pre-condition is a statement or expression used to inform the client about the accepted range of input. Only input that meets these criteria is guaranteed to produce a correct output. All other input (input outside the specified criteria) is not guaranteed to compute correctly. 
  + Pre-Conditions usually include discussion on valid Parameters and the Exceptions that are thrown when the input is invalid.
+ Post-Condition: A post-condition is a statement or expression informing the client on the behavior and range of output to expect from your code.
  + Post-Conditions usually include discussion on the overall Behavior of the method and the possible Return values.

Keep in mind that not all methods will have both a pre and post conditions. Just as with BERP, some methods won’t have parameters or exceptions, so talking about pre-conditions in this case is unnecessary.

+ Avoid implementation details: This applies to both class and method comments. For example, we don’t need to document that our method accomplishes what it does by using a loop – someone reading our code can figure that out just by looking at the method! More importantly though is that mentioning implementation details bogs down our clients. Is it important for clients to know the underlying mechanisms at play in our method? All the client really needs to know is how the method behaves which you provide insight into through your documentation. This is not an exhaustive list but implementation details include mentioning fields, looping constructs, and the data structures being created.
    + The one exception to implementation details are private methods. With private methods, it’s okay to mention implementation details since documentation for private methods is not client facing – only the implementors can see documentation for private methods. When documenting private methods, you should still follow BERP or Pre/Post and mention all necessary details for the implementors to know how the method behaves.

Example of good commenting:
```java
/**
 * This method calculates net profit or loss based on monthly income and daily spending.
 * 
 * @param income the user’s income this month
 * @param spending the amount the user spent each day this month
 * @return The net profit or loss. Positive if profit, negative if loss.
 * @throws IllegalArgumentException if the given income is negative.
 */
public static int calculateNetExpenses(int income, int spending) {
    if (income < 0)
        throw new IllegalArgumentException("Income can't be a negative integer!");
    return income - (spending * DAYS_IN_MONTH);
}
```


Examples of a BAD comment, mentioning implementation details:

```java
/**
 * This method takes income and spending and uses the formula
 * income - (spending * days in a month) to return the user’s net income.
 * 
 * @param income the user’s income this month
 * @param spending the amount the user spent each day this month
 * @return The net profit or loss. Positive if profit, negative if loss.
 * @throws IllegalArgumentException if the given income is negative (income < 0).
 */
public static int calculateNetExpenses(int income, int spending) {
    if (income < 0)
        throw new IllegalArgumentException("Income can't be a negative integer!");
    return income - (spending * DAYS_IN_MONTH);
}
```

General Instructions
=
Keep suggestions about actual code to a minimum (most of your suggestions should be writing javadocs).
When you write javadocs or suggest code, however, make sure to follow these guideline religiously.

Your job is to 1) carefully review my code to spot and fix
any violations of either code quality or commenting guidelines, and 2) generate high-quality, professional comments
following these guidelines. Keep your comments as short as possible
while still capturing the full behavior of the client-facing interface.

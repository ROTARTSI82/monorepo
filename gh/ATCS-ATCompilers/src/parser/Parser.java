package parser;

import ast.*;
import scanner.Scanner;
import scanner.Token;

import java.util.*;

import static ast.NamedExpression.namedOp;
import static parser.BoxedValue.box;

/**
 * Parser.java
 *
 * @author Grant Yang
 * @version 2024.03.06
 * <p>
 * This parser parses the stream of tokens provided by a Scanner
 * and evaluates as it goes, interpreting Pascal on the fly.
 * It uses a right-recursive context-free grammar and parses
 * from the bottom up using recursive descent.
 * See the map definition in the constructor for a full
 * list of operators this parser supports.
 */
public class Parser
{
    final private Scanner scanner;
    private Token currentToken;
    private PrecedenceLevelParser exprParser = null;

    /**
     * Constructs a new parser to parse the tokens scanned in by
     * the scanner specified.
     *
     * @param scanner The scanner to read from
     */
    public Parser(Scanner scanner)
    {
        this.scanner = scanner;
        currentToken = scanner.nextToken();

        for (var op : OperatorSAM.PRECEDENCE)
        {
            boolean rtl = op.getKey();
            this.exprParser = new PrecedenceLevelParser(rtl, op.getValue(), this.exprParser);
        }
    }

    /**
     * Consumes a token from the input stream, with checks for its specific
     * content and type to emit an error on invalid input.
     *
     * @param cont The expected string content of the token, or null to specify any string.
     * @param type The expected type of the token, or null to specify any type.
     * @throws IllegalArgumentException If the read token does not match the expected one
     * @precondition The next token in the scanner matches the expected criteria
     * @postcondition The scanner has advanced forward by 1 token
     */
    private void eat(String cont, Token.Type type) throws IllegalArgumentException
    {
        if ((type == null || currentToken.type().equals(type))
                && (cont == null || currentToken.content().equalsIgnoreCase(cont)))
            currentToken = scanner.nextToken();
        else
            throw new IllegalArgumentException("%s is illegal: expected %s (%s type)"
                    .formatted(currentToken, cont, type));
    }

    /**
     * Overloaded version of eat() that only checks for the content of the token
     * and not the type of token.
     *
     * @param token The expected string content of the token
     * @throws IllegalArgumentException If the read token does not match the expected one
     */
    private void eat(String token) throws IllegalArgumentException
    {
        eat(token, null);
    }

    /**
     * Parse an integer from the input stream
     *
     * @return An AST node representing the integer value that appeared
     * @precondition The scanner is located at the beginning of an integer literal
     * @postcondition The scanner has advanced past the integer literal
     */
    private Expression parseNumber()
    {
        String cont = currentToken.content();
        eat(null, Token.Type.Numeric);
        return namedOp((e) -> box(Integer.parseInt(cont)), "#" + cont);
    }

    /**
     * Parses a and executes a Pascal statement from the input stream.
     * Statement types include blocks (BEGIN ... END;), WRITELN() calls,
     * READLN() calls, and expressions handled by `exprParser`.
     *
     * @return An AST node representing the statement
     * @precondition The input stream is located at the beginning of a statement
     * @postcondition The input stream has advanced past the end of the statement,
     * but no code has been executed.
     */
    public Expression parseStatement()
    {
        switch (currentToken.content().toUpperCase())
        {
            case "WHILE" ->
            {
                eat("WHILE");
                Expression condition = exprParser.parse();
                eat("DO");
                Expression state = parseStatement();
                return new WhileLoop(condition, state);
            }
            case "IF" ->
            {
                eat("IF");
                Expression cond = exprParser.parse();
                eat("THEN");
                Expression state = parseStatement();

                // elseClause needs to be final to be used in a lambda, so I have to do this
                boolean hack = false;
                if (currentToken.content().equalsIgnoreCase("ELSE"))
                {
                    eat("ELSE");
                    hack = true;
                }

                Expression elseClause = hack ? parseStatement() : Expression.NO_OP;
                return (e) -> cond.eval(e).asBool() ? state.eval(e) : elseClause.eval(e);
            }
            case "CONTINUE" ->
            {
                eat("CONTINUE");
                eat(";");
                return (e) ->
                {
                    throw new ContinueException();
                };
            }
            case "BREAK" ->
            {
                eat("BREAK");
                eat(";");
                return (e) ->
                {
                    throw new BreakException();
                };
            }
            case "FOR" ->
            {
                eat("FOR");
                Expression start = exprParser.parse();
                eat("TO");
                Expression end = exprParser.parse();
                eat("DO");
                Expression body = parseStatement();
                return new ForLoop(start, end, body);
            }
            case "BEGIN" ->
            {
                eat("BEGIN");
                ArrayList<Expression> blk = new ArrayList<>();
                while (!currentToken.content().equalsIgnoreCase("END"))
                    blk.add(parseStatement());
                eat("END");
                eat(";");
                return (e) -> blk.stream().map((s) -> s.eval(e)).toList().getLast();
            }
            case "EXIT" ->
            {
                eat("EXIT");
                eat(";");
                return (e) ->
                {
                    throw new ReturnException();
                };
            }
            case "RETURN" ->
            {
                eat("RETURN");
                Expression expr = exprParser.parse();
                eat(";");
                return (e) ->
                {
                    e.setVariable(e.getFrameName(), expr.eval(e).get());
                    throw new ReturnException();
                };
            }
            default ->
            {
                Expression value = exprParser.parse();
                eat(";");
                return value;
            }
        }
    }

    /**
     * Parses a Pascal program from the input stream,
     *
     * @return An AST node representing the parsed program
     */
    public Program parseProgram()
    {
        Map<String, ProcedureDeclaration> procedures = new HashMap<>();
        while (currentToken.content().equalsIgnoreCase("PROCEDURE"))
        {
            eat("PROCEDURE");
            String val = currentToken.content();
            eat(null, Token.Type.Identifier);
            eat("(");
            ArrayList<String> argSlots = new ArrayList<>();
            while (!currentToken.content().equals(")"))
            {
                argSlots.add(currentToken.content());
                eat(null, Token.Type.Identifier);
                if (currentToken.content().equals(","))
                    eat(",");
                else
                    break;
            }
            eat(")");
            eat(";");
            Expression stmt = parseStatement();
            procedures.put(val, new ProcedureDeclaration(stmt, argSlots));
        }

        Expression main = parseStatement();
        eat(null, Token.Type.EOF);
        return new Program(main, procedures);
    }

    /**
     * Parses a factor, the highest precedence level and the basic building blocks
     * in Pascal. Factors include identifiers (variables), strings, unary operators (-, NOT),
     * parenthesis, and special values like arrays (array[1..5]) along with TRUE and FALSE
     *
     * @return An AST node representing the parsed factor
     * @precondition The input stream is located at the beginning of a valid factor expression.
     * @postcondition The input stream has advanced past the factor.
     */
    private Expression parseFactor()
    {
        switch (currentToken.content().toUpperCase())
        {
            case "(" ->
            {
                eat("(");
                Expression ret = exprParser.parse();
                eat(")");
                return ret;
            }
            case "-" ->
            {
                eat("-");
                Expression expr = parseFactor();
                return namedOp((e) -> box(-expr.eval(e).asInt()), "-" + expr);
            }
            case "NOT" ->
            {
                eat("NOT");
                Expression expr = parseFactor();
                return namedOp((e) -> box(!expr.eval(e).asBool()), "NOT " + expr);
            }
            case "TRUE" ->
            {
                eat("TRUE");
                return Expression.TRUE;
            }
            case "FALSE" ->
            {
                eat("FALSE");
                return Expression.FALSE;
            }
            case "ARRAY" ->
            {
                eat("array");
                eat("[");
                Expression lo = exprParser.parse();
                eat("..");
                Expression hi = exprParser.parse();
                eat("]");
                return namedOp(
                        (e) -> box(new PascalArray(lo.eval(e).asInt(), hi.eval(e).asInt())),
                        "arr[" + lo + ".." + hi + "]"
                );
            }
        }

        if (currentToken.type().equals(Token.Type.StringLiteral))
        {
            String ret = currentToken.content();
            eat(ret);
            return namedOp((e) -> box(ret), "\"" + ret + "\"");
        }

        if (currentToken.type().equals(Token.Type.Identifier)
                || currentToken.type().equals(Token.Type.Keyword))
        {
            String id = currentToken.content();
            eat(id);

            if (currentToken.content().equals("("))
            {
                eat("(");
                ArrayList<Expression> args = new ArrayList<>();
                while (!currentToken.content().equals(")"))
                {
                    args.add(exprParser.parse());
                    if (currentToken.content().equals(","))
                        eat(",");
                    else
                        break;
                }
                eat(")");
                return new ProcedureCall(id, args);
            }
            else if (currentToken.content().equals("["))
            {
                eat("[");
                Expression idx = exprParser.parse();
                eat("]");
                return namedOp(
                        (e) -> ((PascalArray) e.getVariable(id).get()).at(idx.eval(e).asInt()),
                        "$" + id + "[" + idx + "]"
                );
            }

            return namedOp((e) -> e.getVariable(id), "$" + id);
        }

        return parseNumber();
    }

    /**
     * This class represents a precedence level of operators to be parsed,
     * and implements a method for parsing such expressions of that precedence level
     * from the input stream. This class generalizes parseTerm() and parseExpression().
     */
    private class PrecedenceLevelParser
    {
        private final PrecedenceLevelParser next;
        private final boolean rightAssociative;
        private final List<String> operators;

        /**
         * Construct a new parser for a specific precedence level
         *
         * @param rtl  If true, this operator is right-associative.
         * @param ops  A list of operators that this precedence level should try to parse.
         * @param next The next lower precedence level after this, to form a linked list
         */
        public PrecedenceLevelParser(boolean rtl, List<String> ops,
                                     PrecedenceLevelParser next)
        {
            this.rightAssociative = rtl;
            this.operators = ops;
            this.next = next;
        }

        /**
         * Parse an expression of this precedence level from the input stream of
         * the parser, falling back to either the `next` precedence level
         * or to `parseFactor()` if `next` is null.
         *
         * @return An AST node representing the expression that was parsed
         */
        public Expression parse()
        {
            LinkedList<String> ops = new LinkedList<>();
            LinkedList<Expression> values = new LinkedList<>();

            Expression ret = next == null ? parseFactor() : next.parse();
            if (rightAssociative)
                values.add(ret);

            while (operators.contains(currentToken.content()))
            {
                String op = currentToken.content();
                eat(op);
                ops.add(op);
                values.add(next == null ? parseFactor() : next.parse());
            }

            while (!ops.isEmpty())
            {
                if (rightAssociative)
                {
                    Expression right = values.removeLast();
                    Expression left = values.removeLast();
                    String opName = ops.removeLast();
                    values.add(new BinOp(opName, left, right));
                }
                else
                {
                    String opName = ops.removeFirst();
                    Expression rhs = values.removeFirst();
                    ret = new BinOp(opName, ret, rhs);
                }
            }

            if (rightAssociative)
                return values.getLast();
            else
                return ret;
        }
    }
}

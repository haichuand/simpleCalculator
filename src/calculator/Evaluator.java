package calculator;
import java.util.*;

/**
 * Class to evaluate arithmetic expressions, such as 3+2*((5+4)*2-7)
 * Only integers and the operators +-/*() are allowed. Divisions are integer divisions; decimal digits are dropped.
 * Expressions must be syntactically correct, otherwise exceptions might be thrown.
 */
public class Evaluator {

	private Stack<Operand> opdStack; //operand stack from arithmetic expression
	private Stack<Operator> oprStack; //operator stack from arithmetic expression
	
	//Hashmap to map operator string input to operator class.
	//The HashMap is used to match the operator string with the corresponding operator object
	static HashMap<String, Operator> operators = new HashMap<String, Operator>();
	public Evaluator() {
		opdStack = new Stack<Operand>();
		oprStack = new Stack<Operator>();		
		operators.put("+", new AdditionOperator());
		operators.put("-", new SubtractionOperator());
		operators.put("*", new MultiplicationOperator());
		operators.put("/", new DivisionOperator());
		operators.put("#", new HashOperator());
		operators.put("!", new ExclamationOperator());
		operators.put("(", new LeftParenOperator());
		operators.put("(~", new LeftParenPopOperator()); //left parenthesis operator when popping opdStack, has lower priority
		operators.put(")", new RightParenOperator());
	}
	/**
	 * Finds the value of the arithmetic expression.
	 * @param expr The arithmetic expression to be evaluated. Only integers and +-/*() are allowed
	 * @return The value of the arithmetic expression. If the expression contains syntax errors, returns Integer.MIN_VALUE.
	 */
	public int eval(String expr) {
		String tok;

		// init stack - necessary with operator priority schema;		
		oprStack.clear();
		opdStack.clear();
		oprStack.push(new HashOperator()); //push "#" operator as indicator for bottom of stack, handle boundary case in generic way

		String delimiters = "+-*/()#! ";		
		StringTokenizer st = new StringTokenizer(expr+"!",delimiters,true); // the 3rd arg is true to indicate to use the delimiters as tokens, too
				
		//break the string into tokens, check for validity and push into operand/operator stacks
		//when the current operator has equal or less priority than the previous one, evaluate the previous one first
		while (st.hasMoreTokens()) {
			if ( !(tok = st.nextToken()).equals(" ")) {	// filter out spaces
				if (Operand.check(tok)) {	// check if tok is an operand
					opdStack.push(new Operand(tok)); 
				}
				else {
					if (!Operator.check(tok)) { //check if tok is an operator
						System.out.println("*****invalid    token******");
						System.exit(1);
					}

					Operator newOpr = operators.get(tok); //get the operator object from HashMap

					while (((Operator)oprStack.peek()).priority() >= newOpr.priority()) { //pop previous operators with higher priority scores
						Operator oldOpr = ((Operator)oprStack.pop());
						Operand op2 = (Operand)opdStack.pop();
						Operand op1 = (Operand)opdStack.pop();
						opdStack.push(oldOpr.execute(op1,op2));
					}
					
					//before pushing LeftParenOperator "(" to oprStack, change to LeftParenPopOperator "(~" with lower priority score
					if (newOpr.equals(operators.get("(")))
						newOpr=operators.get("(~");
					
					oprStack.push(newOpr);
					
					if (newOpr.equals(operators.get(")"))) { //pop the left and right parentheses operators. Before RightParenOperator is pushed to oprStack, the stack is already popped to the matching LeftParenPopOperator
						oprStack.pop();
						oprStack.pop();
					}
				}
			}
		}		
		return opdStack.pop().getValue();
	}
}

/**
 * Class for doing calculation with integer operands
 * @author Haichuan Duan
 */
class Operand {
	private int theOperand;
	
	/**
	 * Constructor for integer operands. It uses Integer.parseInt() to convert strings to integers.
	 * If a NumberFormatException is thrown by Integer.parseInt() due to syntax errors, it prints an error message and exits the program.
	 * @param opr The operator in String format.
	 */
	Operand(String opr) {
		try {
			theOperand=Integer.parseInt(opr);
		}
		catch (NumberFormatException e) {
			System.out.println("parseInt: NumberFormatException");
			System.exit(1);
		}
	}
	
	/**
	 * Constructor for integer operands.
	 * @param val The integer for the operand.
	 */
	Operand(int val) {
		theOperand=val;
	}
	
	/**
	 * Check if the String tok is a valid integer operand
	 * @param tok The token string to check
	 * @return True if String tok is a valid integer operand; false otherwise.
	 */
	static boolean check(String tok) {
		try {
			Integer.parseInt(tok);
		}
		catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get the integer value of the Operand object
	 * @return The integer value represented by the Operand object
	 */
	int getValue() {
		return theOperand;
	}
}

/**
 * Abstract class for the operators +,-,*,/,#,(,(~ and )
 * @author Fudou
 *  */
abstract class Operator {
	/**
	 * Find the priority score of the operator. The scores are: 0 for "#", "(~"; 1 for "!", ")"; 2 for "+", "-"; 3 for "*", "/"
	 * @return The priority score of the operator
	 */
	public abstract int priority();
	
	/**
	 * Check if String tok is a valid operator token
	 * @param tok The String to check
	 * @return True if String tok is a valid operator token, false otherwise.
	 */
	static boolean check(String tok) {
		if (Evaluator.operators.containsKey(tok))
			return true;
		return false;
	}
	/**
	 * Calculate the value of the expression opd1-Operator-opd2
	 * @param opd1 The first operand
	 * @param opd2 The second operand
	 * @return Value of the expression for +-/* operators. Should not be used for the bogus operators #! and parenthesis operators (,(~ and ), even though it returns Ingeter.MIN_VALUE for implementation purposes.
	 */
	public abstract Operand execute(Operand opd1, Operand opd2);
}

/**
 * Class for addition operator "+". Has priority score of 2
 */
class AdditionOperator extends Operator {

	public int priority() {
		return 2;
	}
	
	public Operand execute(Operand opd1, Operand opd2) {
		return (new Operand(opd1.getValue() + opd2.getValue()));
	}
}

/**
 * Class for subtraction operator "-". Has priority score of 2
 */
class SubtractionOperator extends Operator {

	public int priority() {
		return 2;
	}
	
	public Operand execute(Operand opd1, Operand opd2) {
		return (new Operand(opd1.getValue() - opd2.getValue()));
	}
}

/**
 * Class for multiplication operator "*". Has priority score of 3
 */
class MultiplicationOperator extends Operator {

	public int priority() {
		return 3;
	}
	
	public Operand execute(Operand opd1, Operand opd2) {
		return (new Operand(opd1.getValue() * opd2.getValue()));
	}
}

/**
 * Class for division operator "/". Has priority score of 3.
 */
class DivisionOperator extends Operator {

	public int priority() {
		return 3;
	}
	
	public Operand execute(Operand opd1, Operand opd2) {
		return (new Operand(opd1.getValue() / opd2.getValue()));
	}
}

/**
 * Class for hash operator "#". Used as bogus operator to push to bottom of operator stack. Has priority score of 0.
 */
class HashOperator extends Operator {

	public int priority() {
		return 0;
	}
	
	public Operand execute(Operand opd1, Operand opd2) {
		return (new Operand(Integer.MIN_VALUE));
	}
}

/**
 * Class for exclamation operator "!". Used as bogus operator to push to top of operator stack. Has priority score of 1.
 */
class ExclamationOperator extends Operator {

	public int priority() {
		return 1;
	}
	
	public Operand execute(Operand opd1, Operand opd2) {
		return (new Operand(Integer.MIN_VALUE));
	}
}

/**
 * Class for left parenthesis operator "(". Has priority score of 7.
 */
class LeftParenOperator extends Operator { //left parenthesis operator has the highest priority score, so it is always pushed to the oprStack

	public int priority() {
		return 7;
	}
	
	public Operand execute(Operand opd1, Operand opd2) {
		return (new Operand(Integer.MIN_VALUE));
	}
}

/**
 * Class for left parenthesis operator when popping from stack, represented as the string "(~". Has priority score of 0.
 */
class LeftParenPopOperator extends Operator { //before pushed to the oprStack, the left parenthesis operator is changed LeftParenPopOperator with lowest priority score, so that it won't be popped as an operator

	public int priority() {
		return 0;
	}
	
	public Operand execute(Operand opd1, Operand opd2) {
		return (new Operand(Integer.MIN_VALUE));
	}
}

/**
 * Class for right parenthesis operator ")". Has priority score of 1.
 */
class RightParenOperator extends Operator { //right parenthesis operator has lower priority than +-*/ operators, so before being pushed to oprStack, it will pop +-*/ operators and stop at the matching LeftParenPopOperator, which has a lower priority score than RightParenOperator

	public int priority() {
		return 1;
	}
	
	public Operand execute(Operand opd1, Operand opd2) {
		return (new Operand(Integer.MIN_VALUE));
	}
}
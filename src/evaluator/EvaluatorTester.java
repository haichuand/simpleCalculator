package evaluator;

/**
 * Tester program for Evaluator class. Example input: EvaluatorTester 3+((2-5)*4+8)/2 10/5*3-(8*(4+3)-1)*7
 * @author Haichuan Duan
 */
public class EvaluatorTester {
	public static void main(String[] args) {
		Evaluator anEvaluator = new Evaluator();
		for (String arg : args) {
		System.out.println(arg + " = " + anEvaluator.eval(arg));
		}
	}
}

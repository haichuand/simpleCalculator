package calculatorJFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;


/**Calculator applet to calculate arithmetic expression containing only integers, the operators "+", "-", "*", "/", "(" and ")".
 * The arithmetic expression can be entered by using the buttons.
 * Once the "=" button is pressed, it calls Evaluator class to calculate the result of the arithmetic expression.
 * It is assumed that the input arithmetic expression is always syntactically correct.
 * @author Haichuan Duan
 */
public class CalculatorApplication extends JFrame implements ActionListener {
	
	private TextField txField = new TextField();
	private Panel buttonPanel = new Panel();
	private Button buttons[] = new Button[20]; //total 20 buttons on the calculator, numbered from left to right, up to down
	//bText[] array contains text on corresponding buttons
	private static final String bText[] = {"7","8","9","+","4","5","6","-","1","2","3","*","0",".","=","/","(",")","C","CE"};
	/**
	 * Initialize the calculator by setting the panel, text field and buttons.
	 */
	public CalculatorApplication(){
		
		setLayout(new BorderLayout());
		add(txField, BorderLayout.NORTH);
		txField.setEditable(false);
		add(buttonPanel, BorderLayout.CENTER);
		buttonPanel.setLayout(new GridLayout(5,4));
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		for (int i=0; i<20; i++) //create 20 buttons with corresponding text in bText[] array
			buttons[i] = new Button(bText[i]);
		
		for (int i=0; i<20; i++) //add buttons to button panel
			buttonPanel.add(buttons[i]);

		for (int i=0; i<20; i++) //set up buttons to listen for mouse input
			buttons[i].addActionListener(this);		
	}
	
	public static void main(String[] args) {
		CalculatorApplication calc = new CalculatorApplication();
		calc.setSize(300, 400);
		calc.setVisible(true);		
	}
	/**
	 * Listen for ActionEvent such as mouse clicks, identify sources and process commands	 *
	 */
	public void actionPerformed(ActionEvent arg0) { //
		
		boolean found=false;
		int i=-1;
		while (i<20 && !found) { //find the button that is clicked (the source of ActinEvent)
			i++;
			if (arg0.getSource().equals(buttons[i]))
				found = true;				
		}
		
		if (i<18 && i!=13 && i!=14) //do not display "." and "=" in the text field. "." is ignored while "=" triggers expression evaluation
			txField.setText(txField.getText() + bText[i]);
		
		if (arg0.getSource() == buttons[18]) { //"C" button clears last character
			String currentText = txField.getText();
			if (!currentText.isEmpty()) //if current text field is empty, does nothing
				txField.setText(currentText.substring(0, currentText.length()-1));
		}
		
		if (arg0.getSource() == buttons[19]) { //"CE" button clear all text field
			txField.setText("");
		}
		
		if (arg0.getSource() == buttons[14]) { //"=" button pressed; triggers evaluation of arithmetic expression
			Evaluator myEvaluator = new Evaluator();
			String expression = txField.getText();
			if (!expression.isEmpty()) //only evaluate if the string is not empty
				txField.setText(String.valueOf(myEvaluator.eval(expression)));
		}				
	}
	
	/*
	public void windowClosing(WindowEvent e)
	{		
		System.exit(0);
	}
	public void windowOpened(WindowEvent e)	{ }
	
	public void windowIconified(WindowEvent e){ }
	
	public void windowClosed(WindowEvent e)	{ }
	public void windowDeiconified(WindowEvent e){ }
	public void windowActivated(WindowEvent e)	{ }
	public void windowDeactivated(WindowEvent e){ }
	*/

}

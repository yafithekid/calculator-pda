import java.util.*;
import java.lang.*;
import java.io.*;
import java.math.*;


class Util{
	public static String transitionFile = "transition.txt";
	/*
	//versi 1 : dengan double
	public static InputSymbol calculate(InputSymbol _a,InputSymbol _op,InputSymbol _c){
		double a = Double.parseDouble(_a.value);
		char op = _op.type();
		double c = Double.parseDouble(_c.value);
		InputSymbol ret = new InputSymbol();

		if(op == '+'){
			ret.value = String.valueOf(a + c);
		} else if (op == '-'){
			ret.value = String.valueOf(a - c);
		} else if (op == '*'){
			ret.value = String.valueOf(a * c);
		} else if (op == '/'){
			ret.value = String.valueOf(a / c);
		} else {
			ret.value = String.valueOf(-c);
		}
		return ret;
	}*/
	//versi 2 : dengan bigdecimal
	public static InputSymbol calculate(InputSymbol _a,InputSymbol _op,InputSymbol _c){
		BigDecimal a = new BigDecimal(_a.value);
		char op = _op.type();
		BigDecimal c = new BigDecimal(_c.value);
		InputSymbol ret = new InputSymbol();

		if(op == '+'){
			ret.value = (a.add(c)).toString();
		} else if (op == '-'){
			ret.value = (a.subtract(c)).toString();
		} else if (op == '*'){
			System.out.println(a + " " + c);
			ret.value = (a.multiply(c)).toString();
		} else { // '/
			ret.value = (a.divide(c)).toString();
		}
		return ret;
	}
	public static boolean haveDot(String s){
		boolean retval = false;
		for(int i = 0; i < s.length(); ++i)
			if (s.charAt(i) == '.') retval = true;
		return retval;
	}
	public static InputSymbol flipSign(InputSymbol _a){
		InputSymbol a = new InputSymbol();
		if (_a.value.charAt(0) == '-'){
			a.value = _a.value.substring(1);
			return a;
		} else {
			a.value = '-' + _a.value;
			return a;
		}
	}
}


class InputSymbol{
	public String value;
	public void create(String value){
		this.value = value;
	}
	public char type(){
		if (value.equals("Z") || value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/") || value.equals(".") || value.equals("(") || value.equals(")")){
			return value.charAt(0);
		} else if (value.equals("N")){
			return 'N';
		} else {
			//check if it is a number
			for(int i = 0; i < (int) value.length(); ++i){
				if (value.charAt(i) < '0' || value.charAt(i) > '9'){
					if ((i != 0 || value.charAt(i) != '-') && value.charAt(i)!='.'){
						return '?';
					}
				}
			}
			return 'N';
		}
	}
	public boolean isOperator(){
		return this.type() == '+' || this.type() == '-' || this.type() == '*' || this.type() == '/';
	}
	public boolean isNumber(){
		return type() == 'N';
	}
	public int degree(){
		if (this.type() == '+' || this.type() == '-')
			return 1;
		if (this.type() == '*' || this.type() == '/')
			return 2;
		return 0;
	}
};


class Transition{
	public int currentState;
	public InputSymbol topStack;
	public InputSymbol input;

	public int nextState;
	public String nextMove;
	
	public Transition(){
		topStack = new InputSymbol();
		input = new InputSymbol();
	}
	public boolean isValidTransition(InputSymbol input,int currentState,char topStack){
		//System.out.println(this.input.type() + " " + this.currentState + " " + this.topStack);
		//System.out.println(input.type() + " " + currentState + " " + topStack);
		//System.out.println("");
		return this.input.type() == input.type() && this.currentState == currentState && this.topStack.type()==topStack;
	}

};




class PDA {
	private int currentState;
	private Stack<InputSymbol> stack;
	private Vector<Transition> transitions;

	public boolean illegalFlag;
	public void readState(int N){
	}
	public PDA(){
		transitions = new Vector<Transition>();
		stack = new Stack<InputSymbol>();
		InputSymbol i = new InputSymbol(); i.create("Z");
		stack.push(i);
		illegalFlag = false;
	}
	public void setState(int newState){
		this.currentState = newState;
	}
	public void readTransition(){
		try {
			Scanner sc = new Scanner(new File(Util.transitionFile));
			int n = sc.nextInt();	
			for(int i = 0; i < n; ++i){
				Transition t = new Transition();
				t.currentState = sc.nextInt();
				t.input.value = sc.next();
				t.topStack.value = sc.next();
				t.nextState = sc.nextInt();
				t.nextMove = sc.next();
				transitions.add(t);
			}
			System.out.println(n); 
			for(Transition p: transitions){ 
				System.out.println(p.input.type());
			}

		} catch(FileNotFoundException f){
			System.out.println("file transition.txt not found!");
		} catch(Exception e){
			System.out.println(e);
		}
		
	}

	public int searchTransition(InputSymbol c){
		//System.out.println(c.type + " " + currentState)
		for(int i = 0; i < transitions.size(); ++i){
			if (transitions.get(i).isValidTransition(c,currentState,stack.peek().type()))
				return i;
		}
		return -1;
	}

	public boolean isExistTransition(InputSymbol c){
		return (searchTransition(c) != -1);
	}

	public void doTransition(InputSymbol c){
		int idx = searchTransition(c);
		Transition trans = transitions.get(idx);
		currentState = trans.nextState;
		
		if (trans.nextMove.equals("PUSH")){
			stack.push(c);
		} else {
		}
	}

	public boolean evaluate(){
		if (stack.peek().type()==')'){
			stack.pop(); //pop the )
			boolean stop = false;
			while (!stop){
				InputSymbol top = stack.pop();
				
				if (stack.peek().type() == '('){
					//reach the (. put the number
					stack.pop(); //pop the (
					stack.push(top); // push the number
					stop = true;
				} else {
					InputSymbol operator = stack.pop();
					if (!stack.peek().isNumber()){
						//special case: the expression is a - sign
						if (operator.type() == '-'){
							stack.push(Util.flipSign(top));
						} else {
							//unknown
							return false;
						}
					} else {
						InputSymbol prev = stack.pop();
						stack.push(Util.calculate(prev,operator,top));
					}
				}
			}

		}  else if (stack.peek().type() == '.'){
			//concatenante with previous.
			stack.pop();
			if (Util.haveDot(stack.peek().value)){
				//N
				this.illegalFlag = true;
			} else {
				//N.N
				stack.peek().value += '.';
			}

		} else if (stack.peek().isNumber()){
			InputSymbol top = stack.pop();

			if (stack.peek().type() == 'N'){
				//concatenante
				stack.peek().value += top.value;
			} else {
				//push again. wait until being processed
				stack.push(top);
			}
		} else if (stack.peek().isOperator()){
			while (stack.peek().isOperator()){
				InputSymbol opnext = stack.pop();
				if (!stack.peek().isNumber()){
					//the candidates for negative sign
					stack.push(opnext);
					break;
				} else {
					//three alternatives on stack condition
					//	a op b opnext 
					//  a opnext
					InputSymbol b = stack.pop();

					if (!stack.peek().isOperator()){
						//  a opnext
						stack.push(b);
						stack.push(opnext);
						break;
					} else {
						//a op b opnext
						InputSymbol op = stack.pop();
						InputSymbol a = stack.pop();

						//the left is higher or same degree
						if (op.degree() >= opnext.degree()){
							//evaluate the left
							stack.push(Util.calculate(a,op,b));
							stack.push(opnext);
						} else {
							stack.push(a);
							stack.push(op);
							stack.push(b);
							stack.push(opnext);
							break;
						}
					}
				}
			}
		}
		return true;
	}

	public boolean readSymbol(InputSymbol c){	
		System.out.println("top symbol = " + stack.peek().type());
		System.out.println("input now = " + c.type());
		boolean retval;
		if (isExistTransition(c)){
			doTransition(c);
			System.out.println("success");

			//epsilon transition
			if (currentState == 2){ 
				if (!evaluate()) return false;
				currentState = 1;
			}
			retval = true;
		} else {
			retval = false;
		}
		System.out.println();
		System.out.println("current state = " +currentState);
		System.out.println("top symbol = " + stack.peek().type() + " " + " top value = " + stack.peek().value);
		System.out.println("-----------------------------------");
		return retval;
	}

	public String getResult(){
		String top = stack.pop().value;
		if (stack.empty() || stack.peek().type() != 'Z'){
			top = new String("Illegal expression");
		}
		return top;
	}

};


class Main{
	static InputSymbol inputs[];
	static int nInputs;

	static PDA calc;
	public static String expression;

	/**
	 * membaca input simbol dan meng-convert menjadi alfabet yang diterima dari input
	 */
	public static void readInputs(){
		System.out.println("Masukkan Ekspresi Matematika : ");
		Scanner sc = new Scanner(System.in);
		String s = sc.nextLine();
		expression = s;
		//add dummy parentheses
		s = "(" + s + ")";
		//remove spaces
		while (s.contains(" ")){ s = s.replace(" ","");}

		nInputs = s.length();
		inputs = new InputSymbol[nInputs];
		for(int i = 0; i < nInputs; ++i){
			String tmp = "" + s.charAt(i);
			inputs[i] = new InputSymbol(); inputs[i].create(tmp);
			System.out.println(inputs[i].type());
		}
	}

	public static void main(String args[]){
		readInputs();
		calc = new PDA();
		calc.readTransition();
		calc.setState(1);
		int it;

		for(it = 0; it < nInputs; ++it){
			System.out.println("reading : " + inputs[it].value + " " + inputs[it].type());
			
			try {
				if(!calc.readSymbol(inputs[it])){
					System.out.println("ERROR : illegal expression : ");
					System.out.println("("+expression+")");
					for(int i = 0; i < nInputs; ++i){
						System.out.print((i == it) ? '^':' ');
					}
					return;
				}

			} catch (ArithmeticException e){
				System.out.println("ERROR : Division by zero"); return;
			} catch (Exception e){
				System.out.println(e);
				System.out.println("ERROR : Unknown error"); return;
			}
		}
		System.out.println("("+expression+")");
		System.out.println(" = " +calc.getResult());
		
	}
}
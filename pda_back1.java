import java.util.*;
import java.lang.*;
import java.io.File;


class Configuration{
	public static String transitionFile = "E:\\\\tubes\\kalkulator\\transition.txt";
}

class InputSymbol{
	public char type;
	public String value;
	public void create(String value){
		this.value = value;
		if (value == "Z0" || value == "+" || value == "-" || value == "*" || value == "/" || value == "." || value == "(" || value == ")"){
			this.type = value.charAt(0);
		} else if (value == "e"){
			this.type = 'E';
		} else {
			this.type = 'N';
		}
	}
};


class Transition{
	public int currentState;
	public String topStack;
	public InputSymbol input;

	public int nextState;
	public String nextMove;
	
	public Transition(){
		input = new InputSymbol();
	}
	public boolean isValidTransition(InputSymbol input,int currentState,String topStack){
		System.out.println(this.input.type + " " + this.currentState + " " + this.topStack);
		System.out.println(input.type + " " + currentState + " " + topStack);
		return this.input.type.equals(input.type) && this.currentState == currentState && this.topStack.equals(topStack);
	}

};




class PDA{
	private int currentState;
	private Stack<InputSymbol> stack;
	private Vector<Transition> transitions;

	public boolean illegalFlag;
	public void readState(int N){
	}
	public PDA(){
		transitions = new Vector<Transition>();
		stack = new Stack<InputSymbol>();
		InputSymbol i = new InputSymbol(); i.create("Z0");
		stack.push(i);
		illegalFlag = -1;
	}
	public void setState(int newState){
		this.currentState = newState;
	}
	public void readTransition(){
		try {
			Scanner sc = new Scanner(new File(Configuration.transitionFile));
			int n = sc.nextInt();	
			for(int i = 0; i < n; ++i){
				Transition t = new Transition();
				t.currentState = sc.nextInt();
				t.input.type = sc.next();
				t.topStack = sc.next();
				t.nextState = sc.nextInt();
				t.nextMove = sc.next();
				transitions.add(t);
			}
			System.out.println(n); 
			for(Transition p: transitions){ 
				System.out.println(p.input.type);
			}
		} catch (Exception e){
			System.out.println(e);
		}
		
	}

	public int searchTransition(InputSymbol c){
		//System.out.println(c.type + " " + currentState)
		for(int i = 0; i < transitions.size(); ++i){
			if (transitions.get(i).isValidTransition(c,currentState,stack.peek().type))
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

	public void evaluate(){
		if (stack.peek().type==')'){
			Vector<InputSymbol> tmp = new Vector<InputSymbol>();
			//pop )
			stack.pop();
			//pop (
			stack.pop();
		} else if (stack.peek().type.equals("N"){
			InputSymbol now = new InputSymbol();
			now = stack.pop();
			if (stack.peek().type.equals(".") && stack.peek().type.equals("N")){

			}
		}
	}

	public void readSymbol(InputSymbol c){
		//special case
		if (currentState == 2){ 
			evaluate();
		}
		if (isExistTransition(c)){
			doTransition(c);
			System.out.println("success");
		} else {
			System.out.println("Illegal Expression");
			//throw new Exception("illegal input symbol : " + c.value);
		}
	}


};


class Main{
	static InputSymbol inputs[];
	static int nInputs;

	static PDA calc;

	/**
	 * membaca input simbol dan meng-convert menjadi alfabet yang diterima dari input
	 */
	public static void readInputs(){
		Scanner sc = new Scanner(System.in);
		String s = sc.nextLine();
		nInputs = s.length();

		inputs = new InputSymbol[nInputs];
		for(int i = 0; i < nInputs; ++i){
			String tmp = "" + s.charAt(i);
			inputs[i] = new InputSymbol(); inputs[i].create(tmp);
		}
	}

	public static void main(String args[]){
		readInputs();
		calc = new PDA();
		calc.readTransition();
		calc.setState(1);
		int it;
		for(it = 0; it < nInputs; ++it){
			System.out.println("reading : " + inputs[it].value + " " + inputs[it].type);
			calc.readSymbol(inputs[it]);
		}
		
	}
}
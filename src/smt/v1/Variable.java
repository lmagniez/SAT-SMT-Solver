package smt.v1;

public class Variable extends Element{

	protected Integer value;
	
	
	public Variable(String name, String type)
	{
		super(name,type);
		value=null;
	}
	
	public Variable(int v,String name, String type)
	{
		super(name,type);
		value=v;
	}

	@Override
	public String toString() {
		return "Variable [value=" + value + ", name=" + name + ", type=" + type
				+ "]";
	}

	

	
	
	
	
}

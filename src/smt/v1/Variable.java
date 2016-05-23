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

	public String affiche()
	{
		return "Variable [value=" + value + ", name=" + name + ", type=" + type
		+ "]";
	}
	
	public String printTypes()
	{
		String res="";
		res+=name+" type: "+type;
		return res;
	}
	
	@Override
	public String toString() {
		
		return name;
	}

	

	
	
	
	
}

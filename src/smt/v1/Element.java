package smt.v1;

public class Element implements Cloneable{

	protected String name;
	protected String type;
	
	public Element(String name, String type)
	{
		this.name=name;
		this.type=type.toLowerCase();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	
	
	@Override
	public String toString() {
		return "Element [name=" + name + ", type=" + type + "]";
	}
	
	
	
	
}

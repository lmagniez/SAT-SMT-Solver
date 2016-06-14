package smt.v1;

import java.util.Vector;

/**
 * Variable or Function \n
 * Contains name and type
 * @author loick
 *
 */

public abstract class Element implements Cloneable{

	protected String name; //name
	protected String type; //type of variable, or type of return for function
	protected Vector <Relation> relations; //relations linked to this Element (used for resolution)
	
	public Element(String name, String type)
	{
		this.name=name;
		this.type=type.toLowerCase();
		this.relations = new Vector<Relation>();
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
	
	public String affiche()
	{
		return "Element [name=" + name + ", type=" + type + "]";
	}
	public String printTypes()
	{
		return "";
	}
	
	
	
	
}

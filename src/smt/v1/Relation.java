package smt.v1;


/**
 * relation values: 
 * -2  -1  0  1   2    10
 * <=   <  =  >   =>   !=
 */

public class Relation {

	protected Element e1;
	protected Element e2;
	protected int relation; 
	protected int id;
	
	public static int ID=1;
	
	public Relation(Element e1, Element e2, int relation)
	{
		this.e1=e1;
		this.e2=e2;
		this.relation=relation;
		
		if(relation==-2||relation==0||relation==2)id=ID;
		else id=-ID;
		ID++;
	}

	@Override
	public String toString() {
		return "Relation [e1=" + e1 + ", e2=" + e2 + ", relation=" + relation
				+ ", id=" + id + "]";
	}

	
	
	
	
	
}

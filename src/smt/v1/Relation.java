package smt.v1;

import java.util.Vector;


/**
 * 2 elements 
 * relation values: 
 * -3  -2  -1    1   2    3
 * !=   <=   <    >   =>   =
 */

public class Relation {

	protected Element e1;//first element
	protected Element e2;//second element
	protected int relation;//type of relation
	protected int id;//id of the relation
	protected boolean usedForClause;//used for a clause (if not, it will become a clause when checking satisfiability)
	
	public static int ID=1;
	
	public Relation(Element e1, Element e2, int relation)
	{
		this.e1=e1;
		this.e2=e2;
		this.relation=relation;
		this.usedForClause=false;
		
		if(relation==2||relation==3||relation==1)id=ID;
		else id=-ID;
		ID++;
	}
	
	public static Relation findById(Vector<Relation> r, int id)
	{
		for(int i=0; i<r.size(); i++)
		{
			if(Math.abs(r.get(i).id)==Math.abs(id))
				return r.get(i);
		}
		return null;
	}

	public String affiche()
	{
		return "Relation [e1=" + e1 + ", e2=" + e2 + ", relation=" + relation
						+ ", id=" + id + "]";
	}
	
	@Override
	public String toString() {
		
		String sign = "";
		if(relation==3)	sign="=";
		else if(relation==1) sign=">";
		else if(relation==2) sign=">=";
		else if(relation==-1) sign="<";
		else if(relation==-2) sign="<=";
		else if(relation==-3) sign="!=";
			
			
		return e1.toString() + sign + e2.toString();
		
	}

	
	
	
	
	
}

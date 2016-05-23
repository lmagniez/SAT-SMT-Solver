package smt.v1;

import java.util.Vector;

/**
 * set of relation separated by OR 
 *
 */

public class Clause {

	protected Vector<Relation> relations;
	
	public Clause(Vector<Relation> r)
	{
		this.relations = r;
	}

	public Clause(Relation r)
	{
		Vector<Relation> vec = new Vector<Relation>();
		vec.add(r);
		this.relations = vec;
	}
	
	@Override
	public String toString() {
		
		String res="";
		for (int i=0; i<relations.size(); i++)
		{
			res+=relations.get(i).toString();
			if(i+1<relations.size())
				res+=" ^ ";
		}
		return res;
		
		//return "Clause [relations=" + relations + "]";
	}
	
	
	
	
}

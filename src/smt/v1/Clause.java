package smt.v1;

/**
 * set of relation separated by OR 
 *
 */

public class Clause {

	private Relation[] relations;
	
	public Clause(Relation[] r)
	{
		this.relations = r;
	}
	
	
}

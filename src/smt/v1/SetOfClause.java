package smt.v1;

import java.util.Vector;

public class SetOfClause {

	private Vector<Clause> clauses;
	
	public SetOfClause()
	{
		clauses= new Vector<Clause>();
	}

	public void addClause(Clause c)
	{
		clauses.add(c);
	}
	
	public void removeClause(Clause c)
	{
		clauses.remove(c);
	}
	
	public void removeClause(int i)
	{
		clauses.remove(i);
	}
	
}

package cdcl.v1;

import java.util.Vector;

public class Variable {

	public static int actualLevel;
	
	private int variable;
	private int value; //0: false, 1:true, -1:not assigned
	private int decisionLevel;
	private int cut;
	private Variable[] antecedants;
	private int w; //clause that allowed to get this variable
	
	public Variable(int variable)
	{
		this.variable=variable;
		value=-1;
		decisionLevel=-1;
		cut=-1;
		w=-1;
		antecedants=null;
	}
	
	public void addToGraph(int value, int decisionLevel, int cut, int w, Variable[] antecedants)
	{
		this.value=value;
		this.decisionLevel=decisionLevel;
		this.cut=cut;
		this.w=w;
		this.antecedants=antecedants;
	}
	
	public static Variable find(int variable, Variable[] v)
	{
		for(int i=0; i<v.length; i++)
		{
			if(v[i].variable==variable)
				return v[i];
		}
		return null;
	}
	
	public void addAntecedants(Variable[] v)
	{
		for(int i=0; i<v.length; i++)
		{
			addAntecedant(v[i]);
		}
	}
	
	public void addAntecedant(Variable v)
	{
		
		if(antecedants==null)
		{
			antecedants=new Variable[1];
			antecedants[0]=v;
		}
		else
		{
			Variable[] newAntecedants=new Variable[antecedants.length+1];
			for(int i=0; i<antecedants.length; i++)
				newAntecedants[i]=antecedants[i];
			newAntecedants[antecedants.length]=v;
			antecedants=newAntecedants;
		}
		
	}
	
	public static int[] createConflictClause(int[][] cls, Variable[] v)
	{
		int location = searchForUIP(v);
		
		int[] clause = cls[v[location].w];//get the clause
		
		
		Vector<Variable> vect =new Vector<Variable>(); 
		
		//add the antecedants from UIP to vect
		for(int i=0; i<v[location].antecedants.length; i++)
			vect.add(v[location].antecedants[i]);
		
		//go through each variables before UIP
		while(vect.size()!=0)
		{
			location--;
			for(int i=0; i<v[location].antecedants.length; i++)
				if(!vect.contains(v[location].antecedants[i]))//check if already added
					vect.add(v[location].antecedants[i]);
			
			int[] clause2=cls[vect.get(0).w];
			vect.remove(0);
			clause = fuse(clause,clause2);
			//make a swap if there's a problem?
		}
		
		
		return clause;
		
	}
	
	//return the location of the first UIP
	public static int searchForUIP(Variable[] v)
	{
		
		//for each variable
		for(int i=v.length-1; i<=0; i--)
		{
			int cptSame=0;
			int decisionLevel=v[i].decisionLevel;
			int cut = v[i].cut;
			
			for(int j=0; j<v.length; j++)
			{
				if(v[j].decisionLevel==decisionLevel&&
						v[j].cut==cut)
				{
					cptSame++;
				}
			}
			
			if(cptSame==1)
				return i;
			
		}
		
		return v.length-1;
		
	}
	
	
	public static int[] fuse(int[] cls1, int[] cls2)
	{
		for(int i=0; i<cls1.length; i++)
		{
			for(int j=0; j<cls2.length; j++)
			{
				if(cls1[i]==cls2[j])
				{
					//delete cls1[i] and cls2[j] then fuse
					deleteFromTab(cls1, i);
					deleteFromTab(cls2, j);				
				}
			}
		}
		return add(cls1,cls2);
	}
	
	public static void deleteFromTab(int[] tab, int j)
	{
		int[] newTab= new int[tab.length-1];
		int cpt=0;
		for(int i=0; i<tab.length; i++)
		{
			if(i!=j)
			{
				newTab[cpt]=tab[i];
				cpt++;
			}
			
		}
		tab=newTab;
	}
	
	public static int[] add(int[] tab, int[] tab2)
	{
		int[] newTab = new int[tab.length+tab2.length];
		for(int i=0; i<tab.length; i++)
		{
			newTab[i]=tab[i];
		}
		for(int i=0; i<tab2.length; i++)
		{
			newTab[tab.length+i]=tab2[i];
		}
		
		return newTab;
		
	}
	
	public static Variable[] createVariable(int[] tab)
	{
		Variable[] v = new Variable[tab.length];
		for(int i=0; i<tab.length; i++)
		{
			v[i]=new Variable(tab[i]);
		}
		return v;
	}
	
}

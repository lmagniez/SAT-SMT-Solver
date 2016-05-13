package cdcl.v1;

import java.util.Vector;

public class Variable {

	public static int actualLevel=0;
	public static int actualDecision=0;//number of variable set
	
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
	
	public static void MAJActualDecision(Variable[] v)
	{
		int cpt=0;
		for(int i=0; i<v.length; i++)
		{
			if(v[i].getValue()!=-1)
				cpt++;
		}
		actualDecision=cpt;
	}
	
	public static void afficheDecision(Variable[] v)
	{
		for(int i=0;i<v.length; i++)
		{
			if(v[i].getValue()!=-1)
			{
				if(v[i].getValue()==0)
					System.out.print("-");
				System.out.print(v[i].getVariable()+" ");
			}	
		}
		System.out.println();
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
	
	//put to 0 every Variable up to the decision level
	public static int restartVariable(Variable[] v, int decisionLevel)
	{
		System.out.println("decide" + decisionLevel);
		int cpt=0;
		for(int i=0; i<v.length; i++)
		{
			if((v[i].decisionLevel==decisionLevel&&v[i].cut==0))
			{	System.out.println("HELLO THERE "+v[i].getVariable());
			 	
			}
			
			if(!(v[i].decisionLevel<=decisionLevel)&&v[i].decisionLevel!=-1&&v[i].variable!=-1)
			//else if(v[i].decisionLevel>=decisionLevel&&v[i].decisionLevel!=-1&&v[i].variable!=-1)
			{
				System.out.println(v[i].getVariable());
				v[i].cut=-1;
				v[i].antecedants=null;
				v[i].decisionLevel=-1;
				v[i].value=-1;
				v[i].w=-1;
				cpt++;
			}
		}
		//Variable.actualLevel++;
		return cpt;
	}
	
	//find variable from decision level with cut=0
	public static Variable findCutZero(Variable[] v, int decisionLevel)
	{
		for(int i=0; i<v.length; i++)
		{
			if(v[i].decisionLevel==decisionLevel&&v[i].decisionLevel==0)
				return v[i];
		}
		return null;
	}
	
	public static int[] createConflictClause(int[][] cls, Variable[] v)
	{
		int location = searchForUIP(v);
		
		System.out.println("UIP**************************");
		Variable.afficher(v[location]);
		System.out.println("********************************");
		
		int[] clause = cls[v[location].w];//get the clause
		
		//Variable.afficher(v);
		System.out.println(location);
		
		Vector<Variable> vect =new Vector<Variable>(); 
		
		//add the antecedants from UIP to vect
		for(int i=0; i<v[location].antecedants.length-1; i++)
		{
			System.out.println("add "+v[location].antecedants[i]);
			vect.add(v[location].antecedants[i]);
			
		}
		
		/*System.out.println("taille vect "+vect.size());
		for(int i=0; i<vect.size();i++)
		{
			Variable[] t = vect.get(0).antecedants;
			for(int j=0; j<t.length; j++)
			{
				if(t[j]!=null)Variable.afficher(t[j]);
			}
		}
		*/
		
		System.out.println("ah"+vect);
/////////////////////////////////////////////////////////////		
		//go through each variables before UIP
		while(vect.size()!=1)
		{
			
			Variable var =vect.get(0);
			if(var!=null)
			{
				if(var.antecedants!=null)
				{
					Variable.afficher(var);
					for(int i=0; i<var.antecedants.length-1; i++)
						if(!vect.contains(var.antecedants[i]))//check if already added
							vect.add(var.antecedants[i]);
					
					System.out.println("test" + vect + " " + vect.get(0).w);
					vect.get(0).afficher(vect.get(0));
					
					if(vect.get(0).w!=-1)//not useful if decided by algorithm
					{	
						int[] clause2=cls[vect.get(0).w];
						clause = fuse(clause,clause2);
						
						System.out.println("OKOKOK");
						for(int i=0; i<clause.length; i++)
							System.out.print(clause[i]+" ");
						
						
					}//make a swap if there's a problem?
				}
			}
			vect.remove(0);
		}
		
		
		return clause;
		
	}
	
	
	
	//return the location of the first UIP (with the 
	public static int searchForUIP(Variable[] v)
	{
		System.out.println("*************************");
		System.out.println("Search for UIP");
		//Variable.afficher(v);
		
		Vector<Integer> results = new Vector<Integer>();
		
		//get the conflict level
		int ConflictLevel=v[v.length-1].decisionLevel;
		
		//for each variable
		for(int i=v.length-1; i<=0; i--)
		{
			int cptSame=0;
			int decisionLevel=v[i].decisionLevel;
			int cut = v[i].cut;
			
			//Not useful if not same level than conflict
			if(ConflictLevel==decisionLevel)
			{	
				//for each variable, count how many equivalent
				for(int j=0; j<v.length; j++)
				{
					if(v[j].decisionLevel==decisionLevel&&
							v[j].cut==cut)
					{
						cptSame++;
					}
				}
			
				System.out.println("cpt!! "+cptSame);
				
				if(cptSame==1)
					results.add(i);
			}
		}
		
		
		
		if(results.size()==0)
			return v.length-1;//return conflict
		
		//return the maxCut
		else
		{
			int maxCut=-1;
			for(int i=0; i<results.size(); i++)
			{
				if(v[results.get(i)].cut>maxCut)
					maxCut=i;
			}
			return maxCut;
		}
		
		
	}
	

	
	
	public static int[] fuse(int[] cls1, int[] cls2)
	{
		
		System.out.println("cls1");
		for(int i=0; i<cls1.length; i++)
		{
			System.out.print(cls1[i]+ " ");
		}
		System.out.println();
		System.out.println("cls2");
		
		for(int i=0; i<cls2.length; i++)
		{
			System.out.print(cls2[i]+ " ");
		}
		System.out.println();
		
		for(int i=0; i<cls1.length; i++)
		{
			for(int j=0; j<cls2.length; j++)
			{
				if(cls1[i]==cls2[j]*-1&&cls1[i]!=0)
				{
					//System.out.println("=="+cls1[i]+" "+cls2[j]);
					
					//delete cls1[i] and cls2[j] then fuse
					cls1=deleteFromTab(cls1, cls1[i]);
					cls2=deleteFromTab(cls2, cls2[j]);		
					
				}
				
				else if(cls1[i]==cls2[j])
					cls1=deleteFromTab(cls1,cls1[i]);
				
			}
		}
		
		int[] clause=add(cls1,cls2);
		//deleteFromTab(clause,0);
		
		System.out.println("LA CLAUSE");
		for(int i=0; i<clause.length; i++)
		{
			System.out.print(clause[i]+ " ");
		}
		
		return clause;
	}
	
	public static int[] deleteFromTab(int[] tab, int j)
	{
		System.out.println("delete "+j);
		ConstructeurClauseV4.affichetab(tab);
		
		int[] newTab= new int[tab.length-1];
		int cpt=0;
		for(int i=0; i<tab.length; i++)
		{
			if(tab[i]!=j)
			{
				newTab[cpt]=tab[i];
				cpt++;
			}
			
		}
		tab=newTab;
		return tab;
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
	
	public int getValue()
	{
		return this.value;
	}
	
	public int getVariable()
	{
		return this.variable;
	}
	
	public int getLevel()
	{
		return this.decisionLevel;
	}

	public int getCut()
	{
		return this.cut;
	}
	
	public static Variable getLast(Variable[] v)
	{
		int maxDecision=Integer.MIN_VALUE;
		int maxCut=Integer.MIN_VALUE;
		int res=-1;
		
		for(int i=0; i<v.length; i++)
		{
			if(v[i].variable==-1)
			{
				//do nothing
			}
			else if(maxDecision<v[i].getLevel())
			{
				maxDecision=v[i].getLevel();
				maxCut=v[i].getCut();
				res=i;
			}
			else if(maxDecision==v[i].getLevel()&&maxCut<v[i].getCut())
			{
				maxCut=v[i].getCut();
				res=i;
			}
		}
		return v[res];
		
	}
	
	public int getDecisionLevel() {
		return decisionLevel;
	}

	public void setDecisionLevel(int decisionLevel) {
		this.decisionLevel = decisionLevel;
	}

	public void setCut(int cut) {
		this.cut = cut;
	}

	public void setValue(int e)
	{
		this.value=e;
	}
	
	public static void afficher(Variable[] v)
	{
		for(int i=0; i<v.length; i++)
		{
			System.out.println(v[i].variable + ": value:"+v[i].value+" decisionLevel:"+v[i].decisionLevel+  
					" cut:"+v[i].cut+" antecedants:"+v[i].antecedants + " w: "+v[i].w);
		}
	}
	
	public static void afficher(Variable v)
	{
		System.out.println(v.variable + ": value:"+v.value+" decisionLevel:"+v.decisionLevel+  
				" cut:"+v.cut+" antecedants:"+v.antecedants+" w: "+v.w);
	}
}

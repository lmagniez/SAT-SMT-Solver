package cdcl.v2;

import java.util.Deque;
import java.util.Vector;

import cdcl.v1.Variable;
import dpll.v1.ConstructeurClause;

public class VariableV2 {

	public static int actualLevel=0;
	public static int actualDecision=-1;//number of variable set
	public static Vector<Integer> backtracks = new Vector<Integer>();
	
	private int variable;
	private int value; //0: false, 1:true, -1:not assigned
	private int decisionLevel;
	private int cut;
	private VariableV2[] antecedants;
	private int w; //clause that allowed to get this variable
	
	public VariableV2(int variable)
	{
		this.variable=variable;
		value=-1;
		decisionLevel=-1;
		cut=-1;
		w=-1;
		antecedants=null;
	}
	
	/**
	 * Modify the assignment of the Variable
	 * @param value
	 * @param decisionLevel
	 * @param cut
	 * @param w
	 * @param antecedants
	 */
	public void addToGraph(int value, int decisionLevel, int cut, int w, VariableV2[] antecedants)
	{
		this.value=value;
		this.decisionLevel=decisionLevel;
		this.cut=cut;
		this.w=w;
		this.antecedants=antecedants;
	}
	
	/**
	 * Update the variable actualDecision according to list of Variable v \n
	 * actualDecision: actual level of decision
	 * @param v
	 */
	public static void MAJActualDecision(VariableV2[] v)
	{
		int cpt=0;
		for(int i=0; i<v.length; i++)
		{
			if(v[i].getValue()!=-1)
				cpt++;
		}
		actualDecision=cpt;
	}
	
	/**
	 * printing function for testing
	 * @param v
	 */
	public static void afficheDecision(VariableV2[] v)
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
	
	/**
	 * get the decision for the list of Variable (value)
	 * @param v
	 * @return
	 */
	public static int[] getDecision(VariableV2[] v)
	{
		int[] decision= new int[v.length];
		for(int i=0;i<v.length; i++)
		{
			decision[i]=v[i].getValue();
		}
		return decision;
	}
	
	/**
	 * Find a variable in a list of Variable according to its name
	 * @param variable variable name
	 * @param v list of Variable
	 * @return Variable if found, else null
	 */
	public static VariableV2 find(int variable, VariableV2[] v)
	{
		for(int i=0; i<v.length; i++)
		{
			if(v[i].variable==variable)
				return v[i];
		}
		return null;
	}
	
	public void addAntecedants(VariableV2[] v)
	{
		for(int i=0; i<v.length; i++)
		{
			addAntecedant(v[i]);
		}
	}
	
	public void addAntecedant(VariableV2 v)
	{
		
		if(antecedants==null)
		{
			antecedants=new VariableV2[1];
			antecedants[0]=v;
		}
		else
		{
			VariableV2[] newAntecedants=new VariableV2[antecedants.length+1];
			for(int i=0; i<antecedants.length; i++)
				newAntecedants[i]=antecedants[i];
			newAntecedants[antecedants.length]=v;
			antecedants=newAntecedants;
		}
		
	}
	
	
	/**
	 * Backtrack function. Cancel assignment to every Variable up to the decision level (up to cut=0)
	 * @param v list of variable
	 * @param decisionLevel decisionLevel where to backtrack
	 * @param liste stack of variable
	 * @return
	 */
	public static int restartVariable(VariableV2[] v, int decisionLevel, Vector<VariableV2> liste)
	{
		while(!liste.isEmpty()&&liste.get(liste.size()-1).getDecisionLevel()!=decisionLevel)
			liste.remove(liste.get(liste.size()-1));
		
		System.out.println("decide" + decisionLevel);
		int cpt=0;
		for(int i=0; i<v.length; i++)
		{
			
			
			if((v[i].decisionLevel>decisionLevel)&&v[i].decisionLevel!=-1)
			//else if(v[i].decisionLevel>=decisionLevel&&v[i].decisionLevel!=-1&&v[i].variable!=-1)
			{
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

	/**
	 * Search for an UIP 
	 * @param liste stack of Variable
	 */
	public static void searchUIP(Vector<VariableV2> liste)
	{
		//get the position of the last decision
		int pos=liste.size()-2;
		while(liste.get(pos).getCut()!=0)
			pos--;
		
		if(pos==liste.size()-2)return;
		
		//search an UIP from that decision
		for(int i=pos+1; i<liste.size()-2; i++)
		{
			
			if(liste.get(i).getCut()!=liste.get(i-1).getCut()
					&&liste.get(i).getCut()!=liste.get(i+1).getCut())
			{
				//found the uip
				while(liste.size()!=i+1)
					liste.remove(liste.size()-1);
				return;
			}
		}
		return;
		
	}
	/**
	 * Generate a conflict clause according to the graph (need a conflict)
	 * @param cls set a clause
	 * @param v list of Variable
	 * @param liste stack of Variable
	 * @return conflict clause
	 */
	public static int[] generateConflictClause (int[][] cls, VariableV2[] v, Vector<VariableV2> liste)
	{
	
		
		///////////////////////////
		//GENERATION DE LA CLAUSE//
		///////////////////////////
		
		//searchUIP(liste);
		
		System.out.println("taille "+ liste.size());
		
		//commence à -1
		VariableV2 v0 = liste.get(liste.size()-1);
		liste.remove(liste.size()-1);
		int[] c0= cls[v0.w];
		
		//afficher(v0);
		
		//tant que le haut de pile n'est pas la variable pariée
		while(v0.cut!=0)
		{
			//on dépile
			v0 = liste.get(liste.size()-1);
			liste.remove(liste.size()-1);
			
			//si v0 est dans c0 (valeur absolue)
			if(contains(c0,v0.variable)&&v0.cut!=0)
			{
				c0=fuse(c0, cls[v0.w]);
				System.out.print("c0=");
				ConstructeurClauseV5.affichetab(c0);
			}
			
		}
		
		c0=deleteDoublon(c0);
		c0=deleteDoublon2(c0);
		System.out.print("c0=");
		ConstructeurClauseV5.affichetab(c0);
		return c0;
		
		
		
	}
	
	//search where to backtrack and which Variable to add
	
	/**
	 * Analyse Conflict Clause\n
	 * Search for a level k to backtrack, and which Variable to keep
	 * @param conflictClause conflict clause
	 * @param size 
	 * @param v list of Variable
	 * @param liste stack of Variable
	 */
	public static void conflictAnalysis (int[] conflictClause, int size, VariableV2[] v, Vector<VariableV2> liste) 
	{
		System.out.println("clause");
		ConstructeurClauseV5.affichetab(conflictClause);
		
		int d=VariableV2.actualLevel;
		
		int k=-1;
		int min=Integer.MAX_VALUE;
		VariableV2 v0=null;
		
		//search for v0 (decisionLevel d, cut=0)		
		//search for k (the max before d)
		for(int i=0; i<conflictClause.length-1; i++)
		{
			
			VariableV2 tmp= VariableV2.find(Math.abs(conflictClause[i]), v);
			VariableV2.afficher(tmp);
			System.out.println("d="+d);
			
			//test
			if(min>tmp.decisionLevel)
				min=tmp.decisionLevel;
			///
			
			if(tmp.decisionLevel==d)
				v0=tmp;
			if(tmp.decisionLevel<d)
			{
				if(k<tmp.decisionLevel)
					k=tmp.decisionLevel;
			}
			
			//test
			if(k==-1)
				k=min-1;
			
		}
		
		
		
		//backtrack to k
		System.out.println("backtrack to "+k);
		backtracks.add(k);
		VariableV2.actualLevel=k;
		restartVariable(v, k, ConstructeurClauseV5.decisions);
		
		//VariableV2.afficher(v);
		
		if(VariableV2.actualLevel==-1)VariableV2.actualLevel=1;
		
		
		//decide v0 by resolution
		int[][] cls= new int[2][];
		cls[0] = new int[1];
		cls[1] = conflictClause;
		
		for(int i=0; i<conflictClause.length-1; i++)
		{
			
			VariableV2 tmp= VariableV2.find(Math.abs(conflictClause[i]), v);
			if(tmp.value==0)
				cls=ConstructeurClauseV5.unitPropagation(-tmp.variable, cls);
			else if(tmp.value==1)
				cls=ConstructeurClauseV5.unitPropagation(tmp.variable, cls);
		}
		
		System.out.println("conflict clause après résolution");
		if(cls.length!=1)ConstructeurClauseV5.affichetab(cls[1]);
		System.out.println("actualLevel:"+Variable.actualLevel);
		
		VariableV2.MAJActualDecision(v);
		
		
		
		
		
		//VariableV2.actualLevel++;
		//add v0
		v0.decisionLevel=VariableV2.actualLevel;
		if(cls[1][0]>0)
			v0.value=1;
		else
			v0.value=0;
		v0.cut=0;
		v0.w=size-1;//cls learned
		liste.addElement(v0);
		
		return;
		
	}
	
	
	
	
	//test if the tab contains elt (absolute variable)
	public static boolean contains(int[] tab, int elt)
	{
		for(int i=0; i<tab.length; i++)
		{
			if(Math.abs(tab[i])==Math.abs(elt)) return true;
		}
		return false;
	}
	
	/**
	 * Fuse together 2 cls to make one (delete l and -l, and double l)
	 * @param cls1
	 * @param cls2
	 * @return new clause 
	 */
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
		
		
		return clause;
	}
	
	/**
	 * Check if there's 2 time the same literal in the clause, delete it if found (only one needed)
	 * @param tab clause
	 * @return new clause
	 */
	public static int[] deleteDoublon(int[] tab)
	{
		boolean found=false;
		int location=0;
		do{
			found=false;
			for(int i=0; i<tab.length; i++)
			{	
				for(int j=i+1; j<tab.length; j++)
				{
					if(tab[i]==tab[j])
					{
						location=i;
						found=true;
						break;
					}
				}
				if(found)break;
			}
			//trouver doublon, suppr
			if(found)
			{
				int cpt=0;
				int[] tab2 = new int[tab.length-1];
				for(int i=0; i<tab.length; i++)
				{
					if(i!=location)
					{	
						tab2[cpt]=tab[i];
						cpt++;
					}
				}
				tab=tab2;
			}
			
		}while(found);
		
		return tab;
		
	}
	
	/**
	 * Check if there's l and -l, if found, delete both
	 * @param tab clause
	 * @return new clause
	 */
	public static int[] deleteDoublon2(int[] tab)
	{
		boolean found=false;
		int location=0;
		int location2=0;
		do{
			found=false;
			for(int i=0; i<tab.length; i++)
			{	
				for(int j=i+1; j<tab.length; j++)
				{
					if(tab[i]==-tab[j])
					{
						location=i;
						location2=j;
						found=true;
						break;
					}
				}
				if(found)break;
			}
			//trouver doublon, suppr les deux
			if(found)
			{
				int cpt=0;
				int[] tab2 = new int[tab.length-2];
				for(int i=0; i<tab.length; i++)
				{
					if(i!=location&&i!=location2)
					{	
						tab2[cpt]=tab[i];
						cpt++;
					}
				}
				tab=tab2;
			}
			
		}while(found);
		
		return tab;
		
	}
	
	/**
	 * delete j from tab
	 * @param tab
	 * @param j
	 * @return new tab
	 */
	public static int[] deleteFromTab(int[] tab, int j)
	{
		//System.out.println("delete "+j);
		//ConstructeurClauseV5.affichetab(tab);
		
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
	
	public static VariableV2[] createVariable(int[] tab)
	{
		VariableV2[] v = new VariableV2[tab.length];
		for(int i=0; i<tab.length; i++)
		{
			v[i]=new VariableV2(tab[i]);
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
	
	public static void afficher(VariableV2[] v)
	{
		for(int i=0; i<v.length; i++)
		{
			System.out.println(v[i].variable + ": value:"+v[i].value+" decisionLevel:"+v[i].decisionLevel+  
					" cut:"+v[i].cut+" antecedants:"+v[i].antecedants + " w: "+v[i].w);
		}
	}
	
	public static void afficher(VariableV2 v)
	{
		System.out.println(v.variable + ": value:"+v.value+" decisionLevel:"+v.decisionLevel+  
				" cut:"+v.cut+" antecedants:"+v.antecedants+" w: "+v.w);
	}
}

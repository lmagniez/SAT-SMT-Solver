package cdcl.v2;

import java.util.Deque;
import java.util.Vector;

import cdcl.v1.Variable;
import dpll.v1.ConstructeurClause;

public class VariableV2 {

	public static int actualLevel=0;
	public static int actualDecision=0;//number of variable set
	
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
	
	public void addToGraph(int value, int decisionLevel, int cut, int w, VariableV2[] antecedants)
	{
		this.value=value;
		this.decisionLevel=decisionLevel;
		this.cut=cut;
		this.w=w;
		this.antecedants=antecedants;
	}
	
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
	
	//put to 0 every Variable up to the decision level
	public static int restartVariable(VariableV2[] v, int decisionLevel, Vector<VariableV2> liste)
	{
		while(!liste.isEmpty()&&liste.get(liste.size()-1).getDecisionLevel()!=decisionLevel)
			liste.remove(liste.get(liste.size()-1));
		
		System.out.println("decide" + decisionLevel);
		int cpt=0;
		for(int i=0; i<v.length; i++)
		{
			if((v[i].decisionLevel==decisionLevel&&v[i].cut==0))
			{	System.out.println("HELLO THERE "+v[i].getVariable());
			 	
			}
			
			if((v[i].decisionLevel>decisionLevel)&&v[i].decisionLevel!=-1)
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

	
	
	public static int[] generateConflictClause (int[][] cls, VariableV2[] v, Vector<VariableV2> liste)
	{
	
		
		///////////////////////////
		//GENERATION DE LA CLAUSE//
		///////////////////////////
		
		System.out.println("taille "+ liste.size());
		
		//commence � -1
		VariableV2 v0 = liste.get(liste.size()-1);
		liste.remove(liste.size()-1);
		int[] c0= cls[v0.w];
		
		//afficher(v0);
		
		//tant que le haut de pile n'est pas la variable pari�e
		while(v0.cut!=0)
		{
			//on d�pile
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
	public static void conflictAnalysis (int[] conflictClause, int size, VariableV2[] v, Vector<VariableV2> liste) 
	{
		System.out.println("clause");
		ConstructeurClauseV5.affichetab(conflictClause);
		
		int d=VariableV2.actualLevel;
		
		int k=-1;
		VariableV2 v0=null;
		
		//search for v0 (decisionLevel d, cut=0)		
		//search for k (the max before d)
		for(int i=0; i<conflictClause.length-1; i++)
		{
			
			VariableV2 tmp= VariableV2.find(Math.abs(conflictClause[i]), v);
			VariableV2.afficher(tmp);
			System.out.println("d="+d);
			if(tmp.decisionLevel==d)
				v0=tmp;
			if(tmp.decisionLevel<d)
			{
				if(k<tmp.decisionLevel)
					k=tmp.decisionLevel;
			}
		}
		
		
		
		//backtrack to k
		System.out.println("backtrack to "+k);
		VariableV2.actualLevel=k;
		restartVariable(v, k, ConstructeurClauseV5.decisions);
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
		
		System.out.println("conflict clause apr�s r�solution");
		if(cls.length!=1)ConstructeurClauseV5.affichetab(cls[1]);
		System.out.println(Variable.actualLevel);
		
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
	
	
	public static int[] deleteFromTab(int[] tab, int j)
	{
		System.out.println("delete "+j);
		ConstructeurClauseV5.affichetab(tab);
		
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
	
	public static VariableV2 getLast(VariableV2[] v)
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

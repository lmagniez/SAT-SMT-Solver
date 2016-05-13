package cdcl.v2;

import java.awt.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;

import cdcl.v1.Variable;


public class ConstructeurClauseV5 {
	
	private int[][] clauses;
	private int nbClause;
	private int nbVariable;
	private int[] listeVariable;
	private static Vector<VariableV2> decisions = new Vector<VariableV2>();
	
	
	public ConstructeurClauseV5(String nomFichier)
	{
		
		
		BufferedReader br = null;
		
		try {

			String sCurrentLine;
			int currentClause=0;

			br = new BufferedReader(new FileReader(nomFichier));

			while ((sCurrentLine = br.readLine()) != null) {
				
				//remove extra spaces
				sCurrentLine=sCurrentLine.trim();
				while(sCurrentLine.indexOf("  ") >= 0)
				{
					sCurrentLine= sCurrentLine.replaceAll("  ", " ");
				}
				
				String[] line = sCurrentLine.split(" ");
				
				//comment
				if("c".equals(line[0]))
				{
					
				}
				//initialisation
				else if("p".equals(line[0]))
				{
					nbVariable=Integer.parseInt(line[2]);
					nbClause=Integer.parseInt(line[3]);
					
					listeVariable=new int[nbVariable+1];
					for(int i=0; i<nbVariable; i++)
						listeVariable[i]=0;
					listeVariable[nbVariable]=Integer.MAX_VALUE;//conflict variable
					
					//the first line is the nb of variables
					clauses=new int[nbClause+1][];
					int[] nbV= {nbVariable};
					clauses[0]=nbV;
					currentClause++;
				}
				//clause
				else
				{
					clauses[currentClause]=new int[line.length];
					for(int i=0;i<line.length;i++)
					{
						int elt=Integer.parseInt(line[i]);
						elt=Math.abs(elt);
						
						//add into listeVariable if doesn't exist yet
						for(int j=0; j<nbVariable; j++)
						{
							//if find the equivalent
							if(listeVariable[j]==elt)
								break;
							
							//doesn't find equivalent, then add
							if(listeVariable[j]==0)
							{
								listeVariable[j]=elt;
								break;
							}
						}
						
						clauses[currentClause][i]=Integer.parseInt(line[i]);;
				
					}
					currentClause++;
				}
				
			}			

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		
		listeVariable=this.selectionSort(listeVariable);
		listeVariable[listeVariable.length-1]=-1;
		VariableV2[] listeV= VariableV2.createVariable(listeVariable);
		
		
		//affiche(clauses);
		System.out.println(clauses.length);
		
		//System.out.println("result: "+DPLL(clauses, partialI, 0));
		int[] res = CDCL(clauses, this.nbVariable, listeV);
		
		
		
		
	}
	
	public int[] selectionSort(int[] data)
	{
		  int lenD = data.length;
		  int j = 0;
		  int tmp = 0;
		  for(int i=0;i<lenD;i++){
		    j = i;
		    for(int k = i;k<lenD;k++){
		      if(data[j]>data[k]){
		        j = k;
		      }
		    }
		    tmp = data[i];
		    data[i] = data[j];
		    data[j] = tmp;
		  }
		  return data;
	}
	
	public static boolean unitResolution(int[][] cls, int[][] originalCls, VariableV2[] v)
	{
		
		int cut = 1;
		int[][] res=null;
		
		boolean found;
		
		
		//update cls with interpretation
		for(int i=0; i<v.length-1; i++)
		{
			if(v[i].getValue()!=-1)
			{
				int toPropagate=v[i].getVariable();
				if(v[i].getValue()==0)
					toPropagate*=-1;
				
			
				res=ConstructeurClauseV5.unitPropagation(toPropagate,cls);
				
				
				//conflict
				if(res.length==1&&res[0].length==1)
				{
					int cpt=0;
					int locationConflict = res[0][0];
					VariableV2[] antecedants= new VariableV2[originalCls[locationConflict].length-1]; 
					//create the antecedants for k
					for(int j=0; j<originalCls[locationConflict].length-1; j++)
					{	
						
						//System.out.println(originalCls[locationConflict].length-1);
						System.out.println("**");
						System.out.println(originalCls.length);
						System.out.println(cls.length);
						System.out.println(locationConflict);
						System.out.println(j);
						System.out.println(i);
						System.out.println(cpt);
						if(originalCls[locationConflict][j]!=cls[i][0])
						{
							antecedants[cpt]=VariableV2.find(originalCls[locationConflict][j], v);	
							cpt++;
						}
					}
					
					//ajoute k conflit
					v[v.length-1].addToGraph(1, VariableV2.actualLevel, cut, locationConflict, antecedants);
					decisions.add(v[v.length-1]);
					
					
					System.out.println("empty clause");
					return false; //if there is empty clause/conflict
				}
			}
		}
		
		//no conflict
		if(res!=null)
			cls=res;
		
		System.out.println("NEW AJOUT");
		VariableV2.afficheDecision(v);
		
		//search for unit clauses (clause with only one literal)	
		do
		{
			
			System.out.println("ok");
			found=false;
			
			Vector<Integer> unitList = new Vector<Integer>(); 
			
			
			System.out.println("search again");
			//look in all the clauses
			for(int i=0; i<cls.length; i++)
			{
				
				
				//if there is a unit clause
				if(cls[i].length==2)
				{
					
					int elt=cls[i][0];
					boolean ajout=true;
					
					for(int j=0;j<v.length;j++)
					{
						if(v[j].getVariable()==Math.abs(elt)&&v[j].getValue()!=-1)
						{
							ajout=false;
							break;
						}
					}
					
					//does not exist yet,
					//add to the graph (and to unitList)
					if(ajout)
					{
						System.out.println("ajout de elt: "+elt+" en position i: "+i );
						System.out.println("clause");
						for(int c=0;c<cls[i].length;c++)System.out.print(cls[i][c]+" ");
						System.out.println();
						System.out.println("original");
						for(int c=0;c<originalCls[i].length;c++)System.out.print(originalCls[i][c]+" ");
						System.out.println();
						
						
						int value;
						if(elt<0) value=0;					
						else value=1;
						
						
						VariableV2[] antecedants= new VariableV2[originalCls[i].length-2];
						
						//copy dst (from original clauses)
						int cpt=0;
						for(int j=0; j<originalCls[i].length-1; j++)
						{	
							
							
							//System.out.println(originalCls[i][j]+" "+src);
							if(originalCls[i][j]!=elt)
							{
								antecedants[cpt]=VariableV2.find(originalCls[i][j], v);	
								cpt++;
							}
						}
						
						//System.out.println(cls[i][0]);
						
						
						VariableV2 var = VariableV2.find(Math.abs(cls[i][0]), v);
						
						System.out.println("add to graph: "+i);
						var.addToGraph(value, VariableV2.actualLevel, cut, i, antecedants);
						decisions.add(var);
						
						
						if(!unitList.contains(cls[i][0]))
							unitList.add(cls[i][0]);
					}
					
				}
			}//for
			
			//from here, we have all of the unit clauses
			//unit propagation
			if(unitList.size()!=0)
			{
				System.out.println("unitList");
				for(int i=0; i<unitList.size(); i++)
					System.out.println(unitList.get(i));
				
				//pour chaque clause unitaire
				for(int i=0; i<unitList.size(); i++)
				{
					//faire unit propagation
					int elt=unitList.get(i);
					cls=ConstructeurClauseV5.unitPropagation(elt,cls);
					
					
					////////////
					//CONFLICT//
					////////////
					//si conflit
					if(cls.length==1&&cls[0].length==1)
					{
						System.out.println("conflit");
						int cpt=0;
						int locationConflict = cls[0][0];
						//System.out.println("location conflit "+cls[0][0] + " " + originalCls[locationConflict].length);
						
						VariableV2[] antecedants= new VariableV2[originalCls[locationConflict].length-1]; 
						for(int j=0; j<originalCls[locationConflict].length-1; j++)
						{	
							if(originalCls[locationConflict][j]!=-elt)
							{
								antecedants[cpt]=VariableV2.find(originalCls[locationConflict][j], v);	
								cpt++;
							}
						}
						
						//ajoute k conflit
						v[v.length-1].addToGraph(1, VariableV2.actualLevel, cut, locationConflict, antecedants);
						decisions.add(v[v.length-1]);
						return false; //if there is empty clause/conflict
					}
					///////////////
					
					
				}
				found=true;
				
				cut++;//increment each time find cls
			}
			
			
			VariableV2.afficheDecision(v);
			System.out.println("found:"+found);
			
		}while(found);
		
		return true;
		
		
	}
	
	
	
	//delete the entire clauses containing literal (replace by 0) 
	//delete -literal from all clauses 
	//may cause empty clause when delete -literal from clause !(return null)
	
	//DON'T FORGET TO DELETE THE LITERAL AFTER THE PROPAGATION (DONE)
	
	public static int[][] unitPropagation(int literal, int[][] cls)
	{	
		//System.out.println("\nunitPropagation: literal: "+literal);
		
		int[][] clausesTMP=cls;
		
		//remove one variable
		clausesTMP[0][0]=clausesTMP[0][0]-1;
		
		for(int i=0; i<clausesTMP.length; i++)
		{
			boolean found=false; //true if find literal
			int[] newLine = new int[clausesTMP[i].length-1];
			int cptLine=0;//not j, because we may delete literal in the clause
			for(int j=0; j<clausesTMP[i].length-1; j++)
			{
				
				if(clausesTMP[i][j]==literal)
				{
					newLine = new int[1];
					cptLine=0;
					found=true;
					break;
				}
				
				//possible to get empty clause (return null)
				//else skip the value since it has to be removed from the clause
				else if(clausesTMP[i][j]==-literal)
				{
					
					//CONFLICT
					if(clausesTMP[i].length==2){
						System.out.println("clause nulle!");
						int[][] conflict= new int[1][1];
						conflict[0][0]=i;
						return conflict;
					}
					found=true;
				}
				
				//add to the new line
				else
				{
					newLine[cptLine]=clausesTMP[i][j];
					cptLine++;
				}
				
			}
			//change the clause after being treated
			if(found)
			{	
				newLine[cptLine]=0;
				clausesTMP[i]=newLine;
			}
			
		}
		
		return clausesTMP;
		
	}
	
	
	
	
	
	
	//return the choosed Variable
	public VariableV2 nextBranchingVariable(VariableV2[] listeVariable)
	{
		
		
		//for each variable, test if it's already used by interpretation
		//Search for a potential l
		for(int i=0; i<listeVariable.length; i++)
		{
			
			if(listeVariable[i].getValue()==-1)
			{
				//VariableV2.actualLevel++;
				//listeVariable[i].addToGraph(1, VariableV2.actualLevel, 0, -1, new VariableV2[0]);
				return listeVariable[i];
			}
				
		}
		return null;
		
	}
	
	
	
	
	public int[] CDCL(int[][] cls, int nbVariable, VariableV2[] listeVariable)
	{

		int test=0;
		boolean result;
		int[][] learnedCls={};
		int[][] cpyCls=fusion(cls,learnedCls);
		int[][] originalCls= cpyCls.clone();
		VariableV2 next=null;
		boolean pick=true;
		
		//INIT PROPAGATION
		//First test (check if no primal conflict)
		result=unitResolution(cpyCls,originalCls,listeVariable);
		if(result==false)
			return null;
		
		
		while(nextBranchingVariable(listeVariable)!=null)
		{
			System.out.println("conflits appris");
			affiche(learnedCls);
			
			if(pick)
			{
				VariableV2.actualLevel++;
				next=nextBranchingVariable(listeVariable);
				next.addToGraph(1, VariableV2.actualLevel, 0, -1, new VariableV2[0]);
				decisions.add(next);
				System.out.println("add: "+next.getVariable());
			}
			
			
			//prepare the unit resolution
			cpyCls=fusion(cls,learnedCls);
			originalCls=cpyCls.clone();
			result=unitResolution(cpyCls,originalCls,listeVariable);
			
			//if(result==false&&pick==false)
				//break;
			
			//en cas de conflit
			if(result==false)
			{
				//genere la clause de conflit
				int[] conflict= VariableV2.generateConflictClause(originalCls, listeVariable, decisions);
				learnedCls=add(learnedCls,conflict);
				int size= learnedCls.length+cls.length;
				VariableV2.conflictAnalysis(conflict, size, listeVariable,decisions);
				pick=false;
			}
			else pick=true;
			
			
		}
		
		return null;
		
		
	}
		
    ////////////////////////////////
	////////////////////////////////
	////////////////////////////////
	
	
	
	public boolean contains(int[] tab, int elt)
	{
		for(int i=0; i<tab.length; i++)
		{
			if(tab[i]==elt)
				return true;
			
		}
		return false;
	}
	
	public static boolean isEmpty(int[][] tab)
	{
		for(int i=1; i<tab.length;i++)
		{
			if(tab[i][0]!=0)
				return false;
		}
		return true;
	}
	
	public static int[] fusion(int[] tab1, int[] tab2)
	{
		int newSize=tab1.length+tab2.length;
		int[] newTab = new int[newSize];
		for(int i=0; i<tab1.length; i++)
		{
			newTab[i]=tab1[i];
		}
		
		for(int i=0; i<tab2.length; i++)
		{
			newTab[tab1.length+i]=tab2[i];
		}
		return newTab;
		
	}
	
	//fusion between clauses and literal
	public static int[][] fusion2(int[][] tab1, int literal)
		{
			int newSize=tab1.length+1;
			int[][] newTab = new int[newSize][];
			for(int i=0; i<tab1.length; i++)
			{
				newTab[i]=new int[tab1[i].length];
				for(int j=0; j<tab1[i].length;j++)
				{
					newTab[i][j]=tab1[i][j];
			
				}
			}
			
			newTab[tab1.length]=new int[2];
			for(int i=0; i<2; i++)
			{
				newTab[tab1.length][0]=literal;
				newTab[tab1.length][1]=0;
			}
			return newTab;
			
		}
	
	
	
	public int[][] fusion(int[][] tab1, int[][] tab2)
	{
		int[][] newTab= new int[tab1.length+tab2.length][];
		
		for(int i=0; i<tab1.length; i++)
			newTab[i]=tab1[i];
		if(tab2!=null)
			for(int i=0; i<tab2.length; i++)
				newTab[tab1.length+i]=tab2[i];
		return newTab;
	}
	
	
	
	
	
	public static void affichetab(int[] t)
	{
		for(int i=0; i<t.length; i++)
			System.out.print(t[i]+" ");
		System.out.println();
		System.out.println();
	}
	
	public static int[] add(int[]tab, int elt)
	{
		int[] newTab = new int[tab.length+1];
		for(int i=0; i<tab.length; i++)
		{
			newTab[i]=tab[i];
		}
		newTab[tab.length]=elt;
		return newTab;
	}
	
	public static int[][] add(int[][] tab, int[] elt)
	{
		int[][] newTab = new int[tab.length+1][];
		for(int i=0; i<tab.length; i++)
		{
			newTab[i]=tab[i];
		}
		newTab[tab.length]=elt;
		return newTab;
	}

	
	
	public void affiche()
	{
		for(int i=0;i<nbClause;i++)
		{
			for(int j=0; j<clauses[i].length; j++)
				System.out.print(clauses[i][j]+" ");
			System.out.println("");
		}
	}
	
	public static void affiche(int[][] cls)
	{
		if(cls==null)
		{
			System.out.println("clause nulle");
			return;
		}
		
		for(int i=0;i<cls.length;i++)
		{
			for(int j=0; j<cls[i].length; j++)
				System.out.print(cls[i][j]+" ");
			System.out.println("");
		}
	}
	
	public static void main(String[] args) {
		///////////////////
		//SATISFIABLE//////
		///////////////////
		
		//ConstructeurClauseV5 c = new ConstructeurClauseV5("uf20-02.cnf");
		//ConstructeurClauseV5 c = new ConstructeurClauseV5("uf50-01.cnf");
		//ConstructeurClauseV5 c = new ConstructeurClauseV5("queen.cnf");
		//ConstructeurClauseV5 c = new ConstructeurClauseV5("flat150-1.cnf");
		
		///////////////////
		//UNSATISFIABLE////
		///////////////////

		//ConstructeurClauseV5 c = new ConstructeurClauseV5("aim-50-1_6-no-2.cnf");
		//ConstructeurClauseV5 c = new ConstructeurClauseV5("exemple2.cnf");
		//ConstructeurClauseV5 c = new ConstructeurClauseV5("dubois20.cnf");
		
	}
	
	
}

package cdcl.v1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;


public class ConstructeurClauseV4 {
	
	private int[][] clauses;
	private int nbClause;
	private int nbVariable;
	private int[] listeVariable;
	private Vector<int[]> solutions = new Vector<int[]>();
	
	
	public ConstructeurClauseV4(String nomFichier)
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
					listeVariable[nbVariable]=-1;//conflict variable
					
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
		
		Variable[] listeV= Variable.createVariable(listeVariable);
		
		
		//affiche(clauses);
		System.out.println(clauses.length);
		
		//System.out.println("result: "+DPLL(clauses, partialI, 0));
		int[] res = CDCL(clauses, this.nbVariable, listeV);
		
		System.out.println("\n\n\nRESULTAT");
		for(int i=0; i<res.length; i++)
			System.out.println(res[i]);
		System.out.println("///");
		
		
		
	}
	
	
	public static boolean unitResolution(int[][] cls, int[][] originalCls, Variable[] v)
	{
		
		int cut = 1;
		int[][] res=null;
		
		boolean found;
		
		
		//update cls with interpretation
		for(int i=0; i<v.length; i++)
		{
			if(v[i].getValue()!=-1)
			{
				int toPropagate=v[i].getVariable();
				if(v[i].getValue()==0)
					toPropagate*=-1;
				
			
				
				//affichetab(interpretation);
				res=ConstructeurClauseV4.unitPropagation(toPropagate,cls);
				
				//conflict
				if(res.length==1&&res[0].length==1)
				{
					int cpt=0;
					int locationConflict = res[0][0];
					Variable[] antecedants= new Variable[originalCls[locationConflict].length-1]; 
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
							antecedants[cpt]=Variable.find(originalCls[locationConflict][j], v);	
							cpt++;
						}
					}
					
					//ajoute k conflit
					v[v.length-1].addToGraph(1, Variable.actualLevel, cut, locationConflict, antecedants);
					
					System.out.println("empty clause");
					return false; //if there is empty clause/conflict
				}
			}
		}
		
		//no conflict
		if(res!=null)
			cls=res;
		
		System.out.println("NEW AJOUT");
		Variable.afficheDecision(v);
		
		//search for unit clauses (clause with only one literal)	
		do
		{
			found=false;
			
			Vector<Integer> unitList = new Vector<Integer>(); 
			
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
						if(v[j].getVariable()==Math.abs(elt))
						{
							ajout=false;
							break;
						}
					}
					
					//does not exist yet,
					//add to the graph (and to unitList)
					if(ajout)
					{
						
						int value;
						if(elt<0) value=0;					
						else value=1;
						
						
						Variable[] antecedants= new Variable[originalCls[i].length-2];
						
						//copy dst (from original clauses)
						int cpt=0;
						for(int j=0; j<originalCls[i].length-1; j++)
						{	
							
							
							//System.out.println(originalCls[i][j]+" "+src);
							if(originalCls[i][j]!=elt)
							{
								antecedants[cpt]=Variable.find(originalCls[i][j], v);	
								cpt++;
							}
						}
						
						//System.out.println(cls[i][0]);
						
						
						Variable var = Variable.find(Math.abs(cls[i][0]), v);
						
						
						var.addToGraph(value, Variable.actualLevel, cut, i, antecedants);
						
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
					cls=ConstructeurClauseV4.unitPropagation(elt,cls);
					
					System.out.println("ok");
					
					Variable.afficheDecision(v);
					////////////
					//CONFLICT//
					////////////
					//si conflit
					if(cls.length==1&&cls[0].length==1)
					{
						int cpt=0;
						int locationConflict = cls[0][0];
						System.out.println("location conflit "+cls[0][0] + " " + originalCls[locationConflict].length);
						
						Variable[] antecedants= new Variable[originalCls[locationConflict].length-1]; 
						for(int j=0; j<originalCls[locationConflict].length-1; j++)
						{	
							System.out.println(j + " " + originalCls[locationConflict][j] + " "+elt);
							if(originalCls[locationConflict][j]!=-elt)
							{
								System.out.println(cpt);
								System.out.println("testo " + originalCls[locationConflict][j]);
								antecedants[cpt]=Variable.find(originalCls[locationConflict][j], v);	
								cpt++;
							}
						}
						
						//ajoute k conflit
						v[v.length-1].addToGraph(1, Variable.actualLevel, cut, locationConflict, antecedants);
						System.out.println("empty clause 2");
						return false; //if there is empty clause/conflict
					}
					///////////////
					
					
				}
				found=true;
				
				cut++;//increment each time find cls
			}
			
			
			Variable.afficheDecision(v);
			
			
			break;
			
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
		
		//System.out.println("cls après traitement");	
		//affiche(cls);
		//System.out.println("*********");
		
		return clausesTMP;
		
	}
	
	
	
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
	
	
	//true -> satisfiable
	//false -> unsatisfiable
	
	//partialInterpretation -> the variable we choosed (T/F) Empty at first
	
	//return the choosed 
	public int pickBranchingVariable(Variable[] listeVariable)
	{
		
		
		//for each variable, test if it's already used by interpretation
		//Search for a potential l
		for(int i=0; i<listeVariable.length; i++)
		{
			
			if(listeVariable[i].getValue()==-1)
			{
				Variable.actualLevel++;
				listeVariable[i].addToGraph(1, Variable.actualLevel, 0, -1, new Variable[0]);
				System.out.println("ladecision"+Variable.actualLevel);
				return i;
			}
				
		}
		return -1;
		
	}
	
	
	
	
	public int[] CDCL(int[][] cls, int nbVariable, Variable[] listeVariable)
	{
		
		
		boolean result;
		int[][] learnedCls={};
		int[][] cpyCls=fusion(cls,learnedCls);
		int[][] originalCls= cpyCls.clone();
		
		result=unitResolution(cpyCls,originalCls,listeVariable);
		if(result==false)
			return null;
		
		boolean pick=true;
		int location;
		
		int cpt=0;
		while(Variable.actualDecision!=listeVariable.length-1&&cpt<5)
		
		//while(Variable.actualDecision!=listeVariable.length-1)
		{
			
			
			cpt++;
			
			System.out.println("********************************************************");
			
			
			
			if(pick)
			{
				Variable.afficher(listeVariable);
				location = pickBranchingVariable(listeVariable);
				System.out.println("pick a branch: "+listeVariable[location].getVariable());
			}
			else
			{
				//System.out.println("branch: "+interpretation[interpretation.length-1]);
			}
			
			//Variable.afficher(listeVariable);
			
			
			cpyCls=fusion(cls,learnedCls);
			originalCls=cpyCls.clone();
			result=unitResolution(cpyCls,originalCls,listeVariable);
			
			affiche(learnedCls);
			
			if(result==false)
			{
				int[] clause = Variable.createConflictClause(originalCls, listeVariable);
				
				System.out.println("tabClause:");
				affichetab(clause);
				System.out.println("interpretation:");
				Variable.afficheDecision(listeVariable);
				
				learnedCls=add(learnedCls,clause);
				
				//got the clause, got to backtrack now
				//choose the mint level from the clause (to backtrack)
				int maxLevel=Integer.MIN_VALUE;
				Variable vRes=null;
				for(int i=0; i<clause.length-1; i++)
				{
					System.out.println(i);
					Variable v = Variable.find(Math.abs(clause[i]),listeVariable);
					if(v.getLevel()>maxLevel&&v.getCut()==0&&v.getValue()==1)
					{
						maxLevel=v.getLevel();
						vRes=v;
					}
					Variable.actualLevel=maxLevel;
					
				}
				
				//change interpretation
				System.out.println("restart variable"+Variable.actualLevel);
				//Variable.afficher(listeVariable);
				int nbRemoved=Variable.restartVariable(listeVariable, Variable.actualLevel);
				System.out.println("restart variable");
				Variable.afficher(listeVariable);
				
				
				System.out.println("nombre suppr!!"+nbRemoved);
				Variable.MAJActualDecision(listeVariable);
				System.out.println(Variable.actualDecision);
				Variable.afficheDecision(listeVariable);
				
				
				
				
				
				
				
				//flip the atom
				System.out.println("flip the atom");
				if(vRes.getValue()==-1) return null;
				if(vRes.getValue()==0)vRes.setValue(1);
				if(vRes.getValue()==1)vRes.setValue(0);
				System.out.println(vRes.getVariable()+ " value: "+vRes.getValue());
				//interpretation[interpretation.length-1]=interpretation[interpretation.length-1]*-1;
				pick=false;
				
				
				
				System.out.println("new interpretation");
				Variable.afficheDecision(listeVariable);
				
				
				
			}
			else
				pick=true;
			
			Variable.MAJActualDecision(listeVariable);
		}
		
		System.out.println("FIN!!");
		Variable.afficheDecision(listeVariable);
		return null;
		
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
	
	
	//check if every clauses has a size of 2 or less ("literal" "0")
	//also check that there are no literal and -literal in the expression
	public boolean isSatisfiable(int[][] cls)
	{
		for(int i=0; i<cls.length;i++)
		{
			if(cls[i].length>2)
			{
				return false;
			}
			
		}
		
		//check if no litteral and -litteral
		//from here, we're sure there is at most 1 literal in the clauses
		for(int i=0; i<cls.length;i++)
		{
			for(int j=i;j<cls[i].length;j++)
			{
				if(cls[i][0]==-cls[j][0])
					return false;
			}
		}
		
		return true;
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
		//ConstructeurClauseV4 c = new ConstructeurClauseV4("exemple6.cnf");
		ConstructeurClauseV4 c = new ConstructeurClauseV4("queen.cnf");
		
			
		
	}
	
	
}

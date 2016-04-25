package dpll.v3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;


public class ConstructeurClauseV3 {
	
	private int[][] clauses;
	private int nbClause;
	private int nbVariable;
	private int[] listeVariable;
	private Vector<int[]> solutions = new Vector<int[]>();
	
	
	public ConstructeurClauseV3(String nomFichier)
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
					
					listeVariable=new int[nbVariable];
					for(int i=0; i<nbVariable; i++)
						listeVariable[i]=0;
					
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
						
						//ajout dans listeVariable si existe pas
						for(int j=0; j<nbVariable; j++)
						{
							//si trouve équivalent
							if(listeVariable[j]==elt)
								break;
							
							//si arrive au bout, ajoute
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
		
		//affiche(clauses);
		System.out.println(clauses.length);
		
		//System.out.println("result: "+DPLL(clauses, partialI, 0));
		int[] res = DPLL(clauses, this.nbVariable, this.listeVariable);
		
		System.out.println("\n\n\nRESULTAT");
		for(int i=0; i<res.length; i++)
			System.out.println(res[i]);
		System.out.println("///");
		
	}
	
	
	public static int[] unitResolution(int[][] cls, int[][] originalCls, Graph g, int[] interpretation)
	{
		
		int cut = 1;
		
		
		boolean found;
		//search for unit clauses (clause with only one literal)
		
		for(int i=0; i<interpretation.length; i++)
		{
			//affichetab(interpretation);
			if(interpretation[i]!=0)
				cls=ConstructeurClauseV3.unitPropagation(interpretation[i],cls);
			if(cls==null)return null;
		}
		
			
		do
		{
			found=false;
			//look in all the clauses
			for(int i=0; i<cls.length; i++)
			{
				
				//if there is a unit clause
				if(cls[i].length==2)
				{
					//System.out.println("THERE IS A UNIT CLAUSE");
					
					//test if already exist
					int src=cls[i][0];
					
					boolean ajout=true;
					
					for(int j=0;j<interpretation.length;j++)
					{
						if(interpretation[j]==src)
						{
							ajout=false;
							break;
						}
					}
					
					
					//add to the graph 
					if(ajout)
					{
						int actualLvl=g.getLevel();
						int[] dst= new int[originalCls[i].length-2];
						
						//copy dst 
						int cpt=0;
						for(int j=0; j<originalCls[i].length-1; j++)
						{	
							//System.out.println(originalCls[i][j]+" "+src);
							if(originalCls[i][j]!=src)
							{
								dst[cpt]=originalCls[i][j];	
								cpt++;
							}	
						}
						
						Node n=new Node(actualLvl, src, cut, dst);
						cut++;//increment each time find cls
						
						
						g.addNode(n);
						
						
						
					}
					
					//unitPropagation	
					//affichetab(interpretation);
					//System.out.println("UNIT PROPAGATION OF: "+cls[i][0]);
					int elt = cls[i][0];
					cls=ConstructeurClauseV3.unitPropagation(cls[i][0],cls);
					if(cls==null) return null; //if there is empty clause/conflict
					
					//add the new interpretation
					System.out.println("ajout nouvelles interpretations");
					int[] newInterpretation= new int[interpretation.length+1];
					for(int j=0; j<interpretation.length; j++)
						newInterpretation[j]=interpretation[j];
					newInterpretation[interpretation.length]=elt;
					interpretation=newInterpretation;
					
					affichetab(interpretation);
					
					found=true;
					break;
					
				}
			}
			
		}while(found);
		
		
		
		return interpretation;
		
	}
	
	
	
	//delete the entire clauses containing literal (replace by 0) 
	//delete -literal from all clauses 
	//may cause empty clause when delete -literal from clause !(return null)
	
	//DON'T FORGET TO DELETE THE LITERAL AFTER THE PROPAGATION (DONE)
	
	public static int[][] unitPropagation(int literal, int[][] cls)
	{	
		System.out.println("\nunitPropagation: literal: "+literal);
		
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
					if(clausesTMP[i].length==2){
						System.out.println("clause nulle!");
						return null;
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
		
		System.out.println("cls après traitement");	
		//affiche(cls);
		System.out.println("*********");
		
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
	
	
	//true -> satisfiable
	//false -> unsatisfiable
	
	//partialInterpretation -> the variable we choosed (T/F) Empty at first
	
	public int[] DPLL(int[][] cls, int nbVariable, int[] listeVariable)
	{
		Graph g = new Graph();
		int[] interpretation={};
		int[][] learnedCls;
		
		
		int cpt2=0;
		while(true)
		{
			System.out.println("ITERATION: "+cpt2);
			cpt2++;
			
			//if(cls[0][0]<-50)return null;
			System.out.println("DEBUT DE WHILE");
			System.out.println("interpretation");
			affichetab(interpretation);
			
			
			int[] conflictDrivenClause = null;
			int[][] cpyCls=cls.clone();
			
			System.out.println(cpyCls+" "+cls+" "+g+" "+interpretation);
			int[] result = unitResolution(cpyCls, cls, g, interpretation);//recupere interpretation
			
			if(result!=null) interpretation=result;
			
			
			//affiche(cls);
			
			//conflicts
			if(result==null)
			{
				System.out.println("******************");
				System.out.println("IL Y A CONFLIT");
				System.out.println("******************");
				
				affichetab(interpretation);
				g.affiche();
				
				//contradiction without any decision 
				if(interpretation.length==0)
					return null;
				//backtrack to asserting level
				else
				{
					int conflictLevel=g.getNode(g.getNode().length-1).getLevel();
					
					int[][] clauses;
					int assertingLevel = -1;
					
					//determine assertingClause, negation, then add it to the learned clause
					
					//get the nb of clauses
					int i=g.getNode().length-1;
					while(g.getNode(i).getCut()!=0)	
						i--;
					
					int nbClauses= g.getNode().length-i;
					clauses=new int[nbClauses][];
					
					
					//get the clauses
					int cpt=0;
					for(int j=i; j<g.getNode().length;j++)
					{
						int[] clause = g.getNode(j).getDst();
						clauses[cpt]=clause;
						cpt++;
					}	
					
					System.out.println("Conflict sets!");
					affiche(clauses);
					
					
					//from here, we have the clauses (conflict set)
					//conflict level
					//must find first clause with only 1 level equals to conflict level
					
					int indiceClause=0;
					//for each clauses
					for(int j=0; j<clauses.length; j++)
					{
						//count the number of literal with same level than conflict level
						int nbLvl=0;
						for(int k=0; k<clauses[j].length; k++)
						{
							if(g.getLevelOf(clauses[j][k])==conflictLevel||g.getLevelOf(-clauses[j][k])==conflictLevel)
								nbLvl++;
						}
						
						//if only one literal has the same level, search for a conflictDrivenClause
						if(nbLvl==1)
						{
							indiceClause=j;
							conflictDrivenClause=clauses[indiceClause];
							int max=Integer.MIN_VALUE;

							
							//test loop
							//search for the second highest level in the clause (determine assertingLevel)
							for(int k=0; k<conflictDrivenClause.length; k++)
							{
								//test max
								int lvl=g.getLevelOf(conflictDrivenClause[k]);
								if(lvl==-1)
									lvl=g.getLevelOf(-conflictDrivenClause[k]);
								if(lvl>max&&lvl<conflictLevel)
								{
									assertingLevel=lvl;
									max=lvl;
									System.out.println("max: "+max+"lvl: "+lvl+"conflictLvl: "+conflictLevel);
								}
							}
							
							
							//modification of graph level
							if(assertingLevel!=-1)
							{	
								g.setLevel(assertingLevel);
								break;
							}
							else
								g.setLevel(1);
							
							
						}
					}
					
				
					//negate the clause to get the conflictDrivenClause
					for(int j=0; j<conflictDrivenClause.length; j++)
						conflictDrivenClause[j]=conflictDrivenClause[j]*-1;
					
					//add a 0 at the end of the clause (has to correspond with cnf format
					conflictDrivenClause=add(conflictDrivenClause,0);
					
					//add the clause
					cls=add(cls,conflictDrivenClause);
					
					//erase node after asserting level (reduce tab)
					int cursor=g.removeNode(assertingLevel);
					
					//get where to cut interpretation
					int crs=0;
					for(int j=0; j<interpretation.length; j++)
					{
						if(interpretation[j]==cursor){
							crs=j;
							break;
						}
					}
					
					//flip the p value
					interpretation[crs]=-interpretation[crs]; 
					
					//erase interpretation after asserting level (reduce the array
					int[] newInterpretation= new int[crs+1];
					
					for(int j=0; j<=crs; j++)
						newInterpretation[j]=interpretation[j];
					
					interpretation=newInterpretation;
					
					
					System.out.println("**************************");
					System.out.println("Après effacement des interpretations, lvl: "+assertingLevel);
					affichetab(interpretation);
					System.out.println("***************************");
					g.affiche();
				
					
				}	
			}
			
			////////////////////
			//  No conflicts ///
			////////////////////
			else
			{
				boolean found=false;
				int l = 0;
				
				//for each variable, test if it's already used by interpretation
				//Search for a potential l
				for(int i=0; i<listeVariable.length; i++)
				{
					boolean found2=false;
					for(int j=0; j<g.getNode().length; j++)
					{
						if(Math.abs(listeVariable[i])==Math.abs(g.getNode(j).getSrc()))
						{
							found2=true;
						}
					}
					
					if(!found2)
					{
						System.out.println("Trouvé un l: "+listeVariable[i]);
						found=true;
						l=listeVariable[i];
						break;
					}
						
				}
				
				//found an l
				if(found)
				{	
					g.setLevel(g.getLevel()+1);
					
					//add l to the interpretation
					interpretation=add(interpretation,l);
					
					System.out.println("Nouvelle interpretation:");
					affichetab(interpretation);
					
					int[] tab = {l};
					Node n=new Node(g.getLevel(), l, 0,tab);
					g.addNode(n);
					
				}
				else
				{
					g.affiche();
					affiche(cls);
					return interpretation;
				}
			}
			
			
			
			
			
		}
		
		
		
			
		 
		
		
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
	
	//create an array with only 0 in it
	public int[] createPartialInterpretation(int[][] cls)
	{
		int nbVar=cls[0][0];
		int[] partialI=new int[nbVar];
		
		for(int i=0;i<nbVar; i++)
		{
			partialI[i]=0;
		}
		
		return partialI;
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
		ConstructeurClauseV3 c = new ConstructeurClauseV3("queen.cnf");
		
			
		
	}
	
	
}

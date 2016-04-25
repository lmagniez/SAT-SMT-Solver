package dpll.v1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;


public class ConstructeurClause {
	
	private int[][] clauses;
	private int nbClause;
	private int nbVariable;
	private Vector<int[]> solutions = new Vector<int[]>();
	
	
	public ConstructeurClause(String nomFichier)
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
						clauses[currentClause][i]=Integer.parseInt(line[i]);
				
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
		
		int[] partialI = createPartialInterpretation(clauses);
		//System.out.println("result: "+DPLL(clauses, partialI, 0));
		DPLL(clauses, partialI, 0);
		
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
		affiche(cls);
		System.out.println("*********\n\n");
		
		return clausesTMP;
		
	}
	
	//ACCORDING TO WEBSITE, PURE LITERAL IS TOO EXPENSIVE ! 
	
	//CHECK IF REMOVING PURE LITERAL MAY CAUSE EMPTY CLAUSE
	
	//if find any literal only used in one polarity, we may remove it from the clause
	public int[][] pureLiteral(int[][] cls)
	{
		int[] literalList= new int[cls[0][0]];//get the nb of variable
		int[] toDelete = new int[cls[0][0]];
		
		int cptLiteral=0;
		int cptDelete=0;
		
		//get the literal list
		for(int i=0; i<cls.length; i++)
		{
			for(int j=0; j<cls[i].length; j++)
			{
				//l'element inverse est deja dedans, on va devoir le supprimer
				if(contains(literalList,-cls[i][j]))
				{
					toDelete[cptDelete]=-cls[i][j];
					cptDelete++;
				}
				//pas dedans, on ajoute
				else if (!contains(literalList,cls[i][j]))
				{
					literalList[cptLiteral]=cls[i][j];
				}
					
			}
		}
		
		//generate the good list
		for(int i=0; i<toDelete.length; i++)
		{
			for(int j=0; j<literalList.length; j++)
			{
				if(toDelete[i]==literalList[j])
					literalList[j]=0;
			}
		}
		
		//remove each literal from the list
		int[][] newClauses= cls;
		//for each k in the literal list
		for(int k=0; k<literalList.length;k++)
		{	
			
			
			for(int i=0; i<newClauses.length; i++)
			{	
				boolean found=false;//true if delete one
				int[] newClause = new int[newClauses[i].length-1];
				
				for(int j=0; j<newClauses[i].length; j++)	
				{
					if(k==newClauses[i][j])
					{
						if(newClause.length==2)
						{
							System.out.println("clause nulle! ");//not sure about this
							return null;
						}
						found=true;
					}
					else
					{
						newClause[0]=newClauses[i][j];
					}
						
				}
				//changement, on met a jour la clause
				if(found)
				{
					newClauses[i]=newClause;
				}
				
			}
		}
		
		return cls;
		
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
	
	
	
	//true -> satisfiable
	//false -> unsatisfiable
	
	//partialInterpretation -> the variable we choosed (T/F) Empty at first
	
	public boolean DPLL(int[][] cls, int[] partialInterpretation, int nbInterpretation)
	{
		System.out.println();
		System.out.println("nbInterpretation: "+nbInterpretation+" partialInterpretation.length: "+partialInterpretation.length);
		for(int i=0; i<partialInterpretation.length;i++)
			System.out.print(partialInterpretation[i]+" ");
		System.out.println();
		
		//propagate the new literal 
		if(nbInterpretation>0)
		{
			System.out.println("The new literal is: "+partialInterpretation[nbInterpretation-1]);
			
			cls=ConstructeurClause.unitPropagation(partialInterpretation[nbInterpretation-1],cls);
			System.out.println();
		}
			
		boolean found;
		//search for unit clauses (clause with only one literal)
		do
		{
			found=false;
			//look in all the clauses
			for(int i=0; i<cls.length; i++)
			{
				
				//if there is a unit clause
				if(cls[i].length==2)
				{
					//add to the interpretation
					partialInterpretation[nbInterpretation]=cls[i][0];
					nbInterpretation++;
					
					//unitPropagation	
					cls=ConstructeurClause.unitPropagation(cls[i][0],cls);
					if(cls==null) return false; //if there is empty clause/conflict
					found=true;
					break;
				}
			}
			
		}while(found);
		
		
		//CHECKER SI ON A UNE EXPRESSION STABLE
		int p=0;
		if(isSatisfiable(cls))
		{
			System.out.println("Just found a satisfiable solution!");
			
			for(int i=0; i<partialInterpretation.length;i++)
			{
				System.out.print(partialInterpretation[i]+" ");
				
			}
			solutions.add(partialInterpretation.clone());
			
			return true;
		
		}
		
		//SINON CHOISIR UN P ALEATOIRE, TESTER VRAI, SINON FAUX (LE TESTER TOUT AU DEBUT)
		else
		{
			int cpt=0;
			while(cls[cpt].length==1)
				cpt++;
			p=cls[cpt][0];
			
			partialInterpretation[nbInterpretation]=p;
			nbInterpretation++;
			
		}
		
		System.out.println("\n\n***********************************************");
		System.out.println("We assigned p to true (p="+p+")");
		System.out.println("***********************************************");
		
		int[][] clscpy=cls.clone();
		
		DPLL(cls, partialInterpretation, nbInterpretation);
		/*if(DPLL(cls, partialInterpretation, nbInterpretation))
			return true;
		else{*/
			System.out.println("\n\n***********************************************");
			System.out.println("Changing p to -p with -p="+-p);
			System.out.println("***********************************************");
			partialInterpretation[nbInterpretation-1]=-p;
			for(int i=nbInterpretation; i<partialInterpretation.length; i++)
				partialInterpretation[i]=0;
			return DPLL(clscpy,partialInterpretation,nbInterpretation);
		//}
		 
		
		
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


	public Vector<int[]> getSolutions() {
		return solutions;
	}


	public void setSolutions(Vector<int[]> solutions) {
		this.solutions = solutions;
	}
	
	
	
	
	
}

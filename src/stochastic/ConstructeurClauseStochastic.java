package stochastic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;


public class ConstructeurClauseStochastic {
	
	private int[][] clauses;
	private int nbClause;
	private int nbVariable;
	private int[] listeVariable;
	private Vector<int[]> solutions = new Vector<int[]>();
	
	
	public ConstructeurClauseStochastic(String nomFichier)
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
		int[] res = stochastic(clauses, this.nbVariable, this.listeVariable);
		
		System.out.println("\n\n\nRESULT");
		for(int i=0; i<res.length; i++)
			System.out.print(res[i]+" ");
		System.out.println("///");
		
	}
	
	//Perform the entire unitResolution
	//Return null if there is a conflict, else return the full interpretation
	//in Stochastic algorithm, no need to add interpretation since we use only full interpretation.
	
	public static int[] unitResolution(int[][] cls, int[] interpretation)
	{
		//search for unit clauses (clause with only one literal)
		
		for(int i=0; i<interpretation.length; i++)
		{
			//affichetab(interpretation);
			if(interpretation[i]!=0)
				cls=ConstructeurClauseStochastic.unitPropagation(interpretation[i],cls);
			if(cls==null)return null;
		}
	
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
						System.out.println("empty clause!");
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
		
		//System.out.println("cls après traitement");	
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
	
	public int[] stochastic(int[][] cls, int nbVariable, int[] listeVariable)
	{
		
		final int MAX_FLIP=1000000;
		final int MAX_INTERPRETATION=10000;
		
		int cptFlip=0, cptInterpretation=0;
		
		while(cptInterpretation<MAX_INTERPRETATION)
		{
			cptInterpretation++;
			int[] interpretation = createRandomInterpretation(listeVariable);
			
			while(cptFlip<MAX_FLIP)
			{
				cptFlip++;
				int[][] clsTMP=cls.clone();
				int[] res=unitResolution(clsTMP, interpretation);
				if(res!=null) return res;
				
				flipOne(interpretation);
			}
		
		}
		return null;
		 
		
		
	}
	
	//flip a random atom in the interpretation (multiply by -1)
	public int[] flipOne(int[] interpretation)
	{
		Random r = new Random();
		int i=r.nextInt((interpretation.length-1) + 1);
		
		interpretation[i]=interpretation[i]*-1;
		return interpretation;
		
	}
	
	//create a random interpretation by using listeVariable (50% chance of flipping each atom by multiplying by -1)
	public int[] createRandomInterpretation(int[] listeVariable)
	{
		int[] listeCpy= listeVariable.clone();
		for(int i=0;i<listeCpy.length;i++)
		{
			if(createRandomBool())
			{
				listeCpy[i]=-listeCpy[i];
			}
		}
		
		return listeCpy;
	}
	
	public boolean createRandomBool()
	{
		return Math.random()>0.5;
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
			System.out.println("empty clause!");
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
		ConstructeurClauseStochastic c = new ConstructeurClauseStochastic("queen.cnf");
		
			
		
	}
	
	
}

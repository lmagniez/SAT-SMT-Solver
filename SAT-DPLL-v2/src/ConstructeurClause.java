import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;


public class ConstructeurClause {
	
	private int[][] clauses;
	private int nbClause;
	private int nbVariable;
	private Vector<int[]> solutions = new Vector<int[]>();
	
	
	public ConstructeurClause()
	{
		BufferedReader br = null;
		
		try {

			String sCurrentLine;
			int currentClause=0;

			br = new BufferedReader(new FileReader("queen.cnf"));

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
		int[] res = DPLL(clauses);
		
		System.out.println("\n\n\nRESULTAT");
		for(int i=0; i<res.length; i++)
			System.out.println(res[i]);
		System.out.println("///");
		
	}
	
	
	public static int[][][] unitResolution(int[][] cls)
	{
		
		int[] partialInterpretation=new int[0];
		int nbInterpretation=0;
		
		
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
					int[] tmp=new int[nbInterpretation+1];
					for(int j=0;j<nbInterpretation; j++)
						tmp[j]=partialInterpretation[j];
					partialInterpretation=tmp;
					
					partialInterpretation[nbInterpretation]=cls[i][0];
					nbInterpretation++;
					
					//unitPropagation	
					cls=ConstructeurClause.unitPropagation(cls[i][0],cls);
					if(cls==null) return null; //if there is empty clause/conflict
					found=true;
					break;
				}
			}
			
		}while(found);
		
		int[][][] result = new int[2][][];
		result[0]= new int[1][];
		result[0][0]=partialInterpretation;
		result[1]=cls;
		
		return result;
		
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
	
	public int[] DPLL(int[][] cls)
	{
		
		int[][][] result = unitResolution(cls);
		
		//conflicts
		if(result==null)
		{
			System.out.println("CONFLITS");
			return null;
		}
			
		//no more clauses
		int[] I = result[0][0];
		int[][] newCNF = result[1];
		if(isEmpty(newCNF))
		{
			
			System.out.println("il se passe");
			for(int i=0;i<I.length;i++)
				System.out.println(I[i]+" ");
			System.out.println();
			return I;
		}
		//choose a literal p 
		else
		{
			int p[]= {0};
			int[] res,res2;
			int cpt=0;
			while(cls[cpt].length==1)
				cpt++;
			p[0]=cls[cpt][0];
			
			System.out.println("newCNF: "+newCNF[0][0]);
			
			int[][] CNFToTest = fusion2(newCNF,p[0]);
			int[][] CNFToTest2 = fusion2(newCNF,-p[0]);
			
			res=DPLL(CNFToTest);
			res2=DPLL(CNFToTest2);
			if(res!=null)
				return fusion(res,I);
			else if(res2!=null)
			{
				p[0]=-p[0];
				return fusion(res2,I);
			}
			else
			{
				System.out.println("un conflit");
				return null;
			}
		}
		 
		
		
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
		ConstructeurClause c = new ConstructeurClause();
		
			
		
	}
	
	
}

package smt.v1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Modification of DPLL v2 (the safest version of SAT solver implemented)
 * Used for solving SMT problem
 * @author loick
 *
 */

public class ConstructeurClauseV2SMT {
	
	private int[][] clauses;
	private int nbClause;
	private int nbVariable;
	private Vector<int[]> solutions = new Vector<int[]>();
	protected int[] res;
	
	
	
	/**
	 * execute DPLL algorithm and store solution
	 * @param clauses set of clause
	 */
	public ConstructeurClauseV2SMT(int[][] clauses)
	{
		
			System.out.println("clauses!");
			this.affiche(clauses);
		
			this.res=null;
			int[] partialI = createPartialInterpretation(clauses);
			//System.out.println("result: "+DPLL(clauses, partialI, 0));
			int[] res = DPLL(clauses);
			
			if(res==null)
				System.out.println("UNSATISFIABLE");
			else
			{
				System.out.println("SATISFIABLE");
				
				res=this.selectionSort(res);
				
				System.out.println("\n\n\nRESULTAT (taille:"+res.length+")");
				for(int i=0; i<res.length; i++)
					System.out.print(res[i]+" ");
				System.out.println("");
				
				this.res=res;
			}
	}
	
	/***
	 * while there's unit clause, perform unit propagation
	 * @param cls set of clause
	 * @return array with [0][0]-> interpretation, [1]->clauses
	 */
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
					cls=ConstructeurClauseV2SMT.unitPropagation(cls[i][0],cls);
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
	
	
	
	/**
	 * delete the entire clauses containing literal (replace by 0) \n
	 * delete -literal from all clauses \n
	 * may cause empty clause when delete -literal from clause !(return null) \n
	 * @param literal to propagate
	 * @param cls set of clause
	 * @return set of clause after propagation
	 */
	
	public static int[][] unitPropagation(int literal, int[][] cls)
	{	
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
	
	/**
	 * fusion between clauses and literal
	 * @param tab1
	 * @param literal
	 * @return
	 */
	
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
	
	
	/**
	 * execute DPLL algorithme
	 * @param cls set of clause
	 * @return interpretation if satisfiable, null if unsat
	 */
	public int[] DPLL(int[][] cls)
	{
		
		int[][][] result = unitResolution(cls);
		
		//conflicts
		if(result==null)
		{
			System.out.println("CONFLICT");
			return null;
		}
			
		//no more clauses
		int[] I = result[0][0];
		int[][] newCNF = result[1];
		if(isEmpty(newCNF))
		{
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
	
	
	

	/**
	 * create an array with only 0, having the size equal to number of clause
	 * @param cls
	 * @return interpretation
	 */
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
	
	public int[] selectionSort(int[] data){
		  int lenD = data.length;
		  int j = 0;
		  int tmp = 0;
		  for(int i=0;i<lenD;i++){
		    j = i;
		    for(int k = i;k<lenD;k++){
		      if(Math.abs(data[j])>Math.abs(data[k])){
		        j = k;
		      }
		    }
		    tmp = data[i];
		    data[i] = data[j];
		    data[j] = tmp;
		  }
		  return data;
		}
	
	public static void main(String[] args) {
		//ConstructeurClauseV2 c = new ConstructeurClauseV2("exemple6.cnf");
		//ConstructeurClauseV2SMT c = new ConstructeurClauseV2SMT("uf50-01.cnf");
		//ConstructeurClauseV2 c = new ConstructeurClauseV2("aim-50-1_6-no-1.cnf");
		
	}
	
	
}

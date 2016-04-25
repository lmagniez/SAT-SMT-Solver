import java.io.Console;
import java.io.IOException;

import dpll.v1.ConstructeurClause;
import dpll.v2.ConstructeurClauseV2;
import dpll.v3.ConstructeurClauseV3;


public class Main {

	
	public static void main(String[] args) throws IOException {
		
		
		System.out.println("1- Méthode 1 \n2-Méthode 2 \n3-Méthode 3");
		int choix = System.in.read()-48;
		
		System.out.println(choix);
		
		if(choix==1)
		{
		
			ConstructeurClause c = new ConstructeurClause("queen.cnf");
		
			for(int i=0; i<c.getSolutions().size();i++)
			{
				int[] soluce = c.getSolutions().get(i);
				for(int j=0; j<soluce.length;j++)
					System.out.print(soluce[j]+" ");
				System.out.println();
			}
		}
		else if(choix==2)
		{
			ConstructeurClauseV2 c = new ConstructeurClauseV2("queen.cnf");
		}
		else if(choix==3)
		{
			ConstructeurClauseV3 c = new ConstructeurClauseV3("queen.cnf");
		}
		
		
	}
	
}

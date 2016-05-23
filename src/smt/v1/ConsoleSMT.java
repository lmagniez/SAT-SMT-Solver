package smt.v1;

import java.util.Scanner;
import java.util.Vector;

public class ConsoleSMT {

	private Vector<String> listOfName;
	private Vector<Element> listOfElement;
	private Vector<Relation> listOfAssert;
	
	private SetOfClause setOfClause;
	
	
	public ConsoleSMT()
	{
		listOfName= new Vector<String>();
		listOfElement= new Vector<Element>();
		setOfClause= new SetOfClause();
		listOfAssert= new Vector<Relation>();
	}
	
	public void exec() throws CloneNotSupportedException
	{
		String input="";
		Scanner scan=new Scanner(System.in);
		while(!input.equals("exit()"))
		{
			input=scan.nextLine();
			System.out.println(input);
			
			String[] commande = input.split(" ");
			
			//declare-const a Int
			if(commande[0].equals("declare-const"))
			{
				declareConst(commande);
			}
			
			//declare-fun f (Int Bool) Int
			if(commande[0].equals("declare-fun"))
			{
				declareFun(commande);
				
			}
			
			//assert > a 10
			//assert < (f a true) 100
			//assert < (f a 2) 100
			if(commande[0].equals("assert"))
			{
				assertion(commande);
			}
			
			//declare-clause 1 2
			//declare-clause -1 2
			//declare-clause 1 2 3
			if(commande[0].equals("declare-clause"))
			{
				declareClause(commande);
			}
			
			
			
			if(commande[0].equals("print"))
				affiche();
			if(commande[0].equals("print2"))
				affiche2();
			
			if(commande[0].equals("check-sat"))
			{
				createSetOfClause();
				checkSat();
			}
			System.out.println();
			
			
		}
	}
	
	public void declareConst(String[] commande)
	{
		if(commande.length<3)
			System.out.println("SYNTAX ERROR");
		else
		{
			if(!commande[2].toLowerCase().equals("int"))
				System.out.println("SYNTAX ERROR2"+commande[2].toLowerCase());
			else
			{
				if(exist(commande[1]))
					System.out.println("ERROR: Name already used");
				else
				{
					listOfName.add(commande[1]);
					listOfElement.addElement(new Variable(commande[1],"int"));
				}
			}
		}
	}
	
	public void declareFun(String[] commande)
	{
		if(commande.length<4)
			System.out.println("SYNTAX ERROR");
		else
		{
			
			if(commande[2].charAt(0)=='(')
			{
				//add the arguments
				Vector<String> args= new Vector<String>();
				String tmp;
				commande[2]=commande[2].substring(1, commande[2].length());
				
				int i = 2;
				//search for the ')'
				while(commande[i].charAt(commande[i].length()-1)!=')'&&i<commande.length)
				{
					if(commande[i].toLowerCase().equals("int"))
						args.add(commande[i]);
					else System.out.println("SYNTAX ERROR1");
					i++;
				}
				
				//get the ')'
				tmp=commande[i].substring(0, commande[i].length()-1);
				if(tmp.toLowerCase().equals("int"))
				{
					args.add(tmp);
					
					//get type of return 
					String tReturn=commande[i+1];
					if(!commande[i+1].toLowerCase().equals("int"))
						System.out.println("SYNTAX ERROR3");
					else 
					{						
						//name already exist?
						if(exist(commande[1]))
							System.out.println("ERROR: Name already used");
						else
						{
							listOfName.add(commande[1]);
							listOfElement.addElement(new Function(args, tReturn, commande[1]));
						}	
					}
				}
				
				else System.out.println("SYNTAX ERROR2");
				
				
				
				
			}
			else
			{
				System.out.println("SYNTAX ERROR");
			}
		}
	}
	
	public void assertion(String[] commande) throws CloneNotSupportedException
	{
		if(commande.length<4)
			System.out.println("SYNTAX ERROR");
		else
		{
			int relation=-10;
			Element e1 = null;
			Element e2 = null;
			
			if(commande[1].equals("="))
				relation=0;
			else if(commande[1].equals("!="))
				relation=10;
			else if(commande[1].equals(">"))
				relation=1;
			else if(commande[1].equals(">="))
				relation=2;
			else if(commande[1].equals("<"))
				relation=-1;
			else if(commande[1].equals("<="))
				relation=-2;
			
			if(relation==-10)
				System.out.println("SYNTAX ERROR");
			else
			{
				int pose2=3;
				
				///////////
				//  e1   //
				///////////
				
				//may be variable, number, or function
				//variable
				if(commande[2].charAt(0)!='(')
				{
					if(isNumeric(commande[2]))
						e1 = new Variable(Integer.parseInt(commande[2]), commande[2], "int");
					else
						e1=findByName(commande[2]);
						
				}
				//function
				else
				{
					e1 = this.gererFonction(commande, 2);
					if(e1!=null)
					{	
						int i=2;
						int cpt=0;
						while(cpt!=0)
						{
							if(commande[i].charAt(commande[i].length()-1)==')')cpt--;
							if(commande[i].charAt(0)=='(')cpt++;
							
							i++;
						}
						pose2=i+1;
					}
				}
				
				/////////////
				//	e2	   //
				/////////////
				if(e1!=null)
				{
					//variable
					
					if(commande[pose2].charAt(0)!='(')
					{
						if(isNumeric(commande[pose2]))
							e2 = new Variable(Integer.parseInt(commande[pose2]), commande[pose2], "int");
						else
							e2=findByName(commande[pose2]);		
					}
					//function
					else
					{
						e2 = gererFonction(commande, pose2);
					}
				}
				
			}
			if(e1==null||e2==null)
				System.out.println("SYNTAX ERROR |");
			else
			{
				Relation r = new Relation(e1, e2, relation);
				listOfAssert.addElement(r);
				System.out.println("nouvelle relation, id: " + r.id);
			}
		}
	}
	
	public void declareClause(String[] commande)
	{
		if(commande.length<3)
		{
			System.out.println("SYNTAX ERROR");
		}
		else
		{
			Vector<Relation> listeRelation= new Vector<Relation>();
			boolean add=true;
			for(int i=1; i<commande.length; i++)
			{
				if(this.isNumeric(commande[i]))
				{	
					int id = Integer.parseInt(commande[i]);
					Relation r = Relation.findById(this.listOfAssert, Math.abs(id));
					listeRelation.add(r);
				}
				else
				{
					System.out.println("SYNTAX ERROR");
					add=false;
					break;
				}
			}
			if(add)
			{
				this.setOfClause.addClause(new Clause(listeRelation));
			}
			
			
		}
	}
	
	public void createSetOfClause()
	{
		for(int i=0; i<this.listOfAssert.size(); i++)
			if(!listOfAssert.get(i).usedForClause)
			{
				Clause c = new Clause(listOfAssert.get(i));
				setOfClause.addClause(c);
			}
	}
	
	public void checkSat()
	{
		int[][] cls;
		int nbVar=this.listOfAssert.size();
		Vector<Clause> vec = setOfClause.clauses;
		
		
		cls=new int[vec.size()+1][];
		cls[0]=new int[1];
		cls[0][0]=nbVar;
		
		//for each clauses
		for(int i=0; i<vec.size(); i++)
		{	
			Vector<Relation> vec2 = vec.get(i).relations;
			int[] clause = new int[vec2.size()+1];
			//for each assertions
			for(int j=0; j<vec2.size(); j++)
			{
				clause[j]=vec2.get(j).id;
			}
			clause[vec2.size()]=0;
			cls[i+1]=clause;
		}
		
		this.affiche();
		System.out.println();
		this.affiche(cls);
		System.out.println("ok");
		
		ConstructeurClauseV2 c = new ConstructeurClauseV2(cls);
		
		
	}
	
	
	public boolean isNumeric(String s) {
	    return java.util.regex.Pattern.matches("\\d+", s);
	}
	
	/*
	 * start with (name arg1 arg2)...
	 * recursive if multiple function
	 */
	public Function gererFonction(String[] commandes, int begin) throws CloneNotSupportedException
	{
		String[] commande = commandes.clone();
		
		//remove the '('
		commande[begin]=commande[begin].substring(1, commande[begin].length());
		Function f = (Function) findByName(commande[begin]);
		
		if(f==null)
		{
			System.out.println("SYNTAX ERROR");
			return null;
		}
		f=(Function) f.clone();
		Vector<Element> args = new Vector<Element>();
		
		boolean end=false;
		
		//add every argument to f
		for(int i=begin+1; i<commande.length; i++)
		{
			
			//function
			if(commande[i].charAt(0)=='(')
			{	
				Function f2=gererFonction(commande,i);
				args.add(f2);
				//go after the function
				while(commande[i].charAt(commande[i].length()-1)!=')')
				{	
					i++;
				}
				
			}
			
			
			//variable
			else
			{
				if(commande[i].charAt(commande[i].length()-1)==')')
				{	
					commande[i]=commande[i].substring(0, commande[i].length()-1);
					end=true;
				}
				
				//i++;
				if(isNumeric(commande[i]))
				{
					args.add(new Variable(Integer.parseInt(commande[i]), commande[i], "int"));
				}
				else
				{	
					Element test=findByName(commande[i]);
					if(test==null)
					{	
						System.out.println("ERROR SYNTAX");
						return null;
					}
					else
					{
						args.add(test);
					}
				}
			}
			
			if(end) break;
			
				
		}
		
		f.arguments=args;
		
		//concordance des types;
		for(int i=0; i< f.arguments.size(); i++)
		{
			
			if(!f.arguments.get(i).type.equals(f.argumentsTypes.get(i)))
			{
				System.out.println("TYPE ERROR");
				return null;
			}
		}
		
		
		return f;
		
		
	}
	
	public boolean exist(String name)
	{
		for(int i=0; i<listOfName.size(); i++)
		{
			if(listOfName.get(i).equals(name))return true;
				
		}
		return false;
	}
	
	public Element findByName(String name)
	{
		for(int i=0; i<listOfElement.size(); i++)
		{
			if(listOfElement.get(i).getName().equals(name))
				return listOfElement.get(i);
		}
		return null;
	}
	
	
	
	public void affiche()
	{
		System.out.println("listOfName");
		for(int i=0; i<listOfName.size(); i++)
		{
			System.out.println(listOfName.get(i));
		}
		System.out.println("\nlistOfElement");
		for(int i=0; i<listOfElement.size(); i++)
		{
			System.out.println(listOfElement.get(i).printTypes());
		}
		System.out.println("\nlistOfAssertion");
		for(int i=0; i<listOfAssert.size(); i++)
		{
			String space="";
			if(listOfAssert.get(i).id>=0)
				space=" ";
			System.out.println("id: "+space+listOfAssert.get(i).id+ " " +listOfAssert.get(i));
		}
		System.out.println("\nlistOfClause");
		System.out.println(setOfClause);
	
	}
	
	public void affiche2()
	{
		System.out.println("listOfName");
		for(int i=0; i<listOfName.size(); i++)
		{
			System.out.println(listOfName.get(i));
		}
		System.out.println("\nlistOfElement");
		for(int i=0; i<listOfElement.size(); i++)
		{
			System.out.println(listOfElement.get(i).affiche());
		}
		System.out.println("\nlistOfAssertion");
		for(int i=0; i<listOfAssert.size(); i++)
		{
			System.out.println(listOfAssert.get(i).affiche());
		}
		System.out.println("\nlistOfClause");
		System.out.println(setOfClause);
	
	}
	
	//affiche clauses
	public static void affiche(int[][] cls) {
		if (cls == null) {
			System.out.println("clause nulle");
			return;
		}

		for (int i = 0; i < cls.length; i++) {
			for (int j = 0; j < cls[i].length; j++)
				System.out.print(cls[i][j] + " ");
			System.out.println("");
		}
	}
	
	public static void main(String[] args) throws CloneNotSupportedException {
		ConsoleSMT console = new ConsoleSMT();
		console.exec();
	}
}

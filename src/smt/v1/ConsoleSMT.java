package smt.v1;

import java.util.Scanner;
import java.util.Vector;

/**
 * SMT solver, only working in EUF right now.\n
 * use interpreter inspired by Z3 solver. See documentation to use it.
 * @author loick
 *
 */
public class ConsoleSMT {

	private Vector<String> listOfName;
	private Vector<Element> listOfElement;
	private Vector<Relation> listOfAssert;
	
	private SetOfClause setOfClause;
	
	private Vector<Element> solution;
	
	
	public ConsoleSMT()
	{
		listOfName= new Vector<String>();
		listOfElement= new Vector<Element>();
		setOfClause= new SetOfClause();
		listOfAssert= new Vector<Relation>();
	}
	
	/**
	 * Start the SMT interpreter (Z3 like), see documentation \n
	 * Check for: \n
	 *  declare-const a Int\n
	 *  declare-fun f (Int Bool) Int\n
	 *  assert > a 10\n
	 *  declare-clause 1 2\n
	 *  print\n
	 *  check-sat\n
	 * @throws CloneNotSupportedException
	 */
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
	/**
	 * Declare and save a const according to the input (check syntax error)\n
	 * declare-const a Int\n
	 * @param commande
	 */
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
	
	/**
	 * declare a function and save it (check for syntax error)\n
	 * check for name conflict\n
	 * declare-fun f (Int Bool) Int\n
	 * @param commande
	 */
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
	
	
	/**
	 * declare a relation between 2 element and save it, according to the input (check for syntax error)\n
	 * check for type conflict, name conflict\n
	 * assert > a 10\n
	 * @param commande
	 * @throws CloneNotSupportedException
	 */
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
				relation=3;
			else if(commande[1].equals("!="))
				relation=-3;
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
						do
						{
							int j=1;
							System.out.println(commande[i]+ " " + i);
							while(commande[i].charAt(commande[i].length()-j)==')')
								{
								cpt--;
								j++;
								}
							if(commande[i].charAt(0)=='(')cpt++;
							
							i++;
						}while(cpt!=0);
						pose2=i;
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
						{
							System.out.println(commande[pose2]);
							e2=findByName(commande[pose2]);		
						}
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
	
	/**
	 * declare and save a clause according to the input (has to be id already existing)\n
	 * declare-clause 1 2\n
	 * @param commande
	 */
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
					if(r==null)
					{
						System.out.println("SYNTAX ERROR");
						add = false;
						break;
					}
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
	
	/**
	 * create the set of clause according to the existing clause \n
	 * if relation are not used for clause, they become clause itself
	 */
	public void createSetOfClause()
	{
		for(int i=0; i<this.listOfAssert.size(); i++)
			if(!listOfAssert.get(i).usedForClause)
			{
				Clause c = new Clause(listOfAssert.get(i));
				setOfClause.addClause(c);
			}
	}
	
	/**
	 * check satisfiability of the set of clause
	 * use theory solver and sat solver
	 * @throws CloneNotSupportedException
	 */
	public void checkSat() throws CloneNotSupportedException
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
		
		
		while(true)
		{
			ConstructeurClauseV2SMT c = new ConstructeurClauseV2SMT(cls.clone());
		
			if(c.res==null)
			{
				System.out.println("SMT UNSATISFIABLE");
				break;
			}
			
			int [] result = c.res;
			
			SetOfClause newSet;
			newSet= (SetOfClause) setOfClause.clone();
			
			//adapt the smt set of formula
			//delete when not in the solution
			for(int i=0; i<newSet.clauses.size(); i++)
			{
				Vector<Relation> clause = newSet.clauses.get(i).relations;
				for(int j=0; j<clause.size(); j++)
				{
					boolean found=false;
					//check if the relation id is in the sat formula, otherwise, delete it
					for(int k=0; k<result.length; k++)
					{
						if(result[k]==clause.get(j).id)
							found=true;
						if(result[k]==-clause.get(j).id)
						{
							found= true;
							clause.get(j).id=-clause.get(j).id;
						}
					}
					if(!found)
					{
						System.out.println("remove");
						clause.remove(j);
						j--;
					}
				}
			}
			
			System.out.println(newSet);
			
			//setOfClause=newSet;
			
			boolean sat = theorySolver(newSet);
			if(sat)
			{
				System.out.println("SMT SATISFIABLE");
				
				break;
			}
			
			result=negate(result);
			cls=add(cls,result);
			cls[0][0]++;
			
			System.out.println("add clause:");
			this.affichetab(result);
			
			System.out.println(newSet);
			
		}
		
		
	}
	
	/**
	 * Theory solver
	 * Check if an interpretation of smt problem is satisfiable or not
	 * @param newSet interpretation of smt problem
	 * @return sat or unsat
	 */
	private boolean theorySolver(SetOfClause newSet) {
		
		
		
		Vector<Element> listOfElt = new Vector<Element>();
		boolean noChange=true;
		do
		{
			noChange=true;
			listOfElt = new Vector<Element>();
			
			for(int i=0; i<newSet.clauses.size(); i++)
			{
				Clause c = newSet.clauses.get(i);
				
				for(int j=0; j<c.relations.size(); j++)
				{
					Relation r = c.relations.get(j);
					//if relation = or !=
					if(Math.abs(r.relation)==3)
					{	
						//search for every c=d variable
						//to replace d by c
						//avoid c=10...
						if(r.e1 instanceof Variable)
						{
							Variable v1 = (Variable) r.e1;
							if(v1.value==null)
								if(r.e2 instanceof Variable)
								{
									Variable v2 = (Variable) r.e2;
									if(v2.value==null&&!v2.equals(v1))
									{
										replace(newSet,v1,v2);
										noChange=false;
										if(!listOfElt.contains(v1))listOfElement.add(v1);
									}
								}
						}
						
						//e1 function
						//get the possible value using the set of clause
						if(r.e1 instanceof Function)
						{
							r.e1.relations.add(r);
							if(!listOfElement.contains(r.e1))listOfElt.add(r.e1);
						}
						
						if(r.e2 instanceof Function)
						{
							r.e2.relations.add(r);
							if(!listOfElement.contains(r.e2))listOfElt.add(r.e2);
						}
						
						
					}//if
				}//for
			}//for
			
			//resolutions (replace function by variable)
			//search for each variable a possible resolution (we look in the list of Element establish previously)
			System.out.println("listeElt");
			for(int i=0; i<listOfElt.size(); i++)
			{
				System.out.print(listOfElt.get(i)+": ");
				System.out.println(listOfElt.get(i).affiche());
				Element e = listOfElt.get(i);
				
				int equalsCount=0;
				Element solution = null;
				
				//for each relations in e
				for(int j=0; j<e.relations.size(); j++)
				{
					Relation r = e.relations.get(j);
					//if = 
					if(r.relation==3)
					{
						equalsCount++;
						if(equalsCount==1)
							solution=r.e2;
						
					}
						
					
				}
				if(equalsCount==1)
				{
					replace(setOfClause,solution,e);
					noChange=false;
				}
				
			}//for
			
			
			
		}while(!noChange);
		
		//search for conflict
		System.out.println("search for conflict");
		
		//for each clause
		for(int i=0; i<newSet.clauses.size(); i++)
		{
			Clause c = newSet.clauses.get(i);
			
			//for each relation
			for(int j=0; j<c.relations.size(); j++)
			{
				Relation r=c.relations.get(j);
				//conflict type: c!=c
				System.out.println("equals" + r);
				if(r.e1.toString().equals(r.e2.toString())&&r.relation==-3)
				{
					System.out.println("yes");
					return false;
				}
			}
			
		}
		
		this.solution=listOfElement;
		return true;
	}//THEORY SOLVER

	
	/**
	 * replace element v2 by v1 in the set of clause (simplification)
	 * @param newSet
	 * @param v1
	 * @param v2
	 */
	private void replace(SetOfClause newSet, Element v1, Element v2) 
	{
		System.out.println("replace "+v1+" "+v2);
		
		for(int i=0; i<newSet.clauses.size(); i++)
		{
			Clause c = newSet.clauses.get(i);
			for(int j=0; j<c.relations.size(); j++)
			{
				Relation r = c.relations.get(j);
				if(r.e1.equals(v2))
					r.e1=v1;
				if(r.e2.equals(v2))
					r.e2=v1;
				
				//search in the function arguments
				if(r.e1 instanceof Function)
				{
					
					Function f = (Function) r.e1;
					//we register every function we encounter 
					Vector<Function> listFunction= new Vector<Function>();
					listFunction.add(f);
					do
					{
						//choose the next function to analyse
						if(!listFunction.isEmpty())
						{	
							
							f=listFunction.get(0);
							listFunction.remove(0);
						}
						
						//search in the actual function is the element is inside
						for(int k=0; k<f.arguments.size(); k++)
						{
							//System.out.println(v2+" "+f.arguments.get(k));
							
							if(v2.toString().equals(f.arguments.get(k).toString()))
							{
								f.arguments.remove(k);
								f.arguments.addElement(v1);
								k--;
							}
							
						}
						
						//search for function and register to the list
						for(int k=0; k<f.arguments.size(); k++)
						{
							if(f.arguments.get(k) instanceof Function)
							{
								f=(Function) f.arguments.get(k);
								listFunction.add(f);
							}
						}
						
					}while(!listFunction.isEmpty());
					
				}//if (is function)
			}//for (relations)
		}//for (clauses)
				
		
	}

	/**
	 * negate a clause
	 * @param cls
	 * @return new clause
	 */
	public int[] negate (int[] cls)
	{
		for(int i=0; i<cls.length; i++)
		{
			cls[i]=-cls[i];
		}
		return cls;
		
	}
	
	public static int[][] add(int[][] tab, int[] elt) {
		int[][] newTab = new int[tab.length + 1][];
		for (int i = 0; i < tab.length; i++) {
			newTab[i] = tab[i];
		}
		newTab[tab.length] = elt;
		return newTab;
	}
	
	public boolean isNumeric(String s) {
	    return java.util.regex.Pattern.matches("\\d+", s);
	}
	
	public static void affichetab(int[] t) {
		for (int i = 0; i < t.length; i++)
			System.out.print(t[i] + " ");
		System.out.println();
		System.out.println();
	}
	
	/*
	 * start with (name arg1 arg2)...
	 * recursive if multiple function
	 */
	
	
	/**
	 * check if a function has good syntax and return the deepest function 
	 * @param commandes input
	 * @param begin where to begin
	 * @return deepest function (f(g(b)) would return g(b))
	 * @throws CloneNotSupportedException
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
				
				commande[i]=commande[i].substring(0, commande[i].length()-1);
				if(commande[i].charAt(commande[i].length()-1)==')')
					end=true;
				
			}
			
			
			//variable
			else
			{
				//remove the ')'
				while(commande[i].charAt(commande[i].length()-1)==')')
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
						System.out.println("ERROR SYNTAX: no such element ");
						return null;
					}
					else
					{
						args.add(test);
					}
				}
			}
			
			if(end)
				{
				break;
				}
			
				
		}
		
		f.arguments=args;
		
		/*
		System.out.println(f.arguments.size());
		System.out.println(f.argumentsTypes.size());
		System.out.println(f);
		*/
		
		if(f.arguments.size()!=f.argumentsTypes.size())
		{
			System.out.println("ERROR: NUMBER ARGUMENT");
			return null;
		}
		//concordance des types;
		for(int i=0; i< f.arguments.size(); i++)
		{
			if(!f.arguments.get(i).type.equals(f.argumentsTypes.get(i)))
			{
				System.out.println("TYPE ERROR");
				return null;
			}
		}
		
		System.out.println("f is okay: "+f);
		
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

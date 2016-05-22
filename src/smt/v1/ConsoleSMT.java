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
			
			//declare-fun f (Int Bool) Int
			if(commande[0].equals("declare-fun"))
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
			
			//assert > a 10
			//assert < (f a true) 100
			//assert < (f a 2) 100
			if(commande[0].equals("assert"))
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
							e1 = gererFonction(commande, 2);
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
						listOfAssert.addElement(new Relation(e1, e2, relation));
				}
			}
			
			if(commande[0].equals("print"))
				affiche();
			
			System.out.println();
			
			
		}
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
		System.out.println("listOfElement");
		for(int i=0; i<listOfElement.size(); i++)
		{
			System.out.println(listOfElement.get(i));
		}
		System.out.println("listOfAssertion");
		for(int i=0; i<listOfAssert.size(); i++)
		{
			System.out.println(listOfAssert.get(i));
		}
	
	}
	
	
	public static void main(String[] args) throws CloneNotSupportedException {
		ConsoleSMT console = new ConsoleSMT();
		console.exec();
	}
}

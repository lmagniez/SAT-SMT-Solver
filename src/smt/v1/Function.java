package smt.v1;

import java.util.Arrays;
import java.util.Vector;

/**
 * Function (contains arguments with a defined type)
 * may contains nested function
 * @author loick
 *
 */

public class Function extends Element implements Cloneable{

	Vector<Element> arguments; //Element of the function (Variable or Function)
	Vector<String> argumentsTypes; //type of argument (has to correspond with argument)
	
	public Function(Vector<String> v, String returnT, String name)

	{
		super(name,returnT);
		arguments=new Vector<Element>();
		for(int i=0; i<v.size(); i++)
		{
			String res = v.get(i).toLowerCase();
			v.set(i, res);
		}
		argumentsTypes = v;
	}
	
	
	public String affiche() {
		return "Function [arguments=" + arguments + ", name=" + name
				+ ", argumentsTypes=" + argumentsTypes + ", returnType="
				+ type + " relations="+ relations + "]";
		
	}


	public String printTypes()
	{
		String res;
		res = name+"(";
		for(int i=0; i<argumentsTypes.size(); i++)
		{
			res=res+argumentsTypes.get(i).toString();
			if(i+1<argumentsTypes.size())
				res+=" ";
		}
		res+=")";
		res+=" return: "+type;
		
		
		return res;
	}
	
	@Override
	public String toString() {
		/*return "Function [arguments=" + arguments + ", name=" + name
				+ ", argumentsTypes=" + argumentsTypes + ", returnType="
				+ type + "]";
		*/
		String res;
		res = name+"(";
		for(int i=0; i<arguments.size(); i++)
		{
			res=res+arguments.get(i).toString();
			if(i+1<arguments.size())
				res+=" ";
		}
		res+=")";
		
		
		return res;
		
	}







	
	
	
	
	
}

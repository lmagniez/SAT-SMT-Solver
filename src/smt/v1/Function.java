package smt.v1;

import java.util.Arrays;
import java.util.Vector;

public class Function extends Element implements Cloneable{

	Vector<Element> arguments;
	Vector<String> argumentsTypes;
	
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
				+ type + "]";
		
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





	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	
	
	
	
	
}

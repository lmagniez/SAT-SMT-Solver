package dpll.v3;

/**
 * Graph, contains Node
 * @author loick
 *
 */
public class Graph {

	private Node[] nodes;
	private int actualLvl;
	
	public Graph()
	{
		this.nodes= new Node[0];
		this.actualLvl=0;
	}
	
	/**
	 * add a node to the Graph
	 * @param n
	 */
	public void addNode(Node n)
	{
		//peut pas ajouter level plus bas
		if(n.getLevel()<actualLvl)
		{
			System.out.println("Erreur, noeud plus bas level que le dernier");
			return;
		}
		
		Node[] newNodes= new Node[nodes.length+1];
		for(int i=0;i<nodes.length;i++)
		{
			newNodes[i]=this.nodes[i];
		}
		newNodes[nodes.length]=n;
		this.nodes=newNodes;
		this.actualLvl=nodes[nodes.length-1].getLevel();
		
	}
	
	//remove until reach assertionLevel
	//return i = indicator for interpretation
	//negate at the end
	
	/**
	 * remove until reach assertionLevel
	 * negate at the end
	 * @param assertionLevel
	 * @return return i = indicator for interpretation
	 */
	public int removeNode(int assertionLevel)
	{
		System.out.println("assertionLevel: "+assertionLevel);
		
		if(assertionLevel==-1)
		{
			Node n = nodes[0];
			Node[] newnode = new Node[1];
			newnode[0]=n;
			this.nodes= newnode;
			
			return -1;
		}
		
		int i=0;
		
		//erase after asserting level
		while(nodes[i].getLevel()<assertionLevel||i==nodes.length-1)
		{
			i++;
		}

		//nothing to delete
		if(i==nodes.length)
		{
			System.out.println("Erreur, rien a supprimer");
			return -1; 
		}
		
		Node[] newNodes=new Node[i+1];
		
		for(int j=0; j<i+1; j++)
		{
			newNodes[j]=nodes[j];
		}
		nodes=newNodes;
		this.actualLvl=nodes[nodes.length-1].getLevel();
		return nodes[i].getSrc();
		
	}
	
	public Node getNode(int i)
	{
		return this.nodes[i];
	}
	
	public Node[] getNode()
	{
		return this.nodes;
	}
	
	public int getLevelOf(int elt)
	{
		for(int i=0; i<nodes.length; i++)
		{
			if(nodes[i].getSrc()==elt)
			{
				return nodes[i].getLevel();
			}
		}
		return -1;
	}
	
	public int getLevel()
	{
		return actualLvl;
	}
	
	public void setLevel(int actualLvl)
	{
		this.actualLvl=actualLvl;
	}
	
	public void affiche()
	{
		System.out.println("actual lvl: "+actualLvl);
		for(int i=0;i<nodes.length; i++)
		{
			System.out.print("src: "+nodes[i].getSrc()+" Level: "+ nodes[i].getLevel()+
					" cut: "+nodes[i].getCut()+" \ndst: ");
			
			
			for(int j=0; j<nodes[i].getDst().length;j++)
				System.out.print(nodes[i].getDst(j));
			
			System.out.println("\n");
			
			
		}
		
	}
	
	
}

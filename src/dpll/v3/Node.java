package dpll.v3;

/**
 * Node (variable), has level of decision, clause of origin, antecedants and cut
 * @author loick
 *
 */
public class Node {

	private int level;//level of decision
	private int src;//clause of origin
	private int dst[];//variable that allowed finding this variable
	private int cut;//how many resolution on this level before this variable
	
	public Node(int level, int src, int cut)
	{
		this.level=level;
		this.src=src;
		this.cut=cut;
		
	}
	
	public Node(int level, int src, int cut, int[] dst)
	{
		this(level,src,cut);
		this.dst=dst;
	}
	
	
	public int getLevel()
	{
		return level;
	}
	
	public int getSrc()
	{
		return src;
	}
	
	public int getDst(int i)
	{
		return dst[i];
	}
	
	public int getCut()
	{
		return cut;
	}
	
	public int[] getDst()
	{
		return dst;
	}
	
}

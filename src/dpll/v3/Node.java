package dpll.v3;


public class Node {

	private int level;
	private int src;
	private int dst[];
	private int cut;
	
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

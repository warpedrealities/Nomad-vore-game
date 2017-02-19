package artificial_intelligence;

public class ScriptMemory {

	int []value;
	public ScriptMemory()
	{
		value=new int[8];
	}
	public int getValue(int index) {
		return value[index];
	}
	public void setValue(int index, int value) {
		this.value[index] = value;
	}
	
}

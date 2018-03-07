package actor;

public class Modifier_Element {

	int type;
	int index;
	float value;
	
	public Modifier_Element(int type, int index, float value) {
		this.type=type;
		this.index=index;
		this.value=value;
	}

	public int getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}

	public float getValue() {
		return value;
	}	
	
}

package mutation;

public class ProportionalEffect {

	private float ratio;
	public ProportionalEffect(float  ratio) {
		this.ratio=ratio;
	}

	public float getRatio()
	{
		return ratio;
	}

	public int getMod(float v) {
		float sub=v*ratio;
		float out=v-sub;
		if (out<1)
		{
			out=1;
		}
		return (int)out;
	}
}

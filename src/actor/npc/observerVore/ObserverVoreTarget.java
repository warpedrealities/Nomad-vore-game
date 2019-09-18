package actor.npc.observerVore;

public class ObserverVoreTarget {
	private String target;
	private String scene;
	private String preference;

	public ObserverVoreTarget(String target, String scene, String preference) {
		this.target = target;
		this.scene = scene;
		this.preference = preference;
	}

	public String getTarget() {
		return target;
	}

	public String getScene() {
		return scene;
	}

	public String getPreference() {
		return preference;
	}


}

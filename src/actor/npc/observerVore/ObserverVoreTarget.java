package actor.npc.observerVore;

import java.util.ArrayList;
import java.util.List;

public class ObserverVoreTarget {
	private String target;
	private String scene;
	private String preference;

	private List<ObserverVoreReq> requirements;

	public ObserverVoreTarget(String target, String scene, String preference) {
		this.target = target;
		this.scene = scene;
		this.preference = preference;
		requirements = new ArrayList<ObserverVoreReq>();
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

	public List<ObserverVoreReq> getRequirements() {
		return requirements;
	}

	public void AddRequirement(ObserverVoreReq requirement) {
		requirements.add(requirement);
	}

}

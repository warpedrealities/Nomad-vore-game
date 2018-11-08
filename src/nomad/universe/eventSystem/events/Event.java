package nomad.universe.eventSystem.events;

public interface Event {

	boolean eligible();

	void trigger();
}

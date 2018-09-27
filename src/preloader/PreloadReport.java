package preloader;

public class PreloadReport {

	private int completion;
	private final int COMPLETIONTOTAL = 2;

	public PreloadReport() {
		completion = 0;
	}

	public void run() {
		Thread thread = new Thread(new PreloadPerks(this));
		thread.run();
		thread = new Thread(new PreloadUniverse(this));
		thread.run();
	}

	public boolean isComplete() {
		if (completion == COMPLETIONTOTAL) {
			return true;
		}
		return false;
	}

	public void complete() {
		completion++;
	}
}

package preloader;

import nomad.universe.Universe;

public class PreloadUniverse implements Runnable {

	private PreloadReport report;

	public PreloadUniverse(PreloadReport preloadReport) {
		this.report = preloadReport;
	}

	@Override
	public void run() {

		Universe universe = new Universe();

		this.report.complete();
	}

}

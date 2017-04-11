package preloader;

import perks.PerkLibrary;

public class PreloadPerks implements Runnable {

	private PreloadReport report;
	
	public PreloadPerks(PreloadReport preloadReport) {
		this.report=preloadReport;
	}

	@Override
	public void run() {

		PerkLibrary.getInstance();
		
		this.report.complete();
	}

}

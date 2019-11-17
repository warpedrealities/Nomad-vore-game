package actor.npc.observerVore;

public class ObserverVoreReq {

	public enum REQ {
		EQUALS, LESSTHAN, GREATERTHAN
	};

	private boolean global;
	private REQ req;
	private int value;
	private String flag;

	public ObserverVoreReq(String flag, String req, int value, boolean global) {
		this.flag = flag;
		this.value = value;
		this.global = global;
		this.req = calcReq(req);
	}

	private REQ calcReq(String req2) {
		if ("GREATERTHAN".equals(req2)) {
			return REQ.GREATERTHAN;
		}
		if ("LESSTHAN".equals(req2)) {
			return REQ.LESSTHAN;
		}
		return REQ.EQUALS;
	}

	public boolean isGlobal() {
		return global;
	}

	public REQ getReq() {
		return req;
	}

	public int getValue() {
		return value;
	}

	public String getFlag() {
		return flag;
	}


}

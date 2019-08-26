package artificial_intelligence;

import java.util.ArrayList;

public class BrainBank {

	private static BrainBank m_instance;
	private ArrayList<Script_AI> m_ai;
	private ScriptMemory sharedMemory;

	public BrainBank() {
		m_ai = new ArrayList<Script_AI>();
		sharedMemory=new ScriptMemory();
	}

	Script_AI getDupe(String name) {
		// if any ai have the same name return that
		if (m_ai.size() > 0) {
			for (int i = 0; i < m_ai.size(); i++) {
				if (m_ai.get(i).getName().equals(name)) {
					return m_ai.get(i);
				}
			}
		}

		return null;
	}

	public Script_AI getAI(String name) {
		Script_AI ai = getDupe(name);

		if (ai == null) {
			ai = new Script_AI(name,sharedMemory);
			m_ai.add(ai);
		}
		return ai;

	}

	static public BrainBank getInstance() {
		if (m_instance == null) {
			m_instance = new BrainBank();
		}
		return m_instance;
	}

	public void cleanActives() {
		for (int i = 0; i < m_ai.size(); i++) {
			m_ai.get(i).setActive(false);
		}
	}

	public void runMaster() {
		for (int i = 0; i < m_ai.size(); i++) {
			if (m_ai.get(i).getActive()) {
				m_ai.get(i).runMaster();
			}
		}
	}

}

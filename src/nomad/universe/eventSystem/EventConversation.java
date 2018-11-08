package nomad.universe.eventSystem;

import org.w3c.dom.Element;

import dialogue.DialogueScene;
import dialogue.DialogueScene.dialogueOrigin;
import nomad.universe.Universe;
import nomad.universe.eventSystem.events.Event_Impl;
import spaceship.Spaceship;
import vmo.Game;

public class EventConversation extends Event_Impl {

	private String conversationFile;

	public EventConversation(Element e) {
		conversationFile = e.getAttribute("file");
		this.construct((Element) e.getElementsByTagName("conditions").item(0));
	}

	@Override
	public void trigger() {
		DialogueScene dscene = new DialogueScene(conversationFile, dialogueOrigin.View);
		dscene.setShip((Spaceship) Universe.getInstance().getCurrentEntity());
		Game.sceneManager.SwapScene(dscene);
	}

}

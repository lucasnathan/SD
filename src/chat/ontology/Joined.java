package chat.ontology;

import jade.content.Predicate;
import jade.core.AID;

import java.util.List;

//#J2ME_EXCLUDE_FILE

/**
 * Joined predicate used by chat ontology.
 * 
 * @author Michele Izzo - Telecomitalia
 */

@SuppressWarnings("serial")
public class Joined implements Predicate {

	private List<AID> _who;

	public List<AID> getWho() {
		return _who;
	}

	public void setWho(List<AID> who) {
		_who = who;
	}

}

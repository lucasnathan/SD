package chat.ontology;

import jade.content.Predicate;

/**
 * Spoken predicate used by chat ontology.
 *
 */

@SuppressWarnings("serial")
public class Spoken implements Predicate {

	private String _what;

	public String getWhat() {
		return _what;
	}

	public void setWhat(String what) {
		_what = what;
	}

}
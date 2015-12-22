package chat.ontology;

import jade.content.Predicate;
import jade.core.AID;

import java.util.List;

/**
 * Left predicate used by chat ontology.
 *
 */
@SuppressWarnings("serial")
public class Left implements Predicate {

    private List<AID> _who;

    public List<AID> getWho() {
        return _who;
    }

    public void setWho(List<AID> who) {
        _who = who;
    }

}

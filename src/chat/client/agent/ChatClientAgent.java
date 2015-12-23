/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent
 * systems in compliance with the FIPA specifications. Copyright (C) 2000 CSELT
 * S.p.A. * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 2.1 of the License. * This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * ***************************************************************
 */
package chat.client.agent;

import chat.client.AWTChatGui;
import chat.client.ChatGui;
import chat.ontology.ChatOntology;
import jade.content.ContentManager;
import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import jade.util.leap.Iterator;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;
import java.util.ArrayList;

/*#MIDP_INCLUDE_BEGIN
 import chat.client.MIDPChatGui;
 #MIDP_INCLUDE_END*/
//#MIDP_EXCLUDE_BEGIN
//#MIDP_EXCLUDE_END
/**
 * This agent implements the logic of the chat client running on the user
 * terminal. User interactions are handled by the ChatGui in a
 * terminal-dependent way. The ChatClientAgent performs 3 types of behaviours: -
 * ParticipantsManager. A CyclicBehaviour that keeps the list of participants up
 * to date on the basis of the information received from the ChatManagerAgent.
 * This behaviour is also in charge of subscribing as a participant to the
 * ChatManagerAgent. - ChatListener. A CyclicBehaviour that handles messages
 * from other chat participants. - ChatSpeaker. A OneShotBehaviour that sends a
 * message conveying a sentence written by the user to other chat participants.
 *
 * @author Giovanni Caire - TILAB
 */
public class ChatClientAgent extends Agent {

    private static final long serialVersionUID = 1594371294421614291L;
    private static final String CHAT_ID = "__chat__";
    private static final String CHAT_MANAGER_NAME = "manager";
    private Logger logger = Logger.getMyLogger(this.getClass().getName());
    private ChatGui myGui;
    private Set participants = new SortedSetImpl();
    private Codec codec = new SLCodec();
    private Ontology onto = ChatOntology.getInstance();
    private ACLMessage spokenMsg;

    protected void setup() {
        // Register language and ontology
        ContentManager cm = getContentManager();
        cm.registerLanguage(codec);
        cm.registerOntology(onto);
        cm.setValidationMode(false);

        // Add initial behaviours
        addBehaviour(new ParticipantsManager(this));
        addBehaviour(new ChatListener(this));

        // Initialize the message used to convey spoken sentences
        spokenMsg = new ACLMessage(ACLMessage.INFORM);
        spokenMsg.setConversationId(CHAT_ID);

        // Activate the GUI
        //#MIDP_EXCLUDE_BEGIN
        myGui = new AWTChatGui(this);
        //#MIDP_EXCLUDE_END

        /*#MIDP_INCLUDE_BEGIN
         myGui = new MIDPChatGui(this);
         #MIDP_INCLUDE_END*/

    }

    protected void takeDown() {
        if (myGui != null) {
            myGui.dispose();
        }
    }

    private void notifyParticipantsChanged() {
        myGui.notifyParticipantsChanged(getParticipants());
    }

    private void notifySpoken(String speaker, String sentence) {
        myGui.notifySpoken(speaker, sentence);
    }

    // ///////////////////////////////////////
    // Methods called by the interface
    // ///////////////////////////////////////
    public void handleSpoken(String s) {
        // Add a ChatSpeaker behaviour that INFORMs all participants about
        // the spoken sentence
        addBehaviour(new ChatSpeaker(this, s));
    }
    public ArrayList<AID> getParticipants() {
        ArrayList<AID> pp = new ArrayList<>();
        Iterator it = participants.iterator();
        int i = 0;
        System.out.println(participants.size());
        while (it.hasNext()) {
            pp.add((AID) it.next());
        }
        return pp;
    }

    // ///////////////////////////////////////
    // Private utility method
    // ///////////////////////////////////////
    private void handleUnexpected(ACLMessage msg) {
        if (logger.isLoggable(Logger.WARNING)) {
            logger.log(Logger.WARNING, "Mensagem nao esperada recebida de "
                    + msg.getSender().getName());
            logger.log(Logger.WARNING, "O Conteudo e: " + msg.getContent());
        }
    }

    /**
     * Inner class ParticipantsManager. This behaviour registers as a chat
     * participant and keeps the list of participants up to date by managing the
     * information received from the ChatManager agent.
     */
    class ParticipantsManager extends CyclicBehaviour {

        private static final long serialVersionUID = -4845730529175649756L;
        private MessageTemplate template;

        ParticipantsManager(Agent a) {
            super(a);
        }

        public void onStart() {
            // Subscribe as a chat participant to the ChatManager agent

            notifyParticipantsChanged();
            ACLMessage subscription = new ACLMessage(ACLMessage.SUBSCRIBE);
            subscription.setLanguage(codec.getName());
            subscription.setOntology(onto.getName());
            String convId = "C-" + myAgent.getLocalName();
            subscription.setConversationId(convId);
            subscription
                    .addReceiver(new AID(CHAT_MANAGER_NAME, AID.ISLOCALNAME));
            myAgent.send(subscription);
            // Initialize the template used to receive notifications
            // from the ChatManagerAgent
            template = MessageTemplate.MatchConversationId(convId);

        }

        public void action() {
            // Receives information about people joining and leaving
            // the chat from the ChatManager agent
            notifyParticipantsChanged();
            ACLMessage msg = myAgent.receive(template);
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    try {
                        AbsPredicate p = (AbsPredicate) myAgent
                                .getContentManager().extractAbsContent(msg);
                        if (p.getTypeName().equals(ChatOntology.JOINED)) {
                            // Get new participants, add them to the list of
                            // participants and notify the gui


                            AbsAggregate agg = (AbsAggregate) p
                                    .getAbsTerm(ChatOntology.JOINED_WHO);
                            if (agg != null) {

                                //myGui.notifyParticipantsJoined(myAgent);

                                Iterator it = agg.iterator();
                                while (it.hasNext()) {
                                    AbsConcept c = (AbsConcept) it.next();
                                    participants.add(BasicOntology
                                            .getInstance().toObject(c));

                                }
                            }
                            notifyParticipantsChanged();
                        }
                        if (p.getTypeName().equals(ChatOntology.LEFT)) {
                            // Get old participants, remove them from the list
                            // of participants and notify the gui
                            AbsAggregate agg = (AbsAggregate) p
                                    .getAbsTerm(ChatOntology.JOINED_WHO);
                            if (agg != null) {
                                notifyParticipantsChanged();
                                Iterator it = agg.iterator();
                                while (it.hasNext()) {
                                    AbsConcept c = (AbsConcept) it.next();
                                    participants.remove(BasicOntology
                                            .getInstance().toObject(c));
                                }
                            }
                            notifyParticipantsChanged();

                        }
                    } catch (Exception e) {
                        Logger.println(e.toString());
                        e.printStackTrace();
                    }
                } else {
                    handleUnexpected(msg);
                }
            } else {
                block();
            }
            notifyParticipantsChanged();
        }

    } // END of inner class ParticipantsManager

    /**
     * Inner class ChatListener. This behaviour registers as a chat participant
     * and keeps the list of participants up to date by managing the information
     * received from the ChatManager agent.
     */
    class ChatListener extends CyclicBehaviour {

        private static final long serialVersionUID = 741233963737842521L;
        private MessageTemplate template = MessageTemplate
                .MatchConversationId(CHAT_ID);

        ChatListener(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = myAgent.receive(template);
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    notifySpoken(msg.getSender().getLocalName(),
                            msg.getContent());
                } else {
                    handleUnexpected(msg);
                }
            } else {
                block();
            }
        }
    } // END of inner class ChatListener

    /**
     * Inner class ChatSpeaker. INFORMs other participants about a spoken
     * sentence
     */
    private class ChatSpeaker extends OneShotBehaviour {

        private static final long serialVersionUID = -1426033904935339194L;
        private String sentence;

        private ChatSpeaker(Agent a, String s) {
            super(a);
            sentence = s;

        }

        public void action() {
            spokenMsg.clearAllReceiver();
            Iterator it = participants.iterator();
            while (it.hasNext()) {
                spokenMsg.addReceiver((AID) it.next());
            }
            spokenMsg.setContent(sentence);
            notifySpoken(myAgent.getLocalName(), sentence);
            send(spokenMsg);
        }
    } // END of inner class ChatSpeaker

}

/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent
 * systems in compliance with the FIPA specifications. Copyright (C) 2000 CSELT
 * S.p.A.
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * **************************************************************
 */
package chat.ontology;

import jade.content.onto.BasicOntology;
import jade.content.onto.CFReflectiveIntrospector;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;

/**
 * Ontology containing concepts, predicates and actions used within the chat
 * application.
 *
 */
public class ChatOntology extends Ontology implements ChatVocabulary {

    // The singleton instance of this ontology
    private static Ontology theInstance = new ChatOntology();

    /**
     * Constructor
     */
    private ChatOntology() {

        super(ONTOLOGY_NAME, BasicOntology.getInstance(), new CFReflectiveIntrospector());

        try {
            add(new PredicateSchema(JOINED), Joined.class);
            add(new PredicateSchema(LEFT), Left.class);
            add(new PredicateSchema(SPOKEN), Spoken.class);
            //#J2ME_EXCLUDE_END
            PredicateSchema ps = (PredicateSchema) getSchema(JOINED);
            ps.add(JOINED_WHO, (ConceptSchema) getSchema(BasicOntology.AID), 1, ObjectSchema.UNLIMITED);

            ps = (PredicateSchema) getSchema(LEFT);
            ps.add(LEFT_WHO, (ConceptSchema) getSchema(BasicOntology.AID), 1, ObjectSchema.UNLIMITED);

            ps = (PredicateSchema) getSchema(SPOKEN);
            ps.add(SPOKEN_WHAT, getSchema(BasicOntology.STRING));
        } catch (OntologyException oe) {
            oe.printStackTrace();
        }
    }

    public static Ontology getInstance() {
        return theInstance;
    }

}

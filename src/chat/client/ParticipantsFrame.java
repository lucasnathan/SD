/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package chat.client;


import chat.client.agent.ChatClientAgent;
import jade.core.AID;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;

class ParticipantsFrame extends Frame {
	private AWTChatGui parent;
	private JList<ChatClientAgent> participants;
	private ChatClientAgent me;
	
        // Implementação do DefaultListCellRenderer para objeto ChatClientAgent
        // Utilizado para criar uma lista de agentes em tela, assim é possivel
        // a obtenção do Agente pela lista sem a necessidade de gambiarras.
        public class AgentListCellRenderer extends DefaultListCellRenderer {
            public Component getListCellRendererComponent(JList<?> list,
                                     Object value,
                                     int index,
                                     boolean isSelected,
                                     boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ChatClientAgent) {
                ChatClientAgent agent = (ChatClientAgent)value;
                setText(agent.getLocalName());
            }
            else if (value instanceof AID) {
                AID agent = (AID)value;
                setText(agent.getLocalName());
            }
            return this;
            }
        }
        
        
        DefaultListModel listModel;
        
    
	ParticipantsFrame(AWTChatGui parent, ChatClientAgent me) {
		this.parent = parent;
		this.me = me;
		
		setTitle("Participants: ");
		setSize(parent.getSize());
		
                listModel = new DefaultListModel();
                listModel.addElement(me); 
                
		participants = new JList<>(listModel);
                participants.setCellRenderer(new AgentListCellRenderer());
                
                
                
		add(participants, BorderLayout.CENTER);
				
		Button b = new Button("Close");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			} 
		} );
		
		add(b, BorderLayout.SOUTH);
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		} );
	}
	
	void refresh(ArrayList<AID> ss) {
                listModel.clear();
                listModel.addElement(me);
		if (ss != null) {
                    for(AID ag:ss){
                        listModel.addElement(ag);
                    }
		}
	}
	
}
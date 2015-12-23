/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent
 * systems in compliance with the FIPA specifications. Copyright (C) 2000 CSELT
 * S.p.A.  *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 2.1 of the License.  *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
****************************************************************
 */
package chat.client;

import chat.client.Start.NickNameDlg;
import chat.client.agent.ChatClientAgent;
import jade.core.AID;
import jade.core.Agent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class AWTChatGui extends Frame implements ChatGui {

    private ChatClientAgent myAgent;
    private TextField writeTf;
    private TextArea allTa;
    private ParticipantsFrame participantsFrame;

    private AWTChatGui parent;
    private JList<ChatClientAgent> participants;
    DefaultListModel listModel;


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
                ChatClientAgent agent = (ChatClientAgent) value;
                setText(agent.getLocalName());
            } else if (value instanceof AID) {
                AID agent = (AID) value;
                setText(agent.getLocalName());
            }
            return this;
        }
    }

    public AWTChatGui(ChatClientAgent a) {

        listModel = new DefaultListModel();
        listModel.addElement(myAgent);

        participants = new JList<>(listModel);
        participants.setCellRenderer(new AgentListCellRenderer());
        JScrollPane jScrollPane = new JScrollPane(participants);
        jScrollPane.setViewportView(participants);
        Panel frame = new Panel();



        myAgent = a;

        setTitle("Chat: " + myAgent.getLocalName());
        setSize(getProperSize(600,350));
        jScrollPane.setSize(getProperSize(300,300));
        Panel p = new Panel();
        p.setLayout(new BorderLayout());
        writeTf = new TextField();
        writeTf.setText(myAgent.getLocalName()+"Entrou no grupo");
        Button b2 = new Button("Enviar");
        allTa = new TextArea();
        allTa.setEditable(false);
        allTa.setBackground(Color.white);
        allTa.setSize(getProperSize(300,300));
        Button b = new Button("Participantes");
        
        // Implementações de Action Lisners
        
        // Implementação de KeyListner para enviar mensagens quando a tecla
        // enter for pressionada
        writeTf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        // Implementação do botão Send
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Implementação do WindowListner para enviar mensagem quando usuario
        // desconecta do servidor
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                myAgent.handleSpoken(myAgent.getLocalName() + " Saiu do grupo");
                myAgent.doDelete();
                NickNameDlg dlg = new NickNameDlg("Chat");
                setVisible(false);
            }
        });
        
        
        //Adiciona elementos de interface

        frame.add(allTa,BorderLayout.WEST);
        frame.add(jScrollPane,BorderLayout.EAST);
        p.add(writeTf, BorderLayout.CENTER);
        p.add(b2, BorderLayout.EAST);
        add(frame, BorderLayout.BEFORE_FIRST_LINE);
        add(p, BorderLayout.SOUTH);




        setVisible(true);
        sendMessage();

    }

    void refresh(ArrayList<AID> ss) {
        listModel.clear();
        listModel.addElement(myAgent);
        if (ss != null) {
            for (AID ag : ss) {
                listModel.addElement(ag);
            }
        }
    }

    public void sendMessage() {
        String s = writeTf.getText();
        if (s != null && !s.equals("")) {
            myAgent.handleSpoken(s);
            writeTf.setText("");
        }
    }

    public void notifyParticipantsChanged(ArrayList<AID> names) {

            this.refresh(names);
        //myAgent.handleSpoken(names.get(names.size()-1).getLocalName()+" Has joined the Group");
    }

    public void notifyParticipantsJoined(Agent name) {

        myAgent.handleSpoken(name.getLocalName()+" entrou no grupo");

    }
    public void notifySpoken(String speaker, String sentence) {
        allTa.append(speaker + ": " + sentence + "\n");
    }

    Dimension getProperSize(int maxX, int maxY) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width < maxX ? screenSize.width : maxX);
        int y = (screenSize.height < maxY ? screenSize.height : maxY);
        return new Dimension(x, y);
    }

    public void dispose() {
        participantsFrame.dispose();
        super.dispose();
    }

}

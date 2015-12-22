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

    public AWTChatGui(ChatClientAgent a) {
        myAgent = a;

        setTitle("Chat: " + myAgent.getLocalName());
        setSize(getProperSize(256, 320));
        Panel p = new Panel();
        p.setLayout(new BorderLayout());
        writeTf = new TextField();

        writeTf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        p.add(writeTf, BorderLayout.CENTER);
        Button b = new Button("Enviar");

        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        p.add(b, BorderLayout.EAST);
        add(p, BorderLayout.NORTH);

        allTa = new TextArea();
        allTa.setEditable(false);
        allTa.setBackground(Color.white);
        add(allTa, BorderLayout.CENTER);

        b = new Button("Participantes");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!participantsFrame.isVisible()) {
                    participantsFrame.setVisible(true);
                }
            }
        });
        add(b, BorderLayout.SOUTH);

        participantsFrame = new ParticipantsFrame(this, myAgent);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                myAgent.handleSpoken(myAgent.getLocalName() + " Saiu do grupo");
                myAgent.doDelete();
                NickNameDlg dlg = new NickNameDlg("Chat");
            }
        });

        setVisible(true);
    }

    public void sendMessage() {
        String s = writeTf.getText();
        if (s != null && !s.equals("")) {
            myAgent.handleSpoken(s);
            writeTf.setText("");
        }
    }

    public void notifyParticipantsChanged(ArrayList<AID> names) {
        if (participantsFrame != null) {
            participantsFrame.refresh(names);
        }
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

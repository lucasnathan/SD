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

import jade.MicroBoot;
import jade.core.MicroRuntime;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Startup class for the chat application
 */
public class Start extends MicroBoot {

    public static void main(String args[]) {
        MicroBoot.main(args);
        NickNameDlg dlg = new NickNameDlg("Chat");
    }

    private static boolean checkName(String name) {
        if (name == null || name.trim().equals("")) {
            return false;
        }
        return true;
    }

    public static class NickNameDlg extends Frame implements ActionListener {

        private TextField nameTf;
        private TextArea msgTa;

        NickNameDlg(String s) {
            super(s);

            setSize(getProperSize(256, 320));
            Panel p = new Panel();
            p.setLayout(new BorderLayout());
            nameTf = new TextField();
            p.add(nameTf, BorderLayout.CENTER);
            Button b = new Button("OK");
            b.addActionListener(this);
            p.add(b, BorderLayout.EAST);
            add(p, BorderLayout.NORTH);

            msgTa = new TextArea("Entre com um nickname\n");
            nameTf.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_ENTER) {
                        registerUser();
                    }
                }
            });
            msgTa.setEditable(false);
            msgTa.setBackground(Color.white);
            add(msgTa, BorderLayout.CENTER);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    MicroRuntime.stopJADE();
                    System.exit(1);
                }
            });

            showCorrect();
        }
        private void registerUser(){
            String name = nameTf.getText();
            if (!checkName(name)) {
                msgTa.append("Nickname Inválido\n");
            } else {
                try {
                    MicroRuntime.startAgent(name, "chat.client.agent.ChatClientAgent", null);
                    dispose();
                } catch (Exception ex) {
                    msgTa.append("Nickname já está em uso\n");
                }
            }
        }
        public void actionPerformed(ActionEvent e) {
            registerUser();
        }

        private void showCorrect() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize = getSize();
            int centerX = screenSize.width / 2;
            int centerY = screenSize.height / 2;
            setLocation(centerX - frameSize.width / 2, centerY - frameSize.height / 2);
            setVisible(true);
        }

        private Dimension getProperSize(int maxX, int maxY) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width < maxX ? screenSize.width : maxX);
            int y = (screenSize.height < maxY ? screenSize.height : maxY);
            return new Dimension(x, y);
        }
    }
}

//    POS-Tech
//    Based upon Openbravo POS
//
//    Copyright (C) 2007-2009 Openbravo, S.L.
//                       2012 Scil (http://scil.coop)
//
//    This file is part of POS-Tech.
//
//    POS-Tech is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    POS-Tech is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with POS-Tech.  If not, see <http://www.gnu.org/licenses/>.

package fr.pasteque.pos.sales.shared;

import fr.pasteque.pos.ticket.TicketInfo;

import java.awt.Color;
import java.awt.Insets;
import java.util.*;
import javax.swing.*;

import fr.pasteque.basic.BasicException;
import fr.pasteque.data.loader.ImageLoader;
import fr.pasteque.data.gui.MessageInf;
import fr.pasteque.pos.sales.*;
import fr.pasteque.pos.forms.*;
import fr.pasteque.pos.widgets.WidgetsBuilder;

public class JTicketsBagShared extends JTicketsBag {
    
    private String m_sCurrentTicket = null;
    private DataLogicReceipts dlReceipts = null;
    
    
    /** Creates new form JTicketsBagShared */
    public JTicketsBagShared(AppView app, TicketsEditor panelticket) {
        
        super(app, panelticket);
        
        dlReceipts = new DataLogicReceipts();
        
        initComponents();
    }
    
    public void activate() {
        
        // precondicion es que no tenemos ticket activado ni ticket en el panel
        
        m_sCurrentTicket = null;
        selectValidTicket();     
        
        // Authorization
        m_jDelTicket.setEnabled(m_App.getAppUserView().getUser().hasPermission("fr.pasteque.pos.sales.JPanelTicketEdits"));
       
        // postcondicion es que tenemos ticket activado aqui y ticket en el panel
    }
    
    public boolean deactivate() {
        
        // precondicion es que tenemos ticket activado aqui y ticket en el panel 
   
        saveCurrentTicket();
        
        m_sCurrentTicket = null;
        m_panelticket.setActiveTicket(null, null);       
        
        return true;
        
        // postcondicion es que no tenemos ticket activado ni ticket en el panel
    }
        
    public void deleteTicket() {          
        m_sCurrentTicket = null;
        selectValidTicket();      
    }
    
    protected JComponent getBagComponent() {
        return this;
    }
    
    protected JComponent getNullComponent() {
        return new JPanel();
    }
   
    private void saveCurrentTicket() {
        
        // save current ticket, if exists,
        if (m_sCurrentTicket != null) {
            try {
                dlReceipts.insertSharedTicket(m_sCurrentTicket, m_panelticket.getActiveTicket());
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }  
        }    
    }
    
    private void setActiveTicket(String id) throws BasicException{
          
        // BEGIN TRANSACTION
        TicketInfo ticket = dlReceipts.getSharedTicket(id);
        if (ticket == null)  {
            // Does not exists ???
            throw new BasicException(AppLocal.getIntString("message.noticket"));
        } else {
            dlReceipts.deleteSharedTicket(id);
            m_sCurrentTicket = id;
            m_panelticket.setActiveTicket(ticket, null);
        } 
        // END TRANSACTION                 
    }
    
    private void selectValidTicket() {
        
        try {
            List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();
            if (l.size() == 0) {
                newTicket();
            } else {
                setActiveTicket(l.get(0).getId());
            }
        } catch (BasicException e) {
            new MessageInf(e).show(this);
            newTicket();
        }    
    }    
    
    private void newTicket() {      
        
        saveCurrentTicket();

        TicketInfo ticket = new TicketInfo();    
        m_sCurrentTicket = UUID.randomUUID().toString(); // m_fmtid.format(ticket.getId());
        m_panelticket.setActiveTicket(ticket, null);      
    }
    

    private void initComponents() {
        m_jNewTicket = WidgetsBuilder.createButtonTooltip(ImageLoader.readImageIcon("tkt_new.png"),
                AppLocal.getIntString("Button.m_jNewTicket.toolTip"), WidgetsBuilder.SIZE_BIG);
        m_jDelTicket = WidgetsBuilder.createButtonTooltip(ImageLoader.readImageIcon("tkt_delete.png"),
                AppLocal.getIntString("Button.m_jDelTicket.toolTip"), WidgetsBuilder.SIZE_BIG);
        m_jListTickets = WidgetsBuilder.createButtonTooltip(ImageLoader.readImageIcon("tkt_list.png"),
                AppLocal.getIntString("Button.m_jListTickets.toolTip"), WidgetsBuilder.SIZE_BIG);


        m_jNewTicket.setFocusPainted(false);
        m_jNewTicket.setFocusable(false);
        m_jNewTicket.setRequestFocusEnabled(false);
        m_jNewTicket.setMargin(new Insets(0, 5, 0, 0));
        m_jNewTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jNewTicketActionPerformed(evt);
            }
        });

        add(m_jNewTicket);

        m_jDelTicket.setFocusPainted(false);
        m_jDelTicket.setFocusable(false);
        m_jDelTicket.setRequestFocusEnabled(false);
        m_jDelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelTicketActionPerformed(evt);
            }
        });

        add(m_jDelTicket);
        m_jListTickets.setFocusPainted(false);
        m_jListTickets.setFocusable(false);
        m_jListTickets.setRequestFocusEnabled(false);
        m_jListTickets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jListTicketsActionPerformed(evt);
            }
        });

        add(m_jListTickets);

    }

    private void m_jListTicketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jListTicketsActionPerformed

        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                try {
                    List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();

                    JTicketsBagSharedList listDialog = JTicketsBagSharedList.newJDialog(JTicketsBagShared.this);
                    String id = listDialog.showTicketsList(l); 

                    if (id != null) {
                        saveCurrentTicket();
                        setActiveTicket(id); 
                    }
                } catch (BasicException e) {
                    new MessageInf(e).show(JTicketsBagShared.this);
                    newTicket();
                }                    
            }
        });
        
    }//GEN-LAST:event_m_jListTicketsActionPerformed

    private void m_jDelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelTicketActionPerformed
        
        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannadelete"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            deleteTicket();
        }
        
    }//GEN-LAST:event_m_jDelTicketActionPerformed

    private void m_jNewTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jNewTicketActionPerformed

        newTicket();
        
    }//GEN-LAST:event_m_jNewTicketActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton m_jDelTicket;
    private javax.swing.JButton m_jListTickets;
    private javax.swing.JButton m_jNewTicket;
    // End of variables declaration//GEN-END:variables
    
}

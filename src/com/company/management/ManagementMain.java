package com.company.management;

import com.company.config.Configuration;
import com.company.exception.GetAllMessagesFromQueueException;
import com.company.exception.GetAllQueuesException;
import com.company.model.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jan Marti
 * Date: 07.11.13
 * Time: 09:40
 * To change this template use File | Settings | File Templates.
 */
public class ManagementMain implements ActionListener {

    DAO dao = new DAO(new PGDatasource());

    private int clientCount = -1;
    private int queueCount = -1;
    private int messageCount = -1;
    private static String labelPrefixClientCount = "Number of Clients: ";
    private static String labelPrefixQueueCount = "Number of Queues: ";
    private static String labelPrefixMessageCount = "Number of Messages: ";
    final JLabel labelClientCount = new JLabel(labelPrefixClientCount + "-1    ");
    final JLabel labelQueueCount = new JLabel(labelPrefixQueueCount + "-1    ");
    final JLabel labelMessageCount = new JLabel(labelPrefixMessageCount + "-1    ");

    static JFrame frame = null;
    JPanel pane = null;
    JTable table = null;
    JTable messageTable = null;
    QueueTableModel tableModel = null;

    public Component createComponents() {
        String[] columnNames = {"Queue Id", "Created"};

        tableModel = new QueueTableModel(this.getQueues());
        //table = new JTable(this.getQueues(), columnNames);
        table = new JTable(tableModel);

        JButton button = new JButton("Show messages in selected queue");
        button.setMnemonic(KeyEvent.VK_I);
        button.addActionListener(this);

         /* An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        pane = new JPanel(new GridLayout(0, 1));
        pane.add(table);
        pane.add(button);
        pane.setBorder(BorderFactory.createEmptyBorder(
                30, //top
                30, //left
                10, //bottom
                30) //right
        );

        int delay = 5000; //milliseconds
        new Timer(delay, this).start();

        return pane;
    }

    /*public void actionPerformed(ActionEvent e) {
        try {
            labelClientCount.setText(String.valueOf(dao.getClientCount()));
            labelQueueCount.setText(String.valueOf(dao.getQueueCount()));
            labelMessageCount.setText(String.valueOf(dao.getMessageCount()));
        } catch (GetCountException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        clientCount = -1;
        queueCount = -1;
        messageCount = -1;
        try {
            clientCount = dao.getClientCount();
            queueCount = dao.getQueueCount();
            messageCount = dao.getMessageCount();
        } catch (GetCountException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        labelClientCount.setText(labelPrefixClientCount + clientCount);
        labelQueueCount.setText(labelPrefixQueueCount + queueCount);
        labelMessageCount.setText(labelPrefixMessageCount + messageCount);
    }*/

    public void actionPerformed(ActionEvent e) {
        Object eventSource = e.getSource();

        // test.equals(new Timer(n));
        //AbstractButton button = (AbstractButton)e.getSource();
        //if(e.getActionCommand().equals(button.getActionCommand())) {
        if (!(eventSource instanceof Timer)) {
            int row = table.getSelectedRow();

            Object data = (Object)table.getValueAt(row, 0);
            int queueId = (Integer)data;

            String[] columnNames = {"Id", "Sender", "Receiver", "Queue", "Context", "Priority", "Created", "Message"};
            messageTable = new JTable(this.getMessagesFromQueue(queueId), columnNames);

            JOptionPane.showMessageDialog(null, new JScrollPane(messageTable));
        } else {
            String[] columnNames = {"Queue Id", "Created"};
            Object[][] queues = this.getQueues();
            tableModel.deleteAllRows();
            for (int i = 0; i < queues.length; i++) {
                tableModel.addRow(queues[i]);
            }
            tableModel.fireTableDataChanged();

            SwingUtilities.updateComponentTreeUI(frame);

            frame.invalidate();
            frame.validate();
            frame.repaint();
        }
    }

    public Object[][] getQueues() {
        List<Queue> queues = null;
        try {
            queues = dao.getAllQueues();
        } catch (GetAllQueuesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Object[][] res = null;
        if (queues != null) {
            res = new Object[queues.size()][2];
            for (int i = 0; i < queues.size(); i++) {
                for (int j = 0; j < 2; j++) {
                    if (j == 0) {
                        res[i][j] = queues.get(i).getId();
                    } else {
                        res[i][j] = queues.get(i).getCreated();
                    }
                }
            }
        }
        return res;
    }

    public Object[][] getMessagesFromQueue(int id) {
        List<Message> messages = null;
        try {
            messages = dao.getAllMessagesFromQueue(id);
        } catch (GetAllMessagesFromQueueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Object[][] res = null;
        if (messages != null) {
            res = new Object[messages.size()][8];
            for (int i = 0; i < messages.size(); i++) {
                for (int j = 0; j < 8; j++) {
                    switch (j) {
                        case 0:  res[i][j] = messages.get(i).getId();
                            break;
                        case 1:  res[i][j]= messages.get(i).getSender();
                            break;
                        case 2:  res[i][j] = messages.get(i).getReceiver();
                            break;
                        case 3:  res[i][j] = messages.get(i).getQueue();
                            break;
                        case 4:  res[i][j] = messages.get(i).getContext();
                            break;
                        case 5:  res[i][j] = messages.get(i).getPriority();
                            break;
                        case 6:  res[i][j] = messages.get(i).getCreated();
                            break;
                        case 7:  res[i][j] = messages.get(i).getMessage();
                            break;
                        default: res[i][j] = null;
                            break;
                    }
                }
            }
        }
        return res;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void main(String[] args)  {

        String configFilePath = "var/config.prop";
        Configuration.initConfig(configFilePath);

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("CoffeeMQ Management Console - Proudly serving you since '13");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = new Dimension(500, 200);
        frame.setMinimumSize(d);
        frame.setResizable(false);

        ManagementMain app = new ManagementMain();
        Component contents = app.createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);






        //Create and set up the panel
        /*JLabel lblNoClients = new JLabel("# Clients");
        JLabel lblNoMessages = new JLabel("# Messages");
        JLabel lblNoQueues = new JLabel("# Queues");

        JPanel panel = new JPanel(new GridLayout(15,15));

        panel.add(lblNoClients);
        panel.add(lblNoMessages);
        panel.add(lblNoQueues);

        frame.getContentPane().add(panel);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);*/
    }

    public class QueueTableModel extends AbstractTableModel {

        private List<Object[]> rows;

        public QueueTableModel(Object[][] data) {
            rows = new ArrayList<Object[]>();
            for (int i = 0; i < data.length; i++) {
                rows.add(data[i]);
            }
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Integer.class;
            }
            return Timestamp.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object[] row = rows.get(rowIndex);
            return row[columnIndex];
        }

       public void addRow(Object[] row) {
            int rowCount = getRowCount();
            rows.add(row);
            fireTableRowsInserted(rowCount, rowCount);
        }

        public void deleteAllRows() {
            rows.clear();
        }

    }

}


package com.amazonaws.samples;

import java.awt.EventQueue;
import java.awt.TextArea;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.Region;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ScrollPaneConstants;

public class Ec2AwsClass {

	private JFrame frame;
	private JTextArea txtrStatustextarea;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ec2AwsClass window = new Ec2AwsClass();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Ec2AwsClass() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1042, 584);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnShowRegion = new JButton("Show Region");
		btnShowRegion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateSecurityGroupApp2 obj = new CreateSecurityGroupApp2();
				List<Region> reg = obj.ListRegions();
			txtrStatustextarea.setText(reg.get(0).toString());	
			}
		});
		btnShowRegion.setBounds(29, 36, 114, 23);
		frame.getContentPane().add(btnShowRegion);
		
		JButton btnNewButton = new JButton("Show Status");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateSecurityGroupApp2 obj = new CreateSecurityGroupApp2();
				
				List<com.amazonaws.services.ec2.model.InstanceStatus> state= obj.GetInstanceStat();
				int i=0;
				int numcols = table.getModel().getColumnCount();
				while (state.size() > i) {
		        	
			          if(state.get(i).getInstanceState().getName().equals("running")) {
			        	 String output = "id-"+state.get(i).getInstanceId()+"\n"
			        			 +"state-"+state.get(i).getInstanceState()+"\n"
			        			 +"zone-"+state.get(i).getAvailabilityZone()+"\n"
			        			 +"system status-"+state.get(i).getSystemStatus()+"\n";
			        	 
			        	 Object [] tableRow= new Object[numcols];
			        	 tableRow[0] = state.get(i).getInstanceId();
			        	 tableRow[1] = state.get(i).getInstanceState();
			        	 tableRow[2] = state.get(i).getAvailabilityZone();
			        	 tableRow[3] = state.get(i).getSystemStatus();
			        	 
			        	 ((DefaultTableModel) table.getModel()).addRow(tableRow);
			        	
			             //txtrStatustextarea.setText(output);
			                
			            }
			            i++;
			       }
			
			}
		});
		btnNewButton.setBounds(29, 70, 137, 23);
		frame.getContentPane().add(btnNewButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(29, 104, 307, 104);
		frame.getContentPane().add(scrollPane);
		
		txtrStatustextarea = new JTextArea();
		scrollPane.setViewportView(txtrStatustextarea);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(346, 11, 670, 349);
		frame.getContentPane().add(scrollPane_1);
		
		DefaultTableModel modelo = new DefaultTableModel();
		table = new JTable(modelo);
		
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Id", "State", "Zone", "System Status"
			}
		));
		scrollPane_1.setViewportView(table);
		
		JButton btnStopInstance = new JButton("Stop Instance");
		btnStopInstance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object insID= table.getModel().getValueAt(table.getSelectedRow(), 0);
				CreateSecurityGroupApp2 obj = new CreateSecurityGroupApp2();
				obj.StopAnInstance(insID.toString());
			}
		});
		btnStopInstance.setBounds(29, 234, 125, 32);
		frame.getContentPane().add(btnStopInstance);
	}
	public JTextArea getTxtrStatustextarea() {
		return txtrStatustextarea;
	}
}

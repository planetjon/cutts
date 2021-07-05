package cutts.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import planetjon.espresso4j.*;
import static planetjon.espresso4j.Constructs.*;

import cutts.io.HttpRequest;
import cutts.util.SymbolMap;

/**
 * This class sets and display a bug reporting UI.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class BugReportDialog extends JDialog {
	private JLabel name, email, message;
	private JTextField namefield, emailfield;
	private JTextArea messagefield;
	private JButton accept, cancel;
	private String BUG_SERVER = "http://planetjon.ca/cutts/bugreport.php";

	public BugReportDialog(JFrame _owner) {
		super(_owner, "Bug Report", true);
		
		//Set up GUI		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints layoutconfig = new GridBagConstraints();
		getContentPane().setLayout(layout);

		name = new JLabel("Name:");
		layoutconfig.gridx = 0;
		layoutconfig.gridy = 0;
		layoutconfig.weightx = 0;
		layoutconfig.weighty = 1;
		layoutconfig.gridwidth = 1;
		layoutconfig.gridheight =1;
		layoutconfig.insets = new Insets(1, 1, 1, 1);
		layoutconfig.fill = GridBagConstraints.HORIZONTAL;
		layoutconfig.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(name, layoutconfig);
		getContentPane().add(name);
		
		namefield = new JTextField();
		layoutconfig.gridx = 1;
		layoutconfig.gridy = 0;
		layoutconfig.weightx = 3;
		layoutconfig.weighty = 1;
		layoutconfig.gridwidth = 1;
		layoutconfig.gridheight =1;
		layoutconfig.insets = new Insets(1, 1, 1, 1);
		layoutconfig.fill = GridBagConstraints.HORIZONTAL;
		layoutconfig.anchor = GridBagConstraints.WEST;
		layout.setConstraints(namefield, layoutconfig);
		getContentPane().add(namefield);
		
		email = new JLabel("Email Address:");
		layoutconfig.gridx = 0;
		layoutconfig.gridy = 1;
		layoutconfig.weightx = 1;
		layoutconfig.weighty = 1;
		layoutconfig.gridwidth = 1;
		layoutconfig.gridheight =1;
		layoutconfig.insets = new Insets(1, 1, 1, 1);
		layoutconfig.fill = GridBagConstraints.HORIZONTAL;
		layoutconfig.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(email, layoutconfig );
		getContentPane().add(email);
		
		emailfield = new JTextField();
		layoutconfig.gridx = 1;
		layoutconfig.gridy = 1;
		layoutconfig.weightx = 3;
		layoutconfig.weighty = 1;
		layoutconfig.gridwidth = 1;
		layoutconfig.gridheight =1;
		layoutconfig.insets = new Insets(1, 1, 1, 1);
		layoutconfig.fill = GridBagConstraints.HORIZONTAL;
		layoutconfig.anchor = GridBagConstraints.WEST;
		layout.setConstraints(emailfield, layoutconfig);
		getContentPane().add(emailfield);
		
		message = new JLabel("Bug:");
		layoutconfig.gridx = 0;
		layoutconfig.gridy = 2;
		layoutconfig.weightx = 1;
		layoutconfig.weighty = 1;
		layoutconfig.gridwidth = 1;
		layoutconfig.gridheight =1;
		layoutconfig.insets = new Insets(1, 1, 1, 1);
		layoutconfig.fill = GridBagConstraints.HORIZONTAL;
		layoutconfig.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(message, layoutconfig);
		getContentPane().add(message);
		
		messagefield = new JTextArea();
		JScrollPane messagepane = new JScrollPane(messagefield);
		layoutconfig.gridx = 1;
		layoutconfig.gridy = 2;
		layoutconfig.weightx = 3;
		layoutconfig.weighty = 1;
		layoutconfig.gridwidth = 1;
		layoutconfig.gridheight = 7;
		layoutconfig.insets = new Insets(1, 1, 1, 1);
		layoutconfig.fill = GridBagConstraints.BOTH;
		layoutconfig.anchor = GridBagConstraints.WEST;
		layout.setConstraints(messagepane, layoutconfig);
		getContentPane().add(messagepane);
		
		accept = new JButton("Submit");
		layoutconfig.gridx = 0;
		layoutconfig.gridy = 9;
		layoutconfig.weightx = 1;
		layoutconfig.weighty = 1;
		layoutconfig.gridwidth = 1;
		layoutconfig.gridheight =1;
		layoutconfig.insets = new Insets(20, 3, 1, 1);
		layoutconfig.fill = GridBagConstraints.NONE;
		layoutconfig.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(accept, layoutconfig);
		getContentPane().add(accept);
		
		cancel = new JButton("Cancel");
		layoutconfig.gridx = 1;
		layoutconfig.gridy = 9;
		layoutconfig.weightx = 1;
		layoutconfig.weighty = 1;
		layoutconfig.gridwidth = 1;
		layoutconfig.gridheight =1;
		layoutconfig.insets = new Insets(20, 1, 1, 3);
		layoutconfig.fill = GridBagConstraints.NONE;
		layoutconfig.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(cancel, layoutconfig);
		getContentPane().add(cancel);

		setSize(350, 350);
		setLocationRelativeTo(_owner);
        setResizable(false);
        
		accept.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					acceptClicked();
				}
		});
	
		cancel.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancelClicked();
				}
		});

		addWindowListener( new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					cancelClicked();
				}
		});
	}

	private void acceptClicked() {
		List<Pair<String, String>> vars = Constructs.list();
		String content = "";
		vars.add( pair( "name", namefield.getText() ) );
		vars.add( pair( "email", emailfield.getText() ) );
		vars.add( pair( "message", messagefield.getText() ) );

		try {
			content = HttpRequest.post(BUG_SERVER, null, vars);
		}
		catch (Exception e) {
			CUTTSui.errorMessage( SymbolMap.lookupSymbol("CUTTS_BUG_REPORT_FAILURE") );
		}

		CUTTSui.infoMessage(content);
		setVisible(false);
		dispose();
	}
	
	private void cancelClicked() {
		setVisible(false);
		dispose();
	}
}
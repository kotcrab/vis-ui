
package pl.kotcrab.vis.sceneeditor.sample.desktop;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SampleSelector extends JDialog {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	public SampleSelector (final SampleSelected sampleSelected) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 137);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));

		JButton btnSample1 = new JButton("Sample Scene");
		btnSample1.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				sampleSelected.sampleSelected(0);
				dispose();
			}
		});
		contentPane.add(btnSample1);

		JButton btnSample2 = new JButton("Sample Scene with KotcrabText");
		btnSample2.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				sampleSelected.sampleSelected(1);
				dispose();
			}
		});
		contentPane.add(btnSample2);

		JButton btnSample3 = new JButton("Scene2d Sample");
		btnSample3.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				sampleSelected.sampleSelected(2);
				dispose();
			}
		});
		contentPane.add(btnSample3);

		setVisible(true);

	}
}

interface SampleSelected {
	public void sampleSelected (int sampleId);
}

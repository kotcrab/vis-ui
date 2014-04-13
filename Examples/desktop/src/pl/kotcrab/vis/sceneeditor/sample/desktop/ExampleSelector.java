/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor.sample.desktop;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class ExampleSelector extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	public ExampleSelector(final ExampleSelected sampleSelected)
	{
		setTitle("Select example");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 137);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnExample1 = new JButton("Example scene");
		btnExample1.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				sampleSelected.exampleSelected(0);
				dispose();
			}
		});
		contentPane.add(btnExample1);
		
		JButton btnExample2 = new JButton("Example scene with KotcrabText");
		btnExample2.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				sampleSelected.exampleSelected(1);
				dispose();
			}
		});
		contentPane.add(btnExample2);
		
		JButton btnExample3 = new JButton("Scene2d example");
		btnExample3.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				sampleSelected.exampleSelected(2);
				dispose();
			}
		});
		contentPane.add(btnExample3);
		
		setVisible(true);
		
	}
}

interface ExampleSelected
{
	public void exampleSelected(int exampleId);
}

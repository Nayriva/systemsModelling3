package org.yakindu.scr.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.yakindu.scr.digitalwatch.DigitalwatchStatemachine;
import org.yakindu.scr.digitalwatch.IDigitalwatchStatemachine.SCIButtons;
import org.yakindu.scr.digitalwatch.IDigitalwatchStatemachine.SCIDisplayOperationCallback;
import org.yakindu.scr.digitalwatch.IDigitalwatchStatemachine.SCILogicUnitOperationCallback;

public class DigitalWatchViewImpl implements MouseListener, SCIDisplayOperationCallback {
	private JPanel displayPanel;
	private JLabel timeLabel;
	private JLabel dateLabel;
	private JLabel alarmIcon;
	
	private SCIButtons listener;
	private SCILogicUnitOperationCallback model;
	private DigitalwatchStatemachine sm;
    
	public DigitalWatchViewImpl(SCILogicUnitOperationCallback model, SCIButtons listener, DigitalwatchStatemachine sm) {
		this.model = model;
		this.listener = listener;
		this.sm = sm;
		initializeGUI();
	}
    
	@Override
	public void refreshTimeDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				timeLabel.setText(model.getTime());
			}
		});
	}
	
	@Override
	public void refreshChronoDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				timeLabel.setText(model.getChrono());
			}
		});
	}
	
	@Override
	public void refreshDateDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dateLabel.setText(model.getDate());
			}
		});
	}
    
	@Override
	public void refreshAlarmDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (model.getAlarm())
					alarmIcon.setText("\u266A");
				else
					alarmIcon.setText(" ");
			}
		});
	}
	
	@Override
	public void showSelection() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				timeLabel.setText(model.getTimeLabelAsForShowing());
				dateLabel.setText(model.getDateLabelAsForShowing());
			}
		});
	}
    
	@Override
	public void hideSelection() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				timeLabel.setText(model.getTimeLabelAsForHiding());
				dateLabel.setText(model.getDateLabelAsForHiding());
			}
		});
	}
	
	@Override
	public void setIndiglo() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				displayPanel.setBackground(Color.CYAN);
			}
		});
	}
	@Override
	public void unsetIndiglo() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				displayPanel.setBackground(Color.WHITE);
			}
		});
	}
	
	private void initializeGUI() {
		JLayeredPane lpane = new JLayeredPane();
		final Image image = new ImageIcon("watch.gif").getImage();
		@SuppressWarnings("serial")
		JPanel imagePanel = new JPanel() {
			public void paintComponent(Graphics g) { g.drawImage(image, 0, 0, null); }
		};
		Dimension size = new Dimension(image.getWidth(null)+15, image.getHeight(null)+22);
		imagePanel.setPreferredSize(size);
		imagePanel.setMinimumSize(size);
		imagePanel.setMaximumSize(size);
		imagePanel.setSize(size);
        imagePanel.setLayout(null);
        
		imagePanel.addMouseListener(this);
		
		displayPanel = new JPanel();
		displayPanel.setBounds(52,97,122,54);
		displayPanel.setBackground(Color.WHITE);
		displayPanel.setForeground(Color.BLACK);
        
		dateLabel = new JLabel();
		dateLabel.setBounds(94,100,60,20);
		dateLabel.setFont(new Font("Courier", Font.PLAIN, 12));
        
		timeLabel = new JLabel();
		timeLabel.setBounds(84,120,80,20);
		timeLabel.setFont(new Font("Courier", Font.PLAIN, 14));
		
		alarmIcon = new JLabel();
		alarmIcon.setBounds(154,97,20,20);
		alarmIcon.setVisible(true);
		
		lpane.add(imagePanel, 0, 0);
		lpane.add(displayPanel, 1, 0);
		lpane.add(dateLabel, 3, 0);
		lpane.add(timeLabel, 3, 0);
		lpane.add(alarmIcon, 3, 0);
		
		JFrame frame = new JFrame();
	    frame.getContentPane().add(lpane);
	    frame.setPreferredSize(imagePanel.getPreferredSize());
	    frame.setResizable(false);
	    frame.pack();
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	}
    
	// ==========================================================
	// Implementation of MouseEventListener interface
	// ==========================================================
	Rectangle[] buttonBounds = {
    new Rectangle(4, 60, 10, 11),
    new Rectangle(209, 60, 10, 11),
    new Rectangle(4, 162, 10, 11),
    new Rectangle(213, 162, 10, 11)
    };
	String[] buttonPressedEvents = {
    "topLeftPressed", "topRightPressed",
    "bottomLeftPressed", "bottomRightPressed"
    };
	String[] buttonReleasedEvents = {
    "topLeftReleased", "topRightReleased",
    "bottomLeftReleased", "bottomRightReleased"
    };
    
	@Override
	public void mousePressed(MouseEvent e) {
		Point point = e.getPoint();
		for (int i = 0; i < buttonBounds.length; i++) {
			if (buttonBounds[i].contains(point)) {
				notifyEvent(buttonPressedEvents[i]);
				break;
			}
		}
	}
    
	@Override
	public void mouseReleased(MouseEvent e) {
		Point point = e.getPoint();
		for (int i = 0; i < buttonBounds.length; i++) {
			if (buttonBounds[i].contains(point)) {
				notifyEvent(buttonReleasedEvents[i]);
				break;
			}
		}
	}
    
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
    
	// ==========================================================
	// Management of Event listeners (Interface with the State Machine)
	// ==========================================================
	private void notifyEvent(String event) {
		if (event.equals("topLeftPressed"))
			listener.raiseTopLeftPressed();
		else if (event.equals("topLeftReleased"))
			listener.raiseTopLeftReleased();
		else if (event.equals("topRightPressed"))
			listener.raiseTopRightPressed();
		else if (event.equals("topRightReleased"))
			listener.raiseTopRightReleased();
		else if (event.equals("bottomLeftPressed"))
			listener.raiseBottomLeftPressed();
		else if (event.equals("bottomLeftReleased"))
			listener.raiseBottomLeftReleased();
		else if (event.equals("bottomRightPressed"))
			listener.raiseBottomRightPressed();
		else if (event.equals("bottomRightReleased"))
			listener.raiseBottomRightReleased();
		sm.runCycle();
	}
}

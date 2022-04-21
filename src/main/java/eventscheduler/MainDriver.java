package eventscheduler;


import javax.swing.*;
import java.awt.*;

/**
 * The class UserInterface is a JFrame the contains the two JPanels
 * CalendarWithAdjustmentButtonsPanel on the center and AddEditRemoveSearchPanel
 * on the left. The UserInterface is the main user interface with with the user
 * interacts with the rest of the event scheduler system.
 * 
 * 
 * 
 * 
 * 
 * @author Ibrahim
 *
 */

public class MainDriver {

	JFrame mainJFrame = new JFrame();

	

	public static void main(String[] args) {
		MainDriver mainDriver = new MainDriver();
		mainDriver.run();

	}

	public void run() {

		// A sole EventOrganizer instance is created
		EventOrganizer eventOrganizer = new EventOrganizer();

		System.out.println("Welcome to the event scheduler program! \n" );
		
		CalendarPanel calendar = new CalendarPanel(eventOrganizer);
		mainJFrame.getContentPane().add(BorderLayout.CENTER, calendar);

		UserInterfacePanel addEditRemoveSearchPanel = new UserInterfacePanel(eventOrganizer);
		mainJFrame.getContentPane().add(BorderLayout.EAST, addEditRemoveSearchPanel);

		mainJFrame.setTitle("Event Scheduler");

		mainJFrame.setResizable(true);

		// Set maximum height and width
		mainJFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		mainJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainJFrame.setVisible(true);

	}

}

package eventscheduler;

import java.util.*;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * 
 * The class EventOrganizer manages the events. The class allows other classes
 * to create events, modify them, and delete them. The EventOrganizer also
 * allows the searching the events between two dates. The EventOrganizer stores
 * the events in a KdTree and a HashMap. EventOrganizer serializes the events
 * and deserializes them.
 * 
 * 
 * 
 * 
 * 
 * @author Ibrahim
 *
 */

public class EventOrganizer {

	// Stores a hashmap with of the events with the key being the event number
	// and the value the event itself
	private final HashMap<Integer, Event> hashMapOfEvents;

	// Stores the events in the kd tree for looking up the events by
	// starting and ending dates
	private final KdTree kdTreeOfEvents;

	// Stores information about events;
	private final EventInformation eventInformation;

	// For serialization and deserialization, save the files names
	public static final String hashMapOfEventsFileString = "hashMapOfEventsFile.ser";
	public static final String kdTreeOfEventsFileString = "kdTreeOfEventsFile.ser";
	public static final String eventInforamtionFileString = "eventInforamtionFile.ser";

	public EventOrganizer() {
		super();
		hashMapOfEvents = new HashMap<Integer, Event>();
		kdTreeOfEvents = new KdTree();
		eventInformation = new EventInformation();

		// Deserialize
		this.readHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
				eventInforamtionFileString);

	}

	/**
	 * 
	 * @param line
	 * @return boolean true if an event was created otherwise false
	 * 
	 *         The method takes line as a parameter and parses it into tokens which
	 *         are used to create an that is inserted inside the list listOfEvents
	 * 
	 *         Expected format of line CreateEvent <<eventName>> <<Description>>
	 *         <<beginningTime>> <<endingTime>> <<month/day/year>>
	 *         <<month/day/year>>
	 */
	public Event parse(final String line) {

		String[] tokens = line.split(" ");

		Event createdEvent = null;

		if (tokens.length != 8)
			return null;

		if (tokens[0].equals("CreateEvent")) {
			String[] monthDayYear1 = tokens[5].split("//");
			String[] monthDayYear2 = tokens[6].split("//");

			createdEvent = createEvent(tokens[1], tokens[2], tokens[3], tokens[4], Integer.parseInt(monthDayYear1[0]),
					Integer.parseInt(monthDayYear1[1]), Integer.parseInt(monthDayYear1[2]),
					Integer.parseInt(monthDayYear2[0]), Integer.parseInt(monthDayYear2[1]),
					Integer.parseInt(monthDayYear2[2]), Priority.valueOf(tokens[7]));

		}

		return createdEvent;
	}

	/**
	 * Creates an event with the parameters
	 * 
	 * @param eventName        event name
	 * @param eventDescription event description
	 * @param beginningTime    int
	 * @param endingTime       int
	 * @param beginningDay     int
	 * @param beginningMonth   int
	 * @param beginningYear    int
	 * @param endingDay        int
	 * @param endingMonth      int
	 * @param endingYear       int
	 * @param priority         Must be of type enum Priority
	 * @return true if event created
	 */

	public Event createEvent(final String eventName, final String eventDescription, final String beginningTime,
			final String endingTime, final int beginningDay, final int beginningMonth, final int beginningYear,
			final int endingDay, final int endingMonth, final int endingYear, final Priority priority)

	{

		Calendar tempBeginningCalendar = Calendar.getInstance();
		tempBeginningCalendar.set(beginningYear, beginningMonth - 1, beginningDay,
				Integer.parseInt(beginningTime.substring(0, 2)), Integer.parseInt(beginningTime.substring(3, 5)), 0);

		Calendar tempEndingCalendar = Calendar.getInstance();
		tempEndingCalendar.set(endingYear, endingMonth - 1, endingDay, Integer.parseInt(endingTime.substring(0, 2)),
				Integer.parseInt(endingTime.substring(3, 5)), 0);

		Event event = new Event(eventName, eventDescription, tempBeginningCalendar, tempEndingCalendar, priority,
				eventInformation);

		int tempEventNumber = event.getEventNumber();

		hashMapOfEvents.put(tempEventNumber, event);

		kdTreeOfEvents.insertEventIntoTree(event);

		// Serialize
		this.writeHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
				eventInforamtionFileString);
		return event;

	}

	/**
	 * Creates an event with the parameters
	 * 
	 * @param eventName        event name
	 * @param eventDescription event description
	 * @param beginningTime    Must be in format hh:mm
	 * @param endingTime       Must be in format hh:mm
	 * @param beginningDate    Must be in format mm/dd/yyyy
	 * @param endingDate       Must be in format mm/dd/yyyy
	 * @param priority         Must be of type enum Priority
	 * @return true if event is created
	 */

	public Event createEvent(final String eventName, final String eventDescription, final String beginningTime,
			final String endingTime, final String beginningDate, final String endingDate, final Priority priority)

	{

		Calendar tempBeginningCalendar = Calendar.getInstance();

		tempBeginningCalendar.set(Integer.parseInt(beginningDate.substring(6, 10)),
				Integer.parseInt(beginningDate.substring(0, 2)) - 1, Integer.parseInt(beginningDate.substring(3, 5)),
				Integer.parseInt(beginningTime.substring(0, 2)), Integer.parseInt(beginningTime.substring(3, 5)), 0);

		Calendar tempEndingCalendar = Calendar.getInstance();

		tempEndingCalendar.set(Integer.parseInt(endingDate.substring(6, 10)),
				Integer.parseInt(endingDate.substring(0, 2)) - 1, Integer.parseInt(endingDate.substring(3, 5)),
				Integer.parseInt(endingTime.substring(0, 2)), Integer.parseInt(endingTime.substring(3, 5)), 0);

		Event event = new Event(eventName, eventDescription, tempBeginningCalendar, tempEndingCalendar, priority,
				eventInformation);

		int eventEventNumber = event.getEventNumber();

		hashMapOfEvents.put(eventEventNumber, event);

		kdTreeOfEvents.insertEventIntoTree(event);

		// Serialize
		this.writeHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
				eventInforamtionFileString);
		return event;

	}

	/**
	 * Returns the number of events that are stored
	 * 
	 * @return
	 */
	public int getNumberOfEvents() {
		return hashMapOfEvents.size();
	}

	/**
	 * Returns the event with the eventNumber
	 * 
	 * @param eventNumber
	 * @return
	 */
	public Event getEventFromNumber(int eventNumber) {
		return hashMapOfEvents.get(eventNumber);
	}

	/**
	 * Edits priority for the event with the eventNumber
	 * 
	 * @param eventNumber
	 * @param p
	 */
	public void editPriority(final int eventNumber, Priority p) {

		hashMapOfEvents.get(eventNumber).setPriority(p);

		kdTreeOfEvents.searchForEvent(hashMapOfEvents.get(eventNumber)).setPriority(p);

		// Serialize the new information (after editing)
		this.writeHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
				eventInforamtionFileString);

	}

	/**
	 * Edits event name for the event with eventNumber
	 * 
	 * @param eventNumber
	 * @param name
	 */
	public void editEventName(final int eventNumber, String name) {

		hashMapOfEvents.get(eventNumber).setEventName(name);

		kdTreeOfEvents.searchForEvent(hashMapOfEvents.get(eventNumber)).setEventName(name);

		// Serialize the new information (after editing)
		this.writeHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
				eventInforamtionFileString);

	}

	/**
	 * Edits event name for the event with eventNumber
	 * 
	 * @param eventNumber
	 * @param description
	 */

	public void editEventDescription(final int eventNumber, String description) {

		hashMapOfEvents.get(eventNumber).setEventDescription(description);

		kdTreeOfEvents.searchForEvent(hashMapOfEvents.get(eventNumber)).setEventDescription(description);

		// the tree will be updated by itself, because the same event pointers
		// exist in hashMapOfEvents and kdTreeOfEvents

		// Serialize the new information (after editing)
		this.writeHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
				eventInforamtionFileString);

	}

	/**
	 * Edits the beginning date of the event with the eventNumber
	 * 
	 * @param eventNumber
	 * @param cal
	 */
	public void editBeginningDate(final int eventNumber, Calendar cal) {

		kdTreeOfEvents.deleteEvent(hashMapOfEvents.get(eventNumber));

		hashMapOfEvents.get(eventNumber).setEventBeginningTimeCalendar(cal);

		kdTreeOfEvents.insertEventIntoTree(hashMapOfEvents.get(eventNumber));

		// Serialize the new information (after editing)
		this.writeHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
				eventInforamtionFileString);

	}

	/**
	 * Edits the ending date of the event with the eventNumber
	 * 
	 * @param eventNumber
	 * @param cal
	 */
	public void editEndingDate(final int eventNumber, Calendar cal) {

		kdTreeOfEvents.deleteEvent(hashMapOfEvents.get(eventNumber));

		hashMapOfEvents.get(eventNumber).setEventEndingTimeCalendar(cal);

		kdTreeOfEvents.insertEventIntoTree(hashMapOfEvents.get(eventNumber));

		// Serialize the new information (after editing)
		this.writeHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
				eventInforamtionFileString);

	}

	/**
	 * Delete All events that are stored
	 * 
	 * @return true if all events have been deleted
	 */
	public boolean deleteAllEvents() {
		hashMapOfEvents.clear();
		kdTreeOfEvents.clear();
		eventInformation.currentEventNumbers.clear();
		eventInformation.deletedEventNumbers.clear();
		eventInformation.eventCounter = 0;

		// Serialize the new information (after editing)
		this.writeHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
				eventInforamtionFileString);

		return true;
	}

	/**
	 * Returns an ArrayList<Event> of all events that are stored
	 * 
	 * @return
	 */
	public ArrayList<Event> getAllEvents() {

		return kdTreeOfEvents.getAllEvents();
	}

	/**
	 * Returns an ArrayList of the events with the parameter name
	 * <p>
	 * Note: names may not be unique among events
	 * <p>
	 * 
	 * @param name
	 * @return
	 */
	public ArrayList<Event> getEventsListFromName(final String name) {
		ArrayList<Event> list = new ArrayList<Event>();

		for (Event e : hashMapOfEvents.values())

		{
			if (e.getEventName().equals(name))
				list.add(e);
		}

		return list;

	}

	/**
	 * Deletes the event with event number eventNumber
	 * <p>
	 * updates all hashmap and kdTree. Also serializes after the deletion.
	 * 
	 * @param eventNumber
	 * @return
	 */
	public boolean deleteEvent(final int eventNumber) {
		Event toBeDeleted = hashMapOfEvents.get(eventNumber);

		if (hashMapOfEvents.remove(eventNumber) != null) {

			kdTreeOfEvents.deleteEvent(toBeDeleted);
			eventInformation.deletedEventNumbers.add(eventNumber);

			// Update eventInformation
			eventInformation.eventCounter--;

			// Serialize the new information (after deletion)
			this.writeHashMapAndKdTreeAndEventInformation(hashMapOfEventsFileString, kdTreeOfEventsFileString,
					eventInforamtionFileString);
			return true;
		}

		return false;
	}

	/**
	 * Returns an ArrayList<Event> of the events that overlap (even partially) the
	 * range specified by [beginningCal,endingCal]
	 * <p>
	 * For example, beginningCal is 2017/7/3 03:00 and endingCal is 2017/7/25 03:00
	 * <br>
	 * the following will be returned: <br>
	 * 2017/7/3/ 3:00 - 2017/8/3/ 03:00 <br>
	 * 2017/7/2/ 02:00 - 2017/7/5 03:00<br>
	 * 2017/7/25/ 01:00 - 2017/8/7 03:00<br>
	 * 
	 * However 2017/8/3 03:00 - 2017/8/7 03:00 will NOT be returned (becuase it lies
	 * outside the range)
	 * <p>
	 * 
	 * @param beginningCal
	 * @param endingCal
	 * @return
	 */
	public ArrayList<Event> getEventsBetweenTwoDates(Calendar beginningCal, Calendar endingCal) {
		return kdTreeOfEvents.searchKdTreeForEventsBetweenTwoDates(beginningCal, endingCal);

	}

	/**
	 * Returns an ArrayList<Event> of the events that overlap a SINGLE date
	 * <p>
	 * For example, cal is 2017/7/3 03:00 <br>
	 * the following will be returned: <br>
	 * 2017/7/3/ 3:00 - 2017/8/3/ 03:00 <br>
	 * 2017/7/2/ 02:00 - 2017/7/5 03:00<br>
	 * 2017/7/25/ 01:00 - 2017/8/7 03:00<br>
	 * 
	 * However 2017/8/3 03:00 - 2017/8/7 03:00 will NOT be returned (because it does
	 * not overlap the date)
	 * <p>
	 * 
	 * @param cal
	 * @return
	 */
	public ArrayList<Event> getEventsOnOneDate(Calendar cal) {

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		cal1.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		cal2.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 0);

		return kdTreeOfEvents.searchKdTreeForEventsBetweenTwoDates(cal1, cal2);

	}

	/**
	 * deserializes the hashMapOfEvents and the kdTreeOfEvents and eventInformation
	 * from files file1, file2 , and file3 respectively.
	 * <p>
	 * 
	 * @param file1
	 * @param file2
	 * @param file3
	 */
	private void readHashMapAndKdTreeAndEventInformation(String file1, String file2, String file3) {
		try {
			ObjectInputStream oishashMapOfEvents = new ObjectInputStream(new FileInputStream(file1));

			@SuppressWarnings("unchecked")
			HashMap<Integer, Event> hm = (HashMap<Integer, Event>) oishashMapOfEvents.readObject();

			hashMapOfEvents.putAll(hm);

			oishashMapOfEvents.close();

			ObjectInputStream oiskdTreeOfEvents = new ObjectInputStream(new FileInputStream(file2));

			KdTree kd = (KdTree) oiskdTreeOfEvents.readObject();

			kdTreeOfEvents.insertIntoKdTree(kd.getAllEvents());

			oiskdTreeOfEvents.close();

			ObjectInputStream oisEventInformation = new ObjectInputStream(new FileInputStream(file3));

			EventInformation ei = (EventInformation) oisEventInformation.readObject();

			// Make the eventInformation get all values from the stored
			// (deserialized) ei
			eventInformation.currentEventNumbers.addAll(ei.currentEventNumbers);
			eventInformation.deletedEventNumbers.addAll(ei.deletedEventNumbers);
			eventInformation.eventCounter = ei.eventCounter;

			oisEventInformation.close();

		} catch (FileNotFoundException e) {

			// don't do anything

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Serializes the hashMapOfEvents and the kdTreeOfEvents and eventInformation to
	 * files file1, file2 , and file3 respectively.
	 * <p>
	 * 
	 * @param file1
	 * @param file2
	 * @param file3
	 */
	private boolean writeHashMapAndKdTreeAndEventInformation(String file1, String file2, String file3) {
		try {
			ObjectOutputStream ooshashMapOfEvents = new ObjectOutputStream(new FileOutputStream(file1));

			ooshashMapOfEvents.writeObject(hashMapOfEvents);

			ooshashMapOfEvents.close();

			ObjectOutputStream ooskdTreeOfEvents = new ObjectOutputStream(new FileOutputStream(file2));

			ooskdTreeOfEvents.writeObject(kdTreeOfEvents);

			ooskdTreeOfEvents.close();

			ObjectOutputStream oosEventInformation = new ObjectOutputStream(new FileOutputStream(file3));

			oosEventInformation.writeObject(this.eventInformation);

			oosEventInformation.close();

		} catch (IOException e) {

			e.printStackTrace();
			return false;
		}

		return true;

	}
}

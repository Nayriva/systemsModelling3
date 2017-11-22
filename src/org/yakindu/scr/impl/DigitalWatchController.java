package org.yakindu.scr.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.yakindu.scr.digitalwatch.IDigitalwatchStatemachine.SCILogicUnit;
import org.yakindu.scr.digitalwatch.IDigitalwatchStatemachine.SCILogicUnitOperationCallback;

public class DigitalWatchController implements SCILogicUnitOperationCallback {
	enum EditionMode {NONE, TIME, ALARM};
	private int timeHour;
	private int timeMinute;
	private int timeSecond;
	private int alarmHour;
	private int alarmMinute;
	private int alarmSecond;
	private int chronoMinute;
	private int chronoSecond;
	private int chronoCentisec;
	private int day;
	private int month;
	private int year;
	private boolean alarm;
	private EditionMode editionMode;
	private int currentSelection;
	private SCILogicUnit eventListener;
	
	public DigitalWatchController(SCILogicUnit eventListener) {
		this.eventListener = eventListener;
		readSystemTime();
		resetChrono();
		alarm = false;
		editionMode = EditionMode.NONE;
		currentSelection = 0;
	}
	
	public void readSystemTime() {
		Calendar calendar = Calendar.getInstance();
		timeHour = calendar.get(Calendar.HOUR_OF_DAY);
		timeMinute = calendar.get(Calendar.MINUTE);
		timeSecond = calendar.get(Calendar.SECOND);
		
		alarmHour = 12;
		alarmMinute = 0;
		alarmSecond = 0;
		
		day = calendar.get(Calendar.DAY_OF_MONTH);
		month = calendar.get(Calendar.MONTH) + 1;
		year = calendar.get(Calendar.YEAR);
	}

	@Override
	public String getTime() {
		return String.format("%02d:%02d:%02d", timeHour, timeMinute, timeSecond);
	}

	@Override
	public void increaseTimeByOne() {
		timeSecond++;
		if (timeSecond == 60) {
			timeSecond = 0;
			timeMinute++;
			if (timeMinute == 60) {
				timeMinute = 0;
				timeHour++;
				if (timeHour == 24)
					timeHour = 0;
			}
		}
		
		if (alarm && timeHour == alarmHour && timeMinute == alarmMinute && timeSecond == alarmSecond)
			eventListener.raiseStartAlarm();
		
		System.out.println("TICK: " + getTime());
	}

	@Override
	public String getChrono() {
		return String.format("%02d:%02d:%02d", chronoMinute, chronoSecond, chronoCentisec);
	}
	
	@Override
	public void increaseChronoByOne() {
		chronoCentisec++;
		if (chronoCentisec == 100) {
			chronoCentisec = 0;
			chronoSecond++;
			if (chronoSecond == 60) {
			    chronoSecond = 0;
			    chronoMinute++;
				if (chronoMinute == 60)
					chronoMinute = 0;
			}
		}
	}

	@Override
	public void resetChrono() {
		chronoMinute = chronoSecond = chronoCentisec = 0;
	}

	@Override
	public void setAlarm() {
		alarm = !alarm;
	}

	@Override
	public boolean getAlarm() {
		return alarm;
	}

	@Override
	public String getDate() {
		return String.format("%02d/%02d/%02d", month, day, year % 100);
	}

	@Override
	public String getTimeLabelAsForShowing() {
		if (editionMode.equals(EditionMode.TIME))
			return String.format("%02d:%02d:%02d", timeHour, timeMinute, timeSecond);
		else
			return String.format("%02d:%02d:%02d", alarmHour, alarmMinute, alarmSecond);
	}

	@Override
	public String getTimeLabelAsForHiding() {
		String label;
		int h, m, s;
		if (editionMode.equals(EditionMode.TIME)) {
			h = timeHour;
			m = timeMinute;
			s = timeSecond;
		} else {
			h = alarmHour;
			m = alarmMinute;
			s = alarmSecond;
		}
		
		switch (currentSelection) {
		case 0: 
			label = String.format("  :%02d:%02d", m, s); break;
		case 1:
			label = String.format("%02d:  :%02d", h, s); break;
		case 2:
			label = String.format("%02d:%02d:  ", h, m); break;
		default:
			label = String.format("%02d:%02d:%02d", h, m, s);
		}
		
		return label;
	}

	@Override
	public String getDateLabelAsForShowing() {
		return String.format("%02d/%02d/%02d", month, day, year % 100);
	}

	@Override
	public String getDateLabelAsForHiding() {
		String label;
		switch (currentSelection) {
		case 3: 
			label = String.format("  /%02d/%02d", day, year % 100); break;
		case 4:
			label = String.format("%02d/  /%02d", month, year % 100); break;
		case 5:
			label = String.format("%02d/%02d/  ", month, day); break;
		default:
			label = String.format("%02d/%02d/%02d", month, day, year % 100);
		}
		
		return label;
	}

	@Override
	public void startTimeEditMode() {
		currentSelection = 0;
		editionMode = EditionMode.TIME;
	}

	@Override
	public void startAlarmEditMode() {
		currentSelection = 0;
		editionMode = EditionMode.ALARM;
	}

	@Override
	public void increaseSelection() {
		Calendar current = new GregorianCalendar(year,month, day);
		
		if (editionMode == EditionMode.TIME) {
			switch (currentSelection) {
			case 0: timeHour = (timeHour + 1) % 24; break;
			case 1: timeMinute = (timeMinute + 1) % 60; break;
			case 2: timeSecond = (timeSecond + 1) % 60; break;
			case 3: month = month + 1 <= 12 ? month + 1 : 1; break;
			case 4: int maxdays = current.getActualMaximum(Calendar.DAY_OF_MONTH);
					day = day + 1 <= maxdays ? day + 1 : 1;
					break;
			case 5: year++;
			}
		} else {
			switch (currentSelection) {
			case 0: alarmHour = (alarmHour + 1) % 24; break;
			case 1: alarmMinute = (alarmMinute + 1) % 60; break;
			case 2: alarmSecond = (alarmSecond + 1) % 60; break;
			}			
		}
	}

	@Override
	public void selectNext() {
		currentSelection++;
		if ((editionMode == EditionMode.TIME && currentSelection == 6) ||
				(editionMode == EditionMode.ALARM && currentSelection == 3))
			currentSelection = 0;			
	}

}

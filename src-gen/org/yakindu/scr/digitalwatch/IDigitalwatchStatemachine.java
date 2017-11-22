package org.yakindu.scr.digitalwatch;

import org.yakindu.scr.IStatemachine;

public interface IDigitalwatchStatemachine extends IStatemachine {

	public interface SCIButtons {
	
		public void raiseTopLeftPressed();
		
		public void raiseTopLeftReleased();
		
		public void raiseTopRightPressed();
		
		public void raiseTopRightReleased();
		
		public void raiseBottomLeftPressed();
		
		public void raiseBottomLeftReleased();
		
		public void raiseBottomRightPressed();
		
		public void raiseBottomRightReleased();
		
	}
	
	public SCIButtons getSCIButtons();
	
	public interface SCIDisplay {
	
		public void setSCIDisplayOperationCallback(SCIDisplayOperationCallback operationCallback);
	
	}
	
	public interface SCIDisplayOperationCallback {
	
		public void refreshTimeDisplay();
		
		public void refreshChronoDisplay();
		
		public void refreshDateDisplay();
		
		public void refreshAlarmDisplay();
		
		public void setIndiglo();
		
		public void unsetIndiglo();
		
		public void showSelection();
		
		public void hideSelection();
		
	}
	
	public SCIDisplay getSCIDisplay();
	
	public interface SCILogicUnit {
	
		public void raiseStartAlarm();
		
		public void setSCILogicUnitOperationCallback(SCILogicUnitOperationCallback operationCallback);
	
	}
	
	public interface SCILogicUnitOperationCallback {
	
		public String getTime();
		
		public String getTimeLabelAsForShowing();
		
		public String getTimeLabelAsForHiding();
		
		public void increaseTimeByOne();
		
		public String getDate();
		
		public String getDateLabelAsForShowing();
		
		public String getDateLabelAsForHiding();
		
		public String getChrono();
		
		public void increaseChronoByOne();
		
		public void resetChrono();
		
		public void setAlarm();
		
		public boolean getAlarm();
		
		public void startTimeEditMode();
		
		public void startAlarmEditMode();
		
		public void increaseSelection();
		
		public void selectNext();
		
	}
	
	public SCILogicUnit getSCILogicUnit();
	
}

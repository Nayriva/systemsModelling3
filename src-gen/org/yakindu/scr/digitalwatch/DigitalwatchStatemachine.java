package org.yakindu.scr.digitalwatch;
import org.yakindu.scr.ITimer;

public class DigitalwatchStatemachine implements IDigitalwatchStatemachine {

	protected class SCIButtonsImpl implements SCIButtons {
	
		private boolean topLeftPressed;
		
		public void raiseTopLeftPressed() {
			topLeftPressed = true;
		}
		
		private boolean topLeftReleased;
		
		public void raiseTopLeftReleased() {
			topLeftReleased = true;
		}
		
		private boolean topRightPressed;
		
		public void raiseTopRightPressed() {
			topRightPressed = true;
		}
		
		private boolean topRightReleased;
		
		public void raiseTopRightReleased() {
			topRightReleased = true;
		}
		
		private boolean bottomLeftPressed;
		
		public void raiseBottomLeftPressed() {
			bottomLeftPressed = true;
		}
		
		private boolean bottomLeftReleased;
		
		public void raiseBottomLeftReleased() {
			bottomLeftReleased = true;
		}
		
		private boolean bottomRightPressed;
		
		public void raiseBottomRightPressed() {
			bottomRightPressed = true;
		}
		
		private boolean bottomRightReleased;
		
		public void raiseBottomRightReleased() {
			bottomRightReleased = true;
		}
		
		protected void clearEvents() {
			topLeftPressed = false;
			topLeftReleased = false;
			topRightPressed = false;
			topRightReleased = false;
			bottomLeftPressed = false;
			bottomLeftReleased = false;
			bottomRightPressed = false;
			bottomRightReleased = false;
		}
	}
	
	protected SCIButtonsImpl sCIButtons;
	
	protected class SCIDisplayImpl implements SCIDisplay {
	
		private SCIDisplayOperationCallback operationCallback;
		
		public void setSCIDisplayOperationCallback(
				SCIDisplayOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}
	}
	
	protected SCIDisplayImpl sCIDisplay;
	
	protected class SCILogicUnitImpl implements SCILogicUnit {
	
		private SCILogicUnitOperationCallback operationCallback;
		
		public void setSCILogicUnitOperationCallback(
				SCILogicUnitOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}
		private boolean startAlarm;
		
		public void raiseStartAlarm() {
			startAlarm = true;
		}
		
		protected void clearEvents() {
			startAlarm = false;
		}
	}
	
	protected SCILogicUnitImpl sCILogicUnit;
	
	private boolean initialized = false;
	
	public enum State {
		main_region_digitalWatch,
		main_region_digitalWatch_main_chrono,
		main_region_digitalWatch_main_time,
		main_region_digitalWatch_main_time_buttons_noButton,
		main_region_digitalWatch_main_time_buttons_blPress,
		main_region_digitalWatch_main_time_buttons_brPress,
		main_region_digitalWatch_main_edit,
		main_region_digitalWatch_main_edit_edit_selection,
		main_region_digitalWatch_main_edit_edit_selection_flash_on,
		main_region_digitalWatch_main_edit_edit_selection_flash_off,
		main_region_digitalWatch_main_edit_edit_blPress,
		main_region_digitalWatch_main_edit_edit_brPress,
		main_region_digitalWatch_time_running,
		main_region_digitalWatch_time_running_buttons_brPress,
		main_region_digitalWatch_time_running_buttons_noButton,
		main_region_digitalWatch_time_running_buttons_blPress,
		main_region_digitalWatch_time_running_timeUpdate_update,
		main_region_digitalWatch_time_stopped,
		main_region_digitalWatch_time_stopped_buttons_noButton,
		main_region_digitalWatch_time_stopped_buttons_brPress,
		main_region_digitalWatch_time_stopped_buttons_blPress,
		main_region_digitalWatch_chrono_chronoOff,
		main_region_digitalWatch_chrono_chronoOn,
		main_region_digitalWatch_light_lightOff,
		main_region_digitalWatch_light_lightOn,
		main_region_digitalWatch_light_lightOn_buttons_noButton,
		main_region_digitalWatch_light_lightOn_buttons_trPress,
		main_region_digitalWatch_alarm_off,
		main_region_digitalWatch_alarm_on,
		main_region_digitalWatch_alarm_alarmTriggered,
		main_region_digitalWatch_alarm_alarmTriggered_on_snooze,
		main_region_digitalWatch_alarm_alarmTriggered_on_flashing,
		main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn,
		main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff,
		$NullState$
	};
	
	private State[] historyVector = new State[6];
	private final State[] stateVector = new State[6];
	
	private int nextStateIndex;
	
	private ITimer timer;
	
	private final boolean[] timeEvents = new boolean[17];
	
	
	private long flash;
	
	protected void setFlash(long value) {
		flash = value;
	}
	
	protected long getFlash() {
		return flash;
	}
	
	public DigitalwatchStatemachine() {
		sCIButtons = new SCIButtonsImpl();
		sCIDisplay = new SCIDisplayImpl();
		sCILogicUnit = new SCILogicUnitImpl();
	}
	
	public void init() {
		this.initialized = true;
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		
		if (this.sCIDisplay.operationCallback == null) {
			throw new IllegalStateException("Operation callback for interface sCIDisplay must be set.");
		}
		
		if (this.sCILogicUnit.operationCallback == null) {
			throw new IllegalStateException("Operation callback for interface sCILogicUnit must be set.");
		}
		
		for (int i = 0; i < 6; i++) {
			stateVector[i] = State.$NullState$;
		}
		for (int i = 0; i < 6; i++) {
			historyVector[i] = State.$NullState$;
		}
		clearEvents();
		clearOutEvents();
		setFlash(0);
	}
	
	public void enter() {
		if (!initialized) {
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		}
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
	
		enterSequence_main_region_default();
	}
	
	public void exit() {
		exitSequence_main_region();
	}
	
	/**
	 * @see IStatemachine#isActive()
	 */
	public boolean isActive() {
		return stateVector[0] != State.$NullState$||stateVector[1] != State.$NullState$||stateVector[2] != State.$NullState$||stateVector[3] != State.$NullState$||stateVector[4] != State.$NullState$||stateVector[5] != State.$NullState$;
	}
	
	/** 
	* Always returns 'false' since this state machine can never become final.
	*
	* @see IStatemachine#isFinal()
	*/
	public boolean isFinal() {
		return false;
	}
	/**
	* This method resets the incoming events (time events included).
	*/
	protected void clearEvents() {
		sCIButtons.clearEvents();
		sCILogicUnit.clearEvents();
		for (int i=0; i<timeEvents.length; i++) {
			timeEvents[i] = false;
		}
	}
	
	/**
	* This method resets the outgoing events.
	*/
	protected void clearOutEvents() {
	}
	
	/**
	* Returns true if the given state is currently active otherwise false.
	*/
	public boolean isStateActive(State state) {
	
		switch (state) {
		case main_region_digitalWatch:
			return stateVector[0].ordinal() >= State.
					main_region_digitalWatch.ordinal()&& stateVector[0].ordinal() <= State.main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff.ordinal();
		case main_region_digitalWatch_main_chrono:
			return stateVector[0] == State.main_region_digitalWatch_main_chrono;
		case main_region_digitalWatch_main_time:
			return stateVector[0].ordinal() >= State.
					main_region_digitalWatch_main_time.ordinal()&& stateVector[0].ordinal() <= State.main_region_digitalWatch_main_time_buttons_brPress.ordinal();
		case main_region_digitalWatch_main_time_buttons_noButton:
			return stateVector[0] == State.main_region_digitalWatch_main_time_buttons_noButton;
		case main_region_digitalWatch_main_time_buttons_blPress:
			return stateVector[0] == State.main_region_digitalWatch_main_time_buttons_blPress;
		case main_region_digitalWatch_main_time_buttons_brPress:
			return stateVector[0] == State.main_region_digitalWatch_main_time_buttons_brPress;
		case main_region_digitalWatch_main_edit:
			return stateVector[0].ordinal() >= State.
					main_region_digitalWatch_main_edit.ordinal()&& stateVector[0].ordinal() <= State.main_region_digitalWatch_main_edit_edit_brPress.ordinal();
		case main_region_digitalWatch_main_edit_edit_selection:
			return stateVector[0].ordinal() >= State.
					main_region_digitalWatch_main_edit_edit_selection.ordinal()&& stateVector[0].ordinal() <= State.main_region_digitalWatch_main_edit_edit_selection_flash_off.ordinal();
		case main_region_digitalWatch_main_edit_edit_selection_flash_on:
			return stateVector[0] == State.main_region_digitalWatch_main_edit_edit_selection_flash_on;
		case main_region_digitalWatch_main_edit_edit_selection_flash_off:
			return stateVector[0] == State.main_region_digitalWatch_main_edit_edit_selection_flash_off;
		case main_region_digitalWatch_main_edit_edit_blPress:
			return stateVector[0] == State.main_region_digitalWatch_main_edit_edit_blPress;
		case main_region_digitalWatch_main_edit_edit_brPress:
			return stateVector[0] == State.main_region_digitalWatch_main_edit_edit_brPress;
		case main_region_digitalWatch_time_running:
			return stateVector[1].ordinal() >= State.
					main_region_digitalWatch_time_running.ordinal()&& stateVector[1].ordinal() <= State.main_region_digitalWatch_time_running_timeUpdate_update.ordinal();
		case main_region_digitalWatch_time_running_buttons_brPress:
			return stateVector[1] == State.main_region_digitalWatch_time_running_buttons_brPress;
		case main_region_digitalWatch_time_running_buttons_noButton:
			return stateVector[1] == State.main_region_digitalWatch_time_running_buttons_noButton;
		case main_region_digitalWatch_time_running_buttons_blPress:
			return stateVector[1] == State.main_region_digitalWatch_time_running_buttons_blPress;
		case main_region_digitalWatch_time_running_timeUpdate_update:
			return stateVector[2] == State.main_region_digitalWatch_time_running_timeUpdate_update;
		case main_region_digitalWatch_time_stopped:
			return stateVector[1].ordinal() >= State.
					main_region_digitalWatch_time_stopped.ordinal()&& stateVector[1].ordinal() <= State.main_region_digitalWatch_time_stopped_buttons_blPress.ordinal();
		case main_region_digitalWatch_time_stopped_buttons_noButton:
			return stateVector[1] == State.main_region_digitalWatch_time_stopped_buttons_noButton;
		case main_region_digitalWatch_time_stopped_buttons_brPress:
			return stateVector[1] == State.main_region_digitalWatch_time_stopped_buttons_brPress;
		case main_region_digitalWatch_time_stopped_buttons_blPress:
			return stateVector[1] == State.main_region_digitalWatch_time_stopped_buttons_blPress;
		case main_region_digitalWatch_chrono_chronoOff:
			return stateVector[3] == State.main_region_digitalWatch_chrono_chronoOff;
		case main_region_digitalWatch_chrono_chronoOn:
			return stateVector[3] == State.main_region_digitalWatch_chrono_chronoOn;
		case main_region_digitalWatch_light_lightOff:
			return stateVector[4] == State.main_region_digitalWatch_light_lightOff;
		case main_region_digitalWatch_light_lightOn:
			return stateVector[4].ordinal() >= State.
					main_region_digitalWatch_light_lightOn.ordinal()&& stateVector[4].ordinal() <= State.main_region_digitalWatch_light_lightOn_buttons_trPress.ordinal();
		case main_region_digitalWatch_light_lightOn_buttons_noButton:
			return stateVector[4] == State.main_region_digitalWatch_light_lightOn_buttons_noButton;
		case main_region_digitalWatch_light_lightOn_buttons_trPress:
			return stateVector[4] == State.main_region_digitalWatch_light_lightOn_buttons_trPress;
		case main_region_digitalWatch_alarm_off:
			return stateVector[5] == State.main_region_digitalWatch_alarm_off;
		case main_region_digitalWatch_alarm_on:
			return stateVector[5] == State.main_region_digitalWatch_alarm_on;
		case main_region_digitalWatch_alarm_alarmTriggered:
			return stateVector[5].ordinal() >= State.
					main_region_digitalWatch_alarm_alarmTriggered.ordinal()&& stateVector[5].ordinal() <= State.main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff.ordinal();
		case main_region_digitalWatch_alarm_alarmTriggered_on_snooze:
			return stateVector[5] == State.main_region_digitalWatch_alarm_alarmTriggered_on_snooze;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing:
			return stateVector[5].ordinal() >= State.
					main_region_digitalWatch_alarm_alarmTriggered_on_flashing.ordinal()&& stateVector[5].ordinal() <= State.main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff.ordinal();
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
			return stateVector[5] == State.main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
			return stateVector[5] == State.main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff;
		default:
			return false;
		}
	}
	
	/**
	* Set the {@link ITimer} for the state machine. It must be set
	* externally on a timed state machine before a run cycle can be correct
	* executed.
	* 
	* @param timer
	*/
	public void setTimer(ITimer timer) {
		this.timer = timer;
	}
	
	/**
	* Returns the currently used timer.
	* 
	* @return {@link ITimer}
	*/
	public ITimer getTimer() {
		return timer;
	}
	
	public void timeElapsed(int eventID) {
		timeEvents[eventID] = true;
	}
	
	public SCIButtons getSCIButtons() {
		return sCIButtons;
	}
	
	public SCIDisplay getSCIDisplay() {
		return sCIDisplay;
	}
	
	public SCILogicUnit getSCILogicUnit() {
		return sCILogicUnit;
	}
	
	private boolean check_main_region_digitalWatch_main_chrono_tr0_tr0() {
		return sCIButtons.topLeftPressed;
	}
	
	private boolean check_main_region_digitalWatch_main_time_tr0_tr0() {
		return sCIButtons.topLeftPressed;
	}
	
	private boolean check_main_region_digitalWatch_main_time_buttons_noButton_tr0_tr0() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_digitalWatch_main_time_buttons_noButton_tr1_tr1() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_digitalWatch_main_time_buttons_blPress_tr0_tr0() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_digitalWatch_main_time_buttons_blPress_tr1_tr1() {
		return timeEvents[0];
	}
	
	private boolean check_main_region_digitalWatch_main_time_buttons_brPress_tr0_tr0() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_digitalWatch_main_time_buttons_brPress_tr1_tr1() {
		return timeEvents[1];
	}
	
	private boolean check_main_region_digitalWatch_main_edit_edit_selection_tr0_tr0() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_digitalWatch_main_edit_edit_selection_tr1_tr1() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_digitalWatch_main_edit_edit_selection_tr2_tr2() {
		return timeEvents[2];
	}
	
	private boolean check_main_region_digitalWatch_main_edit_edit_selection_flash_on_tr0_tr0() {
		return timeEvents[3];
	}
	
	private boolean check_main_region_digitalWatch_main_edit_edit_selection_flash_off_tr0_tr0() {
		return timeEvents[4];
	}
	
	private boolean check_main_region_digitalWatch_main_edit_edit_blPress_tr0_tr0() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_digitalWatch_main_edit_edit_blPress_tr1_tr1() {
		return timeEvents[5];
	}
	
	private boolean check_main_region_digitalWatch_main_edit_edit_brPress_tr0_tr0() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_digitalWatch_main_edit_edit_brPress_tr1_tr1() {
		return timeEvents[6];
	}
	
	private boolean check_main_region_digitalWatch_time_running_buttons_brPress_tr0_tr0() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_digitalWatch_time_running_buttons_brPress_tr1_tr1() {
		return timeEvents[7];
	}
	
	private boolean check_main_region_digitalWatch_time_running_buttons_noButton_tr0_tr0() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_digitalWatch_time_running_buttons_noButton_tr1_tr1() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_digitalWatch_time_running_buttons_blPress_tr0_tr0() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_digitalWatch_time_running_buttons_blPress_tr1_tr1() {
		return timeEvents[8];
	}
	
	private boolean check_main_region_digitalWatch_time_running_timeUpdate_update_tr0_tr0() {
		return timeEvents[9];
	}
	
	private boolean check_main_region_digitalWatch_time_stopped_buttons_noButton_tr0_tr0() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_digitalWatch_time_stopped_buttons_noButton_tr1_tr1() {
		return timeEvents[10];
	}
	
	private boolean check_main_region_digitalWatch_time_stopped_buttons_noButton_tr2_tr2() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_digitalWatch_time_stopped_buttons_brPress_tr0_tr0() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_digitalWatch_time_stopped_buttons_brPress_tr1_tr1() {
		return timeEvents[11];
	}
	
	private boolean check_main_region_digitalWatch_time_stopped_buttons_blPress_tr0_tr0() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_digitalWatch_chrono_chronoOff_tr0_tr0() {
		return (sCIButtons.bottomRightPressed) && (isStateActive(State.main_region_digitalWatch_main_chrono));
	}
	
	private boolean check_main_region_digitalWatch_chrono_chronoOff_tr1_tr1() {
		return (sCIButtons.bottomLeftPressed) && (isStateActive(State.main_region_digitalWatch_main_chrono));
	}
	
	private boolean check_main_region_digitalWatch_chrono_chronoOn_tr0_tr0() {
		return (sCIButtons.bottomRightPressed) && (isStateActive(State.main_region_digitalWatch_main_chrono));
	}
	
	private boolean check_main_region_digitalWatch_chrono_chronoOn_tr1_tr1() {
		return (sCIButtons.bottomLeftPressed) && (isStateActive(State.main_region_digitalWatch_main_chrono));
	}
	
	private boolean check_main_region_digitalWatch_chrono_chronoOn_tr2_tr2() {
		return timeEvents[12];
	}
	
	private boolean check_main_region_digitalWatch_light_lightOff_tr0_tr0() {
		return sCIButtons.topRightPressed;
	}
	
	private boolean check_main_region_digitalWatch_light_lightOn_buttons_noButton_tr0_tr0() {
		return sCIButtons.topRightPressed;
	}
	
	private boolean check_main_region_digitalWatch_light_lightOn_buttons_noButton_tr1_tr1() {
		return timeEvents[13];
	}
	
	private boolean check_main_region_digitalWatch_light_lightOn_buttons_trPress_tr0_tr0() {
		return sCIButtons.topRightReleased;
	}
	
	private boolean check_main_region_digitalWatch_alarm_off_tr0_tr0() {
		return (sCIButtons.bottomLeftPressed) && (isStateActive(State.main_region_digitalWatch_main_time));
	}
	
	private boolean check_main_region_digitalWatch_alarm_on_tr0_tr0() {
		return (sCIButtons.bottomLeftPressed) && (isStateActive(State.main_region_digitalWatch_main_time));
	}
	
	private boolean check_main_region_digitalWatch_alarm_on_tr1_tr1() {
		return sCILogicUnit.startAlarm;
	}
	
	private boolean check_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_tr0_tr0() {
		return timeEvents[14];
	}
	
	private boolean check_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_tr1_tr1() {
		return (sCIButtons.bottomLeftPressed) && (isStateActive(State.main_region_digitalWatch_main_time));
	}
	
	private boolean check_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_tr0_tr0() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_tr0_tr0() {
		return (timeEvents[15]) && (getFlash()!=4);
	}
	
	private boolean check_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_tr1_tr1() {
		return getFlash()==4;
	}
	
	private boolean check_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff_tr0_tr0() {
		return timeEvents[16];
	}
	
	private void effect_main_region_digitalWatch_main_chrono_tr0() {
		exitSequence_main_region_digitalWatch_main_chrono();
		enterSequence_main_region_digitalWatch_main_time_default();
	}
	
	private void effect_main_region_digitalWatch_main_time_tr0() {
		exitSequence_main_region_digitalWatch_main_time();
		enterSequence_main_region_digitalWatch_main_chrono_default();
	}
	
	private void effect_main_region_digitalWatch_main_time_buttons_noButton_tr0() {
		exitSequence_main_region_digitalWatch_main_time_buttons_noButton();
		enterSequence_main_region_digitalWatch_main_time_buttons_blPress_default();
	}
	
	private void effect_main_region_digitalWatch_main_time_buttons_noButton_tr1() {
		exitSequence_main_region_digitalWatch_main_time_buttons_noButton();
		enterSequence_main_region_digitalWatch_main_time_buttons_brPress_default();
	}
	
	private void effect_main_region_digitalWatch_main_time_buttons_blPress_tr0() {
		exitSequence_main_region_digitalWatch_main_time_buttons_blPress();
		enterSequence_main_region_digitalWatch_main_time_buttons_noButton_default();
	}
	
	private void effect_main_region_digitalWatch_main_time_buttons_blPress_tr1() {
		exitSequence_main_region_digitalWatch_main_time();
		sCILogicUnit.operationCallback.startAlarmEditMode();
		
		enterSequence_main_region_digitalWatch_main_edit_default();
	}
	
	private void effect_main_region_digitalWatch_main_time_buttons_brPress_tr0() {
		exitSequence_main_region_digitalWatch_main_time_buttons_brPress();
		enterSequence_main_region_digitalWatch_main_time_buttons_noButton_default();
	}
	
	private void effect_main_region_digitalWatch_main_time_buttons_brPress_tr1() {
		exitSequence_main_region_digitalWatch_main_time();
		sCILogicUnit.operationCallback.startTimeEditMode();
		
		enterSequence_main_region_digitalWatch_main_edit_default();
	}
	
	private void effect_main_region_digitalWatch_main_edit_edit_selection_tr0() {
		exitSequence_main_region_digitalWatch_main_edit_edit_selection();
		enterSequence_main_region_digitalWatch_main_edit_edit_blPress_default();
	}
	
	private void effect_main_region_digitalWatch_main_edit_edit_selection_tr1() {
		exitSequence_main_region_digitalWatch_main_edit_edit_selection();
		enterSequence_main_region_digitalWatch_main_edit_edit_brPress_default();
	}
	
	private void effect_main_region_digitalWatch_main_edit_edit_selection_tr2() {
		exitSequence_main_region_digitalWatch_main_edit();
		enterSequence_main_region_digitalWatch_main_time_default();
	}
	
	private void effect_main_region_digitalWatch_main_edit_edit_selection_flash_on_tr0() {
		exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on();
		enterSequence_main_region_digitalWatch_main_edit_edit_selection_flash_off_default();
	}
	
	private void effect_main_region_digitalWatch_main_edit_edit_selection_flash_off_tr0() {
		exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_off();
		enterSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on_default();
	}
	
	private void effect_main_region_digitalWatch_main_edit_edit_blPress_tr0() {
		exitSequence_main_region_digitalWatch_main_edit_edit_blPress();
		enterSequence_main_region_digitalWatch_main_edit_edit_selection_default();
	}
	
	private void effect_main_region_digitalWatch_main_edit_edit_blPress_tr1() {
		exitSequence_main_region_digitalWatch_main_edit_edit_blPress();
		enterSequence_main_region_digitalWatch_main_edit_edit_blPress_default();
	}
	
	private void effect_main_region_digitalWatch_main_edit_edit_brPress_tr0() {
		exitSequence_main_region_digitalWatch_main_edit_edit_brPress();
		enterSequence_main_region_digitalWatch_main_edit_edit_selection_default();
	}
	
	private void effect_main_region_digitalWatch_main_edit_edit_brPress_tr1() {
		exitSequence_main_region_digitalWatch_main_edit();
		enterSequence_main_region_digitalWatch_main_time_default();
	}
	
	private void effect_main_region_digitalWatch_time_running_buttons_brPress_tr0() {
		exitSequence_main_region_digitalWatch_time_running_buttons_brPress();
		enterSequence_main_region_digitalWatch_time_running_buttons_noButton_default();
	}
	
	private void effect_main_region_digitalWatch_time_running_buttons_brPress_tr1() {
		exitSequence_main_region_digitalWatch_time_running();
		enterSequence_main_region_digitalWatch_time_stopped_default();
	}
	
	private void effect_main_region_digitalWatch_time_running_buttons_noButton_tr0() {
		exitSequence_main_region_digitalWatch_time_running_buttons_noButton();
		enterSequence_main_region_digitalWatch_time_running_buttons_brPress_default();
	}
	
	private void effect_main_region_digitalWatch_time_running_buttons_noButton_tr1() {
		exitSequence_main_region_digitalWatch_time_running_buttons_noButton();
		enterSequence_main_region_digitalWatch_time_running_buttons_blPress_default();
	}
	
	private void effect_main_region_digitalWatch_time_running_buttons_blPress_tr0() {
		exitSequence_main_region_digitalWatch_time_running_buttons_blPress();
		enterSequence_main_region_digitalWatch_time_running_buttons_noButton_default();
	}
	
	private void effect_main_region_digitalWatch_time_running_buttons_blPress_tr1() {
		exitSequence_main_region_digitalWatch_time_running();
		enterSequence_main_region_digitalWatch_time_stopped_default();
	}
	
	private void effect_main_region_digitalWatch_time_running_timeUpdate_update_tr0() {
		exitSequence_main_region_digitalWatch_time_running_timeUpdate_update();
		sCILogicUnit.operationCallback.increaseTimeByOne();
		
		enterSequence_main_region_digitalWatch_time_running_timeUpdate_update_default();
	}
	
	private void effect_main_region_digitalWatch_time_stopped_buttons_noButton_tr0() {
		exitSequence_main_region_digitalWatch_time_stopped_buttons_noButton();
		enterSequence_main_region_digitalWatch_time_stopped_buttons_brPress_default();
	}
	
	private void effect_main_region_digitalWatch_time_stopped_buttons_noButton_tr1() {
		exitSequence_main_region_digitalWatch_time_stopped();
		enterSequence_main_region_digitalWatch_time_running_default();
	}
	
	private void effect_main_region_digitalWatch_time_stopped_buttons_noButton_tr2() {
		exitSequence_main_region_digitalWatch_time_stopped_buttons_noButton();
		enterSequence_main_region_digitalWatch_time_stopped_buttons_blPress_default();
	}
	
	private void effect_main_region_digitalWatch_time_stopped_buttons_brPress_tr0() {
		exitSequence_main_region_digitalWatch_time_stopped_buttons_brPress();
		enterSequence_main_region_digitalWatch_time_stopped_buttons_noButton_default();
	}
	
	private void effect_main_region_digitalWatch_time_stopped_buttons_brPress_tr1() {
		exitSequence_main_region_digitalWatch_time_stopped();
		enterSequence_main_region_digitalWatch_time_running_default();
	}
	
	private void effect_main_region_digitalWatch_time_stopped_buttons_blPress_tr0() {
		exitSequence_main_region_digitalWatch_time_stopped_buttons_blPress();
		enterSequence_main_region_digitalWatch_time_stopped_buttons_noButton_default();
	}
	
	private void effect_main_region_digitalWatch_chrono_chronoOff_tr0() {
		exitSequence_main_region_digitalWatch_chrono_chronoOff();
		enterSequence_main_region_digitalWatch_chrono_chronoOn_default();
	}
	
	private void effect_main_region_digitalWatch_chrono_chronoOff_tr1() {
		exitSequence_main_region_digitalWatch_chrono_chronoOff();
		sCILogicUnit.operationCallback.resetChrono();
		
		enterSequence_main_region_digitalWatch_chrono_chronoOff_default();
	}
	
	private void effect_main_region_digitalWatch_chrono_chronoOn_tr0() {
		exitSequence_main_region_digitalWatch_chrono_chronoOn();
		enterSequence_main_region_digitalWatch_chrono_chronoOff_default();
	}
	
	private void effect_main_region_digitalWatch_chrono_chronoOn_tr1() {
		exitSequence_main_region_digitalWatch_chrono_chronoOn();
		sCILogicUnit.operationCallback.resetChrono();
		
		enterSequence_main_region_digitalWatch_chrono_chronoOn_default();
	}
	
	private void effect_main_region_digitalWatch_chrono_chronoOn_tr2() {
		exitSequence_main_region_digitalWatch_chrono_chronoOn();
		sCILogicUnit.operationCallback.increaseChronoByOne();
		
		enterSequence_main_region_digitalWatch_chrono_chronoOn_default();
	}
	
	private void effect_main_region_digitalWatch_light_lightOff_tr0() {
		exitSequence_main_region_digitalWatch_light_lightOff();
		enterSequence_main_region_digitalWatch_light_lightOn_default();
	}
	
	private void effect_main_region_digitalWatch_light_lightOn_buttons_noButton_tr0() {
		exitSequence_main_region_digitalWatch_light_lightOn_buttons_noButton();
		enterSequence_main_region_digitalWatch_light_lightOn_buttons_trPress_default();
	}
	
	private void effect_main_region_digitalWatch_light_lightOn_buttons_noButton_tr1() {
		exitSequence_main_region_digitalWatch_light_lightOn();
		enterSequence_main_region_digitalWatch_light_lightOff_default();
	}
	
	private void effect_main_region_digitalWatch_light_lightOn_buttons_trPress_tr0() {
		exitSequence_main_region_digitalWatch_light_lightOn_buttons_trPress();
		enterSequence_main_region_digitalWatch_light_lightOn_buttons_noButton_default();
	}
	
	private void effect_main_region_digitalWatch_alarm_off_tr0() {
		exitSequence_main_region_digitalWatch_alarm_off();
		enterSequence_main_region_digitalWatch_alarm_on_default();
	}
	
	private void effect_main_region_digitalWatch_alarm_on_tr0() {
		exitSequence_main_region_digitalWatch_alarm_on();
		enterSequence_main_region_digitalWatch_alarm_off_default();
	}
	
	private void effect_main_region_digitalWatch_alarm_on_tr1() {
		exitSequence_main_region_digitalWatch_alarm_on();
		enterSequence_main_region_digitalWatch_alarm_alarmTriggered_default();
	}
	
	private void effect_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_tr0() {
		exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_snooze();
		enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_default();
	}
	
	private void effect_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_tr1() {
		exitSequence_main_region_digitalWatch_alarm_alarmTriggered();
		enterSequence_main_region_digitalWatch_alarm_off_default();
	}
	
	private void effect_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_tr0() {
		exitSequence_main_region_digitalWatch_alarm_alarmTriggered();
		enterSequence_main_region_digitalWatch_alarm_off_default();
	}
	
	private void effect_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_tr0() {
		exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn();
		enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff_default();
	}
	
	private void effect_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_tr1() {
		exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
		enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_default();
	}
	
	private void effect_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff_tr0() {
		exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff();
		enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_default();
	}
	
	/* Entry action for state 'chrono'. */
	private void entryAction_main_region_digitalWatch_main_chrono() {
		sCIDisplay.operationCallback.refreshChronoDisplay();
	}
	
	/* Entry action for state 'time'. */
	private void entryAction_main_region_digitalWatch_main_time() {
		sCIDisplay.operationCallback.refreshDateDisplay();
		
		sCIDisplay.operationCallback.refreshTimeDisplay();
	}
	
	/* Entry action for state 'blPress'. */
	private void entryAction_main_region_digitalWatch_main_time_buttons_blPress() {
		timer.setTimer(this, 0, 1500, false);
		
		sCILogicUnit.operationCallback.setAlarm();
	}
	
	/* Entry action for state 'brPress'. */
	private void entryAction_main_region_digitalWatch_main_time_buttons_brPress() {
		timer.setTimer(this, 1, 1500, false);
	}
	
	/* Entry action for state 'selection'. */
	private void entryAction_main_region_digitalWatch_main_edit_edit_selection() {
		timer.setTimer(this, 2, 5 * 1000, false);
	}
	
	/* Entry action for state 'on'. */
	private void entryAction_main_region_digitalWatch_main_edit_edit_selection_flash_on() {
		timer.setTimer(this, 3, 500, false);
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	/* Entry action for state 'off'. */
	private void entryAction_main_region_digitalWatch_main_edit_edit_selection_flash_off() {
		timer.setTimer(this, 4, 500, false);
		
		sCIDisplay.operationCallback.hideSelection();
	}
	
	/* Entry action for state 'blPress'. */
	private void entryAction_main_region_digitalWatch_main_edit_edit_blPress() {
		timer.setTimer(this, 5, 300, false);
		
		sCILogicUnit.operationCallback.increaseSelection();
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	/* Entry action for state 'brPress'. */
	private void entryAction_main_region_digitalWatch_main_edit_edit_brPress() {
		timer.setTimer(this, 6, 2 * 1000, false);
		
		sCILogicUnit.operationCallback.selectNext();
	}
	
	/* Entry action for state 'brPress'. */
	private void entryAction_main_region_digitalWatch_time_running_buttons_brPress() {
		timer.setTimer(this, 7, 1500, false);
	}
	
	/* Entry action for state 'blPress'. */
	private void entryAction_main_region_digitalWatch_time_running_buttons_blPress() {
		timer.setTimer(this, 8, 1500, false);
	}
	
	/* Entry action for state 'update'. */
	private void entryAction_main_region_digitalWatch_time_running_timeUpdate_update() {
		timer.setTimer(this, 9, 1 * 1000, false);
		
		if (isStateActive(State.main_region_digitalWatch_main_time)) {
			sCIDisplay.operationCallback.refreshTimeDisplay();
		}
		if (isStateActive(State.main_region_digitalWatch_main_time)) {
			sCIDisplay.operationCallback.refreshDateDisplay();
		}
	}
	
	/* Entry action for state 'noButton'. */
	private void entryAction_main_region_digitalWatch_time_stopped_buttons_noButton() {
		timer.setTimer(this, 10, 5 * 1000, false);
	}
	
	/* Entry action for state 'brPress'. */
	private void entryAction_main_region_digitalWatch_time_stopped_buttons_brPress() {
		timer.setTimer(this, 11, 2 * 1000, false);
	}
	
	/* Entry action for state 'chronoOff'. */
	private void entryAction_main_region_digitalWatch_chrono_chronoOff() {
		if (isStateActive(State.main_region_digitalWatch_main_chrono)) {
			sCIDisplay.operationCallback.refreshChronoDisplay();
		}
	}
	
	/* Entry action for state 'chronoOn'. */
	private void entryAction_main_region_digitalWatch_chrono_chronoOn() {
		timer.setTimer(this, 12, 10, false);
		
		if (isStateActive(State.main_region_digitalWatch_main_chrono)) {
			sCIDisplay.operationCallback.refreshChronoDisplay();
		}
	}
	
	/* Entry action for state 'lightOff'. */
	private void entryAction_main_region_digitalWatch_light_lightOff() {
		sCIDisplay.operationCallback.unsetIndiglo();
	}
	
	/* Entry action for state 'lightOn'. */
	private void entryAction_main_region_digitalWatch_light_lightOn() {
		sCIDisplay.operationCallback.setIndiglo();
	}
	
	/* Entry action for state 'noButton'. */
	private void entryAction_main_region_digitalWatch_light_lightOn_buttons_noButton() {
		timer.setTimer(this, 13, 2 * 1000, false);
	}
	
	/* Entry action for state 'off'. */
	private void entryAction_main_region_digitalWatch_alarm_off() {
		sCIDisplay.operationCallback.refreshAlarmDisplay();
	}
	
	/* Entry action for state 'on'. */
	private void entryAction_main_region_digitalWatch_alarm_on() {
		sCIDisplay.operationCallback.refreshAlarmDisplay();
	}
	
	/* Entry action for state 'snooze'. */
	private void entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_snooze() {
		timer.setTimer(this, 14, 60 * 1000, false);
		
		sCIDisplay.operationCallback.unsetIndiglo();
	}
	
	/* Entry action for state 'flashing'. */
	private void entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing() {
		setFlash(0);
	}
	
	/* Entry action for state 'lightOn'. */
	private void entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn() {
		timer.setTimer(this, 15, 500, false);
		
		sCIDisplay.operationCallback.setIndiglo();
	}
	
	/* Entry action for state 'lightOff'. */
	private void entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff() {
		timer.setTimer(this, 16, 500, false);
		
		sCIDisplay.operationCallback.unsetIndiglo();
		
		setFlash(flash + 1);
	}
	
	/* Exit action for state 'blPress'. */
	private void exitAction_main_region_digitalWatch_main_time_buttons_blPress() {
		timer.unsetTimer(this, 0);
	}
	
	/* Exit action for state 'brPress'. */
	private void exitAction_main_region_digitalWatch_main_time_buttons_brPress() {
		timer.unsetTimer(this, 1);
	}
	
	/* Exit action for state 'selection'. */
	private void exitAction_main_region_digitalWatch_main_edit_edit_selection() {
		timer.unsetTimer(this, 2);
	}
	
	/* Exit action for state 'on'. */
	private void exitAction_main_region_digitalWatch_main_edit_edit_selection_flash_on() {
		timer.unsetTimer(this, 3);
	}
	
	/* Exit action for state 'off'. */
	private void exitAction_main_region_digitalWatch_main_edit_edit_selection_flash_off() {
		timer.unsetTimer(this, 4);
	}
	
	/* Exit action for state 'blPress'. */
	private void exitAction_main_region_digitalWatch_main_edit_edit_blPress() {
		timer.unsetTimer(this, 5);
	}
	
	/* Exit action for state 'brPress'. */
	private void exitAction_main_region_digitalWatch_main_edit_edit_brPress() {
		timer.unsetTimer(this, 6);
	}
	
	/* Exit action for state 'brPress'. */
	private void exitAction_main_region_digitalWatch_time_running_buttons_brPress() {
		timer.unsetTimer(this, 7);
	}
	
	/* Exit action for state 'blPress'. */
	private void exitAction_main_region_digitalWatch_time_running_buttons_blPress() {
		timer.unsetTimer(this, 8);
	}
	
	/* Exit action for state 'update'. */
	private void exitAction_main_region_digitalWatch_time_running_timeUpdate_update() {
		timer.unsetTimer(this, 9);
	}
	
	/* Exit action for state 'noButton'. */
	private void exitAction_main_region_digitalWatch_time_stopped_buttons_noButton() {
		timer.unsetTimer(this, 10);
	}
	
	/* Exit action for state 'brPress'. */
	private void exitAction_main_region_digitalWatch_time_stopped_buttons_brPress() {
		timer.unsetTimer(this, 11);
	}
	
	/* Exit action for state 'chronoOn'. */
	private void exitAction_main_region_digitalWatch_chrono_chronoOn() {
		timer.unsetTimer(this, 12);
	}
	
	/* Exit action for state 'noButton'. */
	private void exitAction_main_region_digitalWatch_light_lightOn_buttons_noButton() {
		timer.unsetTimer(this, 13);
	}
	
	/* Exit action for state 'snooze'. */
	private void exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_snooze() {
		timer.unsetTimer(this, 14);
	}
	
	/* Exit action for state 'flashing'. */
	private void exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing() {
		sCIDisplay.operationCallback.unsetIndiglo();
	}
	
	/* Exit action for state 'lightOn'. */
	private void exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn() {
		timer.unsetTimer(this, 15);
	}
	
	/* Exit action for state 'lightOff'. */
	private void exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff() {
		timer.unsetTimer(this, 16);
	}
	
	/* 'default' enter sequence for state digitalWatch */
	private void enterSequence_main_region_digitalWatch_default() {
		enterSequence_main_region_digitalWatch_main_default();
		enterSequence_main_region_digitalWatch_time_default();
		enterSequence_main_region_digitalWatch_chrono_default();
		enterSequence_main_region_digitalWatch_light_default();
		enterSequence_main_region_digitalWatch_alarm_default();
	}
	
	/* 'default' enter sequence for state chrono */
	private void enterSequence_main_region_digitalWatch_main_chrono_default() {
		entryAction_main_region_digitalWatch_main_chrono();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalWatch_main_chrono;
	}
	
	/* 'default' enter sequence for state time */
	private void enterSequence_main_region_digitalWatch_main_time_default() {
		entryAction_main_region_digitalWatch_main_time();
		enterSequence_main_region_digitalWatch_main_time_buttons_default();
	}
	
	/* 'default' enter sequence for state noButton */
	private void enterSequence_main_region_digitalWatch_main_time_buttons_noButton_default() {
		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalWatch_main_time_buttons_noButton;
	}
	
	/* 'default' enter sequence for state blPress */
	private void enterSequence_main_region_digitalWatch_main_time_buttons_blPress_default() {
		entryAction_main_region_digitalWatch_main_time_buttons_blPress();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalWatch_main_time_buttons_blPress;
	}
	
	/* 'default' enter sequence for state brPress */
	private void enterSequence_main_region_digitalWatch_main_time_buttons_brPress_default() {
		entryAction_main_region_digitalWatch_main_time_buttons_brPress();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalWatch_main_time_buttons_brPress;
	}
	
	/* 'default' enter sequence for state edit */
	private void enterSequence_main_region_digitalWatch_main_edit_default() {
		enterSequence_main_region_digitalWatch_main_edit_edit_default();
	}
	
	/* 'default' enter sequence for state selection */
	private void enterSequence_main_region_digitalWatch_main_edit_edit_selection_default() {
		entryAction_main_region_digitalWatch_main_edit_edit_selection();
		enterSequence_main_region_digitalWatch_main_edit_edit_selection_flash_default();
	}
	
	/* 'default' enter sequence for state on */
	private void enterSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on_default() {
		entryAction_main_region_digitalWatch_main_edit_edit_selection_flash_on();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalWatch_main_edit_edit_selection_flash_on;
		
		historyVector[0] = stateVector[0];
	}
	
	/* 'default' enter sequence for state off */
	private void enterSequence_main_region_digitalWatch_main_edit_edit_selection_flash_off_default() {
		entryAction_main_region_digitalWatch_main_edit_edit_selection_flash_off();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalWatch_main_edit_edit_selection_flash_off;
		
		historyVector[0] = stateVector[0];
	}
	
	/* 'default' enter sequence for state blPress */
	private void enterSequence_main_region_digitalWatch_main_edit_edit_blPress_default() {
		entryAction_main_region_digitalWatch_main_edit_edit_blPress();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalWatch_main_edit_edit_blPress;
	}
	
	/* 'default' enter sequence for state brPress */
	private void enterSequence_main_region_digitalWatch_main_edit_edit_brPress_default() {
		entryAction_main_region_digitalWatch_main_edit_edit_brPress();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalWatch_main_edit_edit_brPress;
	}
	
	/* 'default' enter sequence for state running */
	private void enterSequence_main_region_digitalWatch_time_running_default() {
		enterSequence_main_region_digitalWatch_time_running_buttons_default();
		enterSequence_main_region_digitalWatch_time_running_timeUpdate_default();
		historyVector[1] = stateVector[1];
	}
	
	/* 'default' enter sequence for state brPress */
	private void enterSequence_main_region_digitalWatch_time_running_buttons_brPress_default() {
		entryAction_main_region_digitalWatch_time_running_buttons_brPress();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_digitalWatch_time_running_buttons_brPress;
	}
	
	/* 'default' enter sequence for state noButton */
	private void enterSequence_main_region_digitalWatch_time_running_buttons_noButton_default() {
		nextStateIndex = 1;
		stateVector[1] = State.main_region_digitalWatch_time_running_buttons_noButton;
	}
	
	/* 'default' enter sequence for state blPress */
	private void enterSequence_main_region_digitalWatch_time_running_buttons_blPress_default() {
		entryAction_main_region_digitalWatch_time_running_buttons_blPress();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_digitalWatch_time_running_buttons_blPress;
	}
	
	/* 'default' enter sequence for state update */
	private void enterSequence_main_region_digitalWatch_time_running_timeUpdate_update_default() {
		entryAction_main_region_digitalWatch_time_running_timeUpdate_update();
		nextStateIndex = 2;
		stateVector[2] = State.main_region_digitalWatch_time_running_timeUpdate_update;
	}
	
	/* 'default' enter sequence for state stopped */
	private void enterSequence_main_region_digitalWatch_time_stopped_default() {
		enterSequence_main_region_digitalWatch_time_stopped_buttons_default();
		historyVector[1] = stateVector[1];
	}
	
	/* 'default' enter sequence for state noButton */
	private void enterSequence_main_region_digitalWatch_time_stopped_buttons_noButton_default() {
		entryAction_main_region_digitalWatch_time_stopped_buttons_noButton();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_digitalWatch_time_stopped_buttons_noButton;
	}
	
	/* 'default' enter sequence for state brPress */
	private void enterSequence_main_region_digitalWatch_time_stopped_buttons_brPress_default() {
		entryAction_main_region_digitalWatch_time_stopped_buttons_brPress();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_digitalWatch_time_stopped_buttons_brPress;
	}
	
	/* 'default' enter sequence for state blPress */
	private void enterSequence_main_region_digitalWatch_time_stopped_buttons_blPress_default() {
		nextStateIndex = 1;
		stateVector[1] = State.main_region_digitalWatch_time_stopped_buttons_blPress;
	}
	
	/* 'default' enter sequence for state chronoOff */
	private void enterSequence_main_region_digitalWatch_chrono_chronoOff_default() {
		entryAction_main_region_digitalWatch_chrono_chronoOff();
		nextStateIndex = 3;
		stateVector[3] = State.main_region_digitalWatch_chrono_chronoOff;
		
		historyVector[2] = stateVector[3];
	}
	
	/* 'default' enter sequence for state chronoOn */
	private void enterSequence_main_region_digitalWatch_chrono_chronoOn_default() {
		entryAction_main_region_digitalWatch_chrono_chronoOn();
		nextStateIndex = 3;
		stateVector[3] = State.main_region_digitalWatch_chrono_chronoOn;
		
		historyVector[2] = stateVector[3];
	}
	
	/* 'default' enter sequence for state lightOff */
	private void enterSequence_main_region_digitalWatch_light_lightOff_default() {
		entryAction_main_region_digitalWatch_light_lightOff();
		nextStateIndex = 4;
		stateVector[4] = State.main_region_digitalWatch_light_lightOff;
	}
	
	/* 'default' enter sequence for state lightOn */
	private void enterSequence_main_region_digitalWatch_light_lightOn_default() {
		entryAction_main_region_digitalWatch_light_lightOn();
		enterSequence_main_region_digitalWatch_light_lightOn_buttons_default();
	}
	
	/* 'default' enter sequence for state noButton */
	private void enterSequence_main_region_digitalWatch_light_lightOn_buttons_noButton_default() {
		entryAction_main_region_digitalWatch_light_lightOn_buttons_noButton();
		nextStateIndex = 4;
		stateVector[4] = State.main_region_digitalWatch_light_lightOn_buttons_noButton;
	}
	
	/* 'default' enter sequence for state trPress */
	private void enterSequence_main_region_digitalWatch_light_lightOn_buttons_trPress_default() {
		nextStateIndex = 4;
		stateVector[4] = State.main_region_digitalWatch_light_lightOn_buttons_trPress;
	}
	
	/* 'default' enter sequence for state off */
	private void enterSequence_main_region_digitalWatch_alarm_off_default() {
		entryAction_main_region_digitalWatch_alarm_off();
		nextStateIndex = 5;
		stateVector[5] = State.main_region_digitalWatch_alarm_off;
		
		historyVector[3] = stateVector[5];
	}
	
	/* 'default' enter sequence for state on */
	private void enterSequence_main_region_digitalWatch_alarm_on_default() {
		entryAction_main_region_digitalWatch_alarm_on();
		nextStateIndex = 5;
		stateVector[5] = State.main_region_digitalWatch_alarm_on;
		
		historyVector[3] = stateVector[5];
	}
	
	/* 'default' enter sequence for state alarmTriggered */
	private void enterSequence_main_region_digitalWatch_alarm_alarmTriggered_default() {
		enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_default();
		historyVector[3] = stateVector[5];
	}
	
	/* 'default' enter sequence for state snooze */
	private void enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_default() {
		entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_snooze();
		nextStateIndex = 5;
		stateVector[5] = State.main_region_digitalWatch_alarm_alarmTriggered_on_snooze;
		
		historyVector[4] = stateVector[5];
	}
	
	/* 'default' enter sequence for state flashing */
	private void enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_default() {
		entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
		enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_default();
		historyVector[4] = stateVector[5];
	}
	
	/* 'default' enter sequence for state lightOn */
	private void enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_default() {
		entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn();
		nextStateIndex = 5;
		stateVector[5] = State.main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn;
		
		historyVector[5] = stateVector[5];
	}
	
	/* 'default' enter sequence for state lightOff */
	private void enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff_default() {
		entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff();
		nextStateIndex = 5;
		stateVector[5] = State.main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff;
		
		historyVector[5] = stateVector[5];
	}
	
	/* 'default' enter sequence for region main_region */
	private void enterSequence_main_region_default() {
		react_main_region__entry_Default();
	}
	
	/* 'default' enter sequence for region main */
	private void enterSequence_main_region_digitalWatch_main_default() {
		react_main_region_digitalWatch_main__entry_Default();
	}
	
	/* 'default' enter sequence for region buttons */
	private void enterSequence_main_region_digitalWatch_main_time_buttons_default() {
		react_main_region_digitalWatch_main_time_buttons__entry_Default();
	}
	
	/* 'default' enter sequence for region edit */
	private void enterSequence_main_region_digitalWatch_main_edit_edit_default() {
		react_main_region_digitalWatch_main_edit_edit__entry_Default();
	}
	
	/* 'default' enter sequence for region flash */
	private void enterSequence_main_region_digitalWatch_main_edit_edit_selection_flash_default() {
		react_main_region_digitalWatch_main_edit_edit_selection_flash__entry_Default();
	}
	
	/* shallow enterSequence with history in child flash */
	private void shallowEnterSequence_main_region_digitalWatch_main_edit_edit_selection_flash() {
		switch (historyVector[0]) {
		case main_region_digitalWatch_main_edit_edit_selection_flash_on:
			enterSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on_default();
			break;
		case main_region_digitalWatch_main_edit_edit_selection_flash_off:
			enterSequence_main_region_digitalWatch_main_edit_edit_selection_flash_off_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region time */
	private void enterSequence_main_region_digitalWatch_time_default() {
		react_main_region_digitalWatch_time__entry_Default();
	}
	
	/* shallow enterSequence with history in child time */
	private void shallowEnterSequence_main_region_digitalWatch_time() {
		switch (historyVector[1]) {
		case main_region_digitalWatch_time_running_buttons_brPress:
			enterSequence_main_region_digitalWatch_time_running_default();
			break;
		case main_region_digitalWatch_time_running_buttons_noButton:
			enterSequence_main_region_digitalWatch_time_running_default();
			break;
		case main_region_digitalWatch_time_running_buttons_blPress:
			enterSequence_main_region_digitalWatch_time_running_default();
			break;
		case main_region_digitalWatch_time_stopped_buttons_noButton:
			enterSequence_main_region_digitalWatch_time_stopped_default();
			break;
		case main_region_digitalWatch_time_stopped_buttons_brPress:
			enterSequence_main_region_digitalWatch_time_stopped_default();
			break;
		case main_region_digitalWatch_time_stopped_buttons_blPress:
			enterSequence_main_region_digitalWatch_time_stopped_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region buttons */
	private void enterSequence_main_region_digitalWatch_time_running_buttons_default() {
		react_main_region_digitalWatch_time_running_buttons__entry_Default();
	}
	
	/* 'default' enter sequence for region timeUpdate */
	private void enterSequence_main_region_digitalWatch_time_running_timeUpdate_default() {
		react_main_region_digitalWatch_time_running_timeUpdate__entry_Default();
	}
	
	/* 'default' enter sequence for region buttons */
	private void enterSequence_main_region_digitalWatch_time_stopped_buttons_default() {
		react_main_region_digitalWatch_time_stopped_buttons__entry_Default();
	}
	
	/* 'default' enter sequence for region chrono */
	private void enterSequence_main_region_digitalWatch_chrono_default() {
		react_main_region_digitalWatch_chrono__entry_Default();
	}
	
	/* shallow enterSequence with history in child chrono */
	private void shallowEnterSequence_main_region_digitalWatch_chrono() {
		switch (historyVector[2]) {
		case main_region_digitalWatch_chrono_chronoOff:
			enterSequence_main_region_digitalWatch_chrono_chronoOff_default();
			break;
		case main_region_digitalWatch_chrono_chronoOn:
			enterSequence_main_region_digitalWatch_chrono_chronoOn_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region light */
	private void enterSequence_main_region_digitalWatch_light_default() {
		react_main_region_digitalWatch_light__entry_Default();
	}
	
	/* 'default' enter sequence for region buttons */
	private void enterSequence_main_region_digitalWatch_light_lightOn_buttons_default() {
		react_main_region_digitalWatch_light_lightOn_buttons__entry_Default();
	}
	
	/* 'default' enter sequence for region alarm */
	private void enterSequence_main_region_digitalWatch_alarm_default() {
		react_main_region_digitalWatch_alarm__entry_Default();
	}
	
	/* deep enterSequence with history in child alarm */
	private void deepEnterSequence_main_region_digitalWatch_alarm() {
		switch (historyVector[3]) {
		case main_region_digitalWatch_alarm_off:
			enterSequence_main_region_digitalWatch_alarm_off_default();
			break;
		case main_region_digitalWatch_alarm_on:
			enterSequence_main_region_digitalWatch_alarm_on_default();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_snooze:
			deepEnterSequence_main_region_digitalWatch_alarm_alarmTriggered_on();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
			deepEnterSequence_main_region_digitalWatch_alarm_alarmTriggered_on();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
			deepEnterSequence_main_region_digitalWatch_alarm_alarmTriggered_on();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region on */
	private void enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_default() {
		react_main_region_digitalWatch_alarm_alarmTriggered_on__entry_Default();
	}
	
	/* deep enterSequence with history in child on */
	private void deepEnterSequence_main_region_digitalWatch_alarm_alarmTriggered_on() {
		switch (historyVector[4]) {
		case main_region_digitalWatch_alarm_alarmTriggered_on_snooze:
			enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_default();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
			entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
			deepEnterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
			entryAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
			deepEnterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1();
			break;
		default:
			break;
		}
	}
	
	/* shallow enterSequence with history in child on */
	private void shallowEnterSequence_main_region_digitalWatch_alarm_alarmTriggered_on() {
		switch (historyVector[4]) {
		case main_region_digitalWatch_alarm_alarmTriggered_on_snooze:
			enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_default();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
			enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_default();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
			enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_default();
			break;
		default:
			break;
		}
	}
	
	/* 'default' enter sequence for region r1 */
	private void enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_default() {
		react_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1__entry_Default();
	}
	
	/* deep enterSequence with history in child r1 */
	private void deepEnterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1() {
		switch (historyVector[5]) {
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
			enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_default();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
			enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff_default();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for state chrono */
	private void exitSequence_main_region_digitalWatch_main_chrono() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state time */
	private void exitSequence_main_region_digitalWatch_main_time() {
		exitSequence_main_region_digitalWatch_main_time_buttons();
	}
	
	/* Default exit sequence for state noButton */
	private void exitSequence_main_region_digitalWatch_main_time_buttons_noButton() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state blPress */
	private void exitSequence_main_region_digitalWatch_main_time_buttons_blPress() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_main_time_buttons_blPress();
	}
	
	/* Default exit sequence for state brPress */
	private void exitSequence_main_region_digitalWatch_main_time_buttons_brPress() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_main_time_buttons_brPress();
	}
	
	/* Default exit sequence for state edit */
	private void exitSequence_main_region_digitalWatch_main_edit() {
		exitSequence_main_region_digitalWatch_main_edit_edit();
	}
	
	/* Default exit sequence for state selection */
	private void exitSequence_main_region_digitalWatch_main_edit_edit_selection() {
		exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash();
		exitAction_main_region_digitalWatch_main_edit_edit_selection();
	}
	
	/* Default exit sequence for state on */
	private void exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_main_edit_edit_selection_flash_on();
	}
	
	/* Default exit sequence for state off */
	private void exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_off() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_main_edit_edit_selection_flash_off();
	}
	
	/* Default exit sequence for state blPress */
	private void exitSequence_main_region_digitalWatch_main_edit_edit_blPress() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_main_edit_edit_blPress();
	}
	
	/* Default exit sequence for state brPress */
	private void exitSequence_main_region_digitalWatch_main_edit_edit_brPress() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_main_edit_edit_brPress();
	}
	
	/* Default exit sequence for state running */
	private void exitSequence_main_region_digitalWatch_time_running() {
		exitSequence_main_region_digitalWatch_time_running_buttons();
		exitSequence_main_region_digitalWatch_time_running_timeUpdate();
	}
	
	/* Default exit sequence for state brPress */
	private void exitSequence_main_region_digitalWatch_time_running_buttons_brPress() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_time_running_buttons_brPress();
	}
	
	/* Default exit sequence for state noButton */
	private void exitSequence_main_region_digitalWatch_time_running_buttons_noButton() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for state blPress */
	private void exitSequence_main_region_digitalWatch_time_running_buttons_blPress() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_time_running_buttons_blPress();
	}
	
	/* Default exit sequence for state update */
	private void exitSequence_main_region_digitalWatch_time_running_timeUpdate_update() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_time_running_timeUpdate_update();
	}
	
	/* Default exit sequence for state stopped */
	private void exitSequence_main_region_digitalWatch_time_stopped() {
		exitSequence_main_region_digitalWatch_time_stopped_buttons();
	}
	
	/* Default exit sequence for state noButton */
	private void exitSequence_main_region_digitalWatch_time_stopped_buttons_noButton() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_time_stopped_buttons_noButton();
	}
	
	/* Default exit sequence for state brPress */
	private void exitSequence_main_region_digitalWatch_time_stopped_buttons_brPress() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_time_stopped_buttons_brPress();
	}
	
	/* Default exit sequence for state blPress */
	private void exitSequence_main_region_digitalWatch_time_stopped_buttons_blPress() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for state chronoOff */
	private void exitSequence_main_region_digitalWatch_chrono_chronoOff() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
	}
	
	/* Default exit sequence for state chronoOn */
	private void exitSequence_main_region_digitalWatch_chrono_chronoOn() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_chrono_chronoOn();
	}
	
	/* Default exit sequence for state lightOff */
	private void exitSequence_main_region_digitalWatch_light_lightOff() {
		nextStateIndex = 4;
		stateVector[4] = State.$NullState$;
	}
	
	/* Default exit sequence for state lightOn */
	private void exitSequence_main_region_digitalWatch_light_lightOn() {
		exitSequence_main_region_digitalWatch_light_lightOn_buttons();
	}
	
	/* Default exit sequence for state noButton */
	private void exitSequence_main_region_digitalWatch_light_lightOn_buttons_noButton() {
		nextStateIndex = 4;
		stateVector[4] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_light_lightOn_buttons_noButton();
	}
	
	/* Default exit sequence for state trPress */
	private void exitSequence_main_region_digitalWatch_light_lightOn_buttons_trPress() {
		nextStateIndex = 4;
		stateVector[4] = State.$NullState$;
	}
	
	/* Default exit sequence for state off */
	private void exitSequence_main_region_digitalWatch_alarm_off() {
		nextStateIndex = 5;
		stateVector[5] = State.$NullState$;
	}
	
	/* Default exit sequence for state on */
	private void exitSequence_main_region_digitalWatch_alarm_on() {
		nextStateIndex = 5;
		stateVector[5] = State.$NullState$;
	}
	
	/* Default exit sequence for state alarmTriggered */
	private void exitSequence_main_region_digitalWatch_alarm_alarmTriggered() {
		exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on();
	}
	
	/* Default exit sequence for state snooze */
	private void exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_snooze() {
		nextStateIndex = 5;
		stateVector[5] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_snooze();
	}
	
	/* Default exit sequence for state flashing */
	private void exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing() {
		exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1();
		exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
	}
	
	/* Default exit sequence for state lightOn */
	private void exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn() {
		nextStateIndex = 5;
		stateVector[5] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn();
	}
	
	/* Default exit sequence for state lightOff */
	private void exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff() {
		nextStateIndex = 5;
		stateVector[5] = State.$NullState$;
		
		exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff();
	}
	
	/* Default exit sequence for region main_region */
	private void exitSequence_main_region() {
		switch (stateVector[0]) {
		case main_region_digitalWatch_main_chrono:
			exitSequence_main_region_digitalWatch_main_chrono();
			break;
		case main_region_digitalWatch_main_time_buttons_noButton:
			exitSequence_main_region_digitalWatch_main_time_buttons_noButton();
			break;
		case main_region_digitalWatch_main_time_buttons_blPress:
			exitSequence_main_region_digitalWatch_main_time_buttons_blPress();
			break;
		case main_region_digitalWatch_main_time_buttons_brPress:
			exitSequence_main_region_digitalWatch_main_time_buttons_brPress();
			break;
		case main_region_digitalWatch_main_edit_edit_selection_flash_on:
			exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on();
			exitAction_main_region_digitalWatch_main_edit_edit_selection();
			break;
		case main_region_digitalWatch_main_edit_edit_selection_flash_off:
			exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_off();
			exitAction_main_region_digitalWatch_main_edit_edit_selection();
			break;
		case main_region_digitalWatch_main_edit_edit_blPress:
			exitSequence_main_region_digitalWatch_main_edit_edit_blPress();
			break;
		case main_region_digitalWatch_main_edit_edit_brPress:
			exitSequence_main_region_digitalWatch_main_edit_edit_brPress();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
		case main_region_digitalWatch_time_running_buttons_brPress:
			exitSequence_main_region_digitalWatch_time_running_buttons_brPress();
			break;
		case main_region_digitalWatch_time_running_buttons_noButton:
			exitSequence_main_region_digitalWatch_time_running_buttons_noButton();
			break;
		case main_region_digitalWatch_time_running_buttons_blPress:
			exitSequence_main_region_digitalWatch_time_running_buttons_blPress();
			break;
		case main_region_digitalWatch_time_stopped_buttons_noButton:
			exitSequence_main_region_digitalWatch_time_stopped_buttons_noButton();
			break;
		case main_region_digitalWatch_time_stopped_buttons_brPress:
			exitSequence_main_region_digitalWatch_time_stopped_buttons_brPress();
			break;
		case main_region_digitalWatch_time_stopped_buttons_blPress:
			exitSequence_main_region_digitalWatch_time_stopped_buttons_blPress();
			break;
		default:
			break;
		}
		
		switch (stateVector[2]) {
		case main_region_digitalWatch_time_running_timeUpdate_update:
			exitSequence_main_region_digitalWatch_time_running_timeUpdate_update();
			break;
		default:
			break;
		}
		
		switch (stateVector[3]) {
		case main_region_digitalWatch_chrono_chronoOff:
			exitSequence_main_region_digitalWatch_chrono_chronoOff();
			break;
		case main_region_digitalWatch_chrono_chronoOn:
			exitSequence_main_region_digitalWatch_chrono_chronoOn();
			break;
		default:
			break;
		}
		
		switch (stateVector[4]) {
		case main_region_digitalWatch_light_lightOff:
			exitSequence_main_region_digitalWatch_light_lightOff();
			break;
		case main_region_digitalWatch_light_lightOn_buttons_noButton:
			exitSequence_main_region_digitalWatch_light_lightOn_buttons_noButton();
			break;
		case main_region_digitalWatch_light_lightOn_buttons_trPress:
			exitSequence_main_region_digitalWatch_light_lightOn_buttons_trPress();
			break;
		default:
			break;
		}
		
		switch (stateVector[5]) {
		case main_region_digitalWatch_alarm_off:
			exitSequence_main_region_digitalWatch_alarm_off();
			break;
		case main_region_digitalWatch_alarm_on:
			exitSequence_main_region_digitalWatch_alarm_on();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_snooze:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_snooze();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn();
			exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff();
			exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region main */
	private void exitSequence_main_region_digitalWatch_main() {
		switch (stateVector[0]) {
		case main_region_digitalWatch_main_chrono:
			exitSequence_main_region_digitalWatch_main_chrono();
			break;
		case main_region_digitalWatch_main_time_buttons_noButton:
			exitSequence_main_region_digitalWatch_main_time_buttons_noButton();
			break;
		case main_region_digitalWatch_main_time_buttons_blPress:
			exitSequence_main_region_digitalWatch_main_time_buttons_blPress();
			break;
		case main_region_digitalWatch_main_time_buttons_brPress:
			exitSequence_main_region_digitalWatch_main_time_buttons_brPress();
			break;
		case main_region_digitalWatch_main_edit_edit_selection_flash_on:
			exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on();
			exitAction_main_region_digitalWatch_main_edit_edit_selection();
			break;
		case main_region_digitalWatch_main_edit_edit_selection_flash_off:
			exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_off();
			exitAction_main_region_digitalWatch_main_edit_edit_selection();
			break;
		case main_region_digitalWatch_main_edit_edit_blPress:
			exitSequence_main_region_digitalWatch_main_edit_edit_blPress();
			break;
		case main_region_digitalWatch_main_edit_edit_brPress:
			exitSequence_main_region_digitalWatch_main_edit_edit_brPress();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region buttons */
	private void exitSequence_main_region_digitalWatch_main_time_buttons() {
		switch (stateVector[0]) {
		case main_region_digitalWatch_main_time_buttons_noButton:
			exitSequence_main_region_digitalWatch_main_time_buttons_noButton();
			break;
		case main_region_digitalWatch_main_time_buttons_blPress:
			exitSequence_main_region_digitalWatch_main_time_buttons_blPress();
			break;
		case main_region_digitalWatch_main_time_buttons_brPress:
			exitSequence_main_region_digitalWatch_main_time_buttons_brPress();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region edit */
	private void exitSequence_main_region_digitalWatch_main_edit_edit() {
		switch (stateVector[0]) {
		case main_region_digitalWatch_main_edit_edit_selection_flash_on:
			exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on();
			exitAction_main_region_digitalWatch_main_edit_edit_selection();
			break;
		case main_region_digitalWatch_main_edit_edit_selection_flash_off:
			exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_off();
			exitAction_main_region_digitalWatch_main_edit_edit_selection();
			break;
		case main_region_digitalWatch_main_edit_edit_blPress:
			exitSequence_main_region_digitalWatch_main_edit_edit_blPress();
			break;
		case main_region_digitalWatch_main_edit_edit_brPress:
			exitSequence_main_region_digitalWatch_main_edit_edit_brPress();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region flash */
	private void exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash() {
		switch (stateVector[0]) {
		case main_region_digitalWatch_main_edit_edit_selection_flash_on:
			exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on();
			break;
		case main_region_digitalWatch_main_edit_edit_selection_flash_off:
			exitSequence_main_region_digitalWatch_main_edit_edit_selection_flash_off();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region time */
	private void exitSequence_main_region_digitalWatch_time() {
		switch (stateVector[1]) {
		case main_region_digitalWatch_time_running_buttons_brPress:
			exitSequence_main_region_digitalWatch_time_running_buttons_brPress();
			break;
		case main_region_digitalWatch_time_running_buttons_noButton:
			exitSequence_main_region_digitalWatch_time_running_buttons_noButton();
			break;
		case main_region_digitalWatch_time_running_buttons_blPress:
			exitSequence_main_region_digitalWatch_time_running_buttons_blPress();
			break;
		case main_region_digitalWatch_time_stopped_buttons_noButton:
			exitSequence_main_region_digitalWatch_time_stopped_buttons_noButton();
			break;
		case main_region_digitalWatch_time_stopped_buttons_brPress:
			exitSequence_main_region_digitalWatch_time_stopped_buttons_brPress();
			break;
		case main_region_digitalWatch_time_stopped_buttons_blPress:
			exitSequence_main_region_digitalWatch_time_stopped_buttons_blPress();
			break;
		default:
			break;
		}
		
		switch (stateVector[2]) {
		case main_region_digitalWatch_time_running_timeUpdate_update:
			exitSequence_main_region_digitalWatch_time_running_timeUpdate_update();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region buttons */
	private void exitSequence_main_region_digitalWatch_time_running_buttons() {
		switch (stateVector[1]) {
		case main_region_digitalWatch_time_running_buttons_brPress:
			exitSequence_main_region_digitalWatch_time_running_buttons_brPress();
			break;
		case main_region_digitalWatch_time_running_buttons_noButton:
			exitSequence_main_region_digitalWatch_time_running_buttons_noButton();
			break;
		case main_region_digitalWatch_time_running_buttons_blPress:
			exitSequence_main_region_digitalWatch_time_running_buttons_blPress();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region timeUpdate */
	private void exitSequence_main_region_digitalWatch_time_running_timeUpdate() {
		switch (stateVector[2]) {
		case main_region_digitalWatch_time_running_timeUpdate_update:
			exitSequence_main_region_digitalWatch_time_running_timeUpdate_update();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region buttons */
	private void exitSequence_main_region_digitalWatch_time_stopped_buttons() {
		switch (stateVector[1]) {
		case main_region_digitalWatch_time_stopped_buttons_noButton:
			exitSequence_main_region_digitalWatch_time_stopped_buttons_noButton();
			break;
		case main_region_digitalWatch_time_stopped_buttons_brPress:
			exitSequence_main_region_digitalWatch_time_stopped_buttons_brPress();
			break;
		case main_region_digitalWatch_time_stopped_buttons_blPress:
			exitSequence_main_region_digitalWatch_time_stopped_buttons_blPress();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region chrono */
	private void exitSequence_main_region_digitalWatch_chrono() {
		switch (stateVector[3]) {
		case main_region_digitalWatch_chrono_chronoOff:
			exitSequence_main_region_digitalWatch_chrono_chronoOff();
			break;
		case main_region_digitalWatch_chrono_chronoOn:
			exitSequence_main_region_digitalWatch_chrono_chronoOn();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region light */
	private void exitSequence_main_region_digitalWatch_light() {
		switch (stateVector[4]) {
		case main_region_digitalWatch_light_lightOff:
			exitSequence_main_region_digitalWatch_light_lightOff();
			break;
		case main_region_digitalWatch_light_lightOn_buttons_noButton:
			exitSequence_main_region_digitalWatch_light_lightOn_buttons_noButton();
			break;
		case main_region_digitalWatch_light_lightOn_buttons_trPress:
			exitSequence_main_region_digitalWatch_light_lightOn_buttons_trPress();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region buttons */
	private void exitSequence_main_region_digitalWatch_light_lightOn_buttons() {
		switch (stateVector[4]) {
		case main_region_digitalWatch_light_lightOn_buttons_noButton:
			exitSequence_main_region_digitalWatch_light_lightOn_buttons_noButton();
			break;
		case main_region_digitalWatch_light_lightOn_buttons_trPress:
			exitSequence_main_region_digitalWatch_light_lightOn_buttons_trPress();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region alarm */
	private void exitSequence_main_region_digitalWatch_alarm() {
		switch (stateVector[5]) {
		case main_region_digitalWatch_alarm_off:
			exitSequence_main_region_digitalWatch_alarm_off();
			break;
		case main_region_digitalWatch_alarm_on:
			exitSequence_main_region_digitalWatch_alarm_on();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_snooze:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_snooze();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn();
			exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff();
			exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region on */
	private void exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on() {
		switch (stateVector[5]) {
		case main_region_digitalWatch_alarm_alarmTriggered_on_snooze:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_snooze();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn();
			exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff();
			exitAction_main_region_digitalWatch_alarm_alarmTriggered_on_flashing();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region r1 */
	private void exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1() {
		switch (stateVector[5]) {
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn();
			break;
		case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
			exitSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff();
			break;
		default:
			break;
		}
	}
	
	/* The reactions of state chrono. */
	private void react_main_region_digitalWatch_main_chrono() {
		if (check_main_region_digitalWatch_main_chrono_tr0_tr0()) {
			effect_main_region_digitalWatch_main_chrono_tr0();
		}
	}
	
	/* The reactions of state noButton. */
	private void react_main_region_digitalWatch_main_time_buttons_noButton() {
		if (check_main_region_digitalWatch_main_time_tr0_tr0()) {
			effect_main_region_digitalWatch_main_time_tr0();
		} else {
			if (check_main_region_digitalWatch_main_time_buttons_noButton_tr0_tr0()) {
				effect_main_region_digitalWatch_main_time_buttons_noButton_tr0();
			} else {
				if (check_main_region_digitalWatch_main_time_buttons_noButton_tr1_tr1()) {
					effect_main_region_digitalWatch_main_time_buttons_noButton_tr1();
				}
			}
		}
	}
	
	/* The reactions of state blPress. */
	private void react_main_region_digitalWatch_main_time_buttons_blPress() {
		if (check_main_region_digitalWatch_main_time_tr0_tr0()) {
			effect_main_region_digitalWatch_main_time_tr0();
		} else {
			if (check_main_region_digitalWatch_main_time_buttons_blPress_tr0_tr0()) {
				effect_main_region_digitalWatch_main_time_buttons_blPress_tr0();
			} else {
				if (check_main_region_digitalWatch_main_time_buttons_blPress_tr1_tr1()) {
					effect_main_region_digitalWatch_main_time_buttons_blPress_tr1();
				}
			}
		}
	}
	
	/* The reactions of state brPress. */
	private void react_main_region_digitalWatch_main_time_buttons_brPress() {
		if (check_main_region_digitalWatch_main_time_tr0_tr0()) {
			effect_main_region_digitalWatch_main_time_tr0();
		} else {
			if (check_main_region_digitalWatch_main_time_buttons_brPress_tr0_tr0()) {
				effect_main_region_digitalWatch_main_time_buttons_brPress_tr0();
			} else {
				if (check_main_region_digitalWatch_main_time_buttons_brPress_tr1_tr1()) {
					effect_main_region_digitalWatch_main_time_buttons_brPress_tr1();
				}
			}
		}
	}
	
	/* The reactions of state on. */
	private void react_main_region_digitalWatch_main_edit_edit_selection_flash_on() {
		if (check_main_region_digitalWatch_main_edit_edit_selection_tr0_tr0()) {
			effect_main_region_digitalWatch_main_edit_edit_selection_tr0();
		} else {
			if (check_main_region_digitalWatch_main_edit_edit_selection_tr1_tr1()) {
				effect_main_region_digitalWatch_main_edit_edit_selection_tr1();
			} else {
				if (check_main_region_digitalWatch_main_edit_edit_selection_tr2_tr2()) {
					effect_main_region_digitalWatch_main_edit_edit_selection_tr2();
				} else {
					if (check_main_region_digitalWatch_main_edit_edit_selection_flash_on_tr0_tr0()) {
						effect_main_region_digitalWatch_main_edit_edit_selection_flash_on_tr0();
					}
				}
			}
		}
	}
	
	/* The reactions of state off. */
	private void react_main_region_digitalWatch_main_edit_edit_selection_flash_off() {
		if (check_main_region_digitalWatch_main_edit_edit_selection_tr0_tr0()) {
			effect_main_region_digitalWatch_main_edit_edit_selection_tr0();
		} else {
			if (check_main_region_digitalWatch_main_edit_edit_selection_tr1_tr1()) {
				effect_main_region_digitalWatch_main_edit_edit_selection_tr1();
			} else {
				if (check_main_region_digitalWatch_main_edit_edit_selection_tr2_tr2()) {
					effect_main_region_digitalWatch_main_edit_edit_selection_tr2();
				} else {
					if (check_main_region_digitalWatch_main_edit_edit_selection_flash_off_tr0_tr0()) {
						effect_main_region_digitalWatch_main_edit_edit_selection_flash_off_tr0();
					}
				}
			}
		}
	}
	
	/* The reactions of state blPress. */
	private void react_main_region_digitalWatch_main_edit_edit_blPress() {
		if (check_main_region_digitalWatch_main_edit_edit_blPress_tr0_tr0()) {
			effect_main_region_digitalWatch_main_edit_edit_blPress_tr0();
		} else {
			if (check_main_region_digitalWatch_main_edit_edit_blPress_tr1_tr1()) {
				effect_main_region_digitalWatch_main_edit_edit_blPress_tr1();
			}
		}
	}
	
	/* The reactions of state brPress. */
	private void react_main_region_digitalWatch_main_edit_edit_brPress() {
		if (check_main_region_digitalWatch_main_edit_edit_brPress_tr0_tr0()) {
			effect_main_region_digitalWatch_main_edit_edit_brPress_tr0();
		} else {
			if (check_main_region_digitalWatch_main_edit_edit_brPress_tr1_tr1()) {
				effect_main_region_digitalWatch_main_edit_edit_brPress_tr1();
			}
		}
	}
	
	/* The reactions of state brPress. */
	private void react_main_region_digitalWatch_time_running_buttons_brPress() {
		if (check_main_region_digitalWatch_time_running_buttons_brPress_tr0_tr0()) {
			effect_main_region_digitalWatch_time_running_buttons_brPress_tr0();
		} else {
			if (check_main_region_digitalWatch_time_running_buttons_brPress_tr1_tr1()) {
				effect_main_region_digitalWatch_time_running_buttons_brPress_tr1();
			}
		}
	}
	
	/* The reactions of state noButton. */
	private void react_main_region_digitalWatch_time_running_buttons_noButton() {
		if (check_main_region_digitalWatch_time_running_buttons_noButton_tr0_tr0()) {
			effect_main_region_digitalWatch_time_running_buttons_noButton_tr0();
		} else {
			if (check_main_region_digitalWatch_time_running_buttons_noButton_tr1_tr1()) {
				effect_main_region_digitalWatch_time_running_buttons_noButton_tr1();
			}
		}
	}
	
	/* The reactions of state blPress. */
	private void react_main_region_digitalWatch_time_running_buttons_blPress() {
		if (check_main_region_digitalWatch_time_running_buttons_blPress_tr0_tr0()) {
			effect_main_region_digitalWatch_time_running_buttons_blPress_tr0();
		} else {
			if (check_main_region_digitalWatch_time_running_buttons_blPress_tr1_tr1()) {
				effect_main_region_digitalWatch_time_running_buttons_blPress_tr1();
			}
		}
	}
	
	/* The reactions of state update. */
	private void react_main_region_digitalWatch_time_running_timeUpdate_update() {
		if (check_main_region_digitalWatch_time_running_timeUpdate_update_tr0_tr0()) {
			effect_main_region_digitalWatch_time_running_timeUpdate_update_tr0();
		}
	}
	
	/* The reactions of state noButton. */
	private void react_main_region_digitalWatch_time_stopped_buttons_noButton() {
		if (check_main_region_digitalWatch_time_stopped_buttons_noButton_tr0_tr0()) {
			effect_main_region_digitalWatch_time_stopped_buttons_noButton_tr0();
		} else {
			if (check_main_region_digitalWatch_time_stopped_buttons_noButton_tr1_tr1()) {
				effect_main_region_digitalWatch_time_stopped_buttons_noButton_tr1();
			} else {
				if (check_main_region_digitalWatch_time_stopped_buttons_noButton_tr2_tr2()) {
					effect_main_region_digitalWatch_time_stopped_buttons_noButton_tr2();
				}
			}
		}
	}
	
	/* The reactions of state brPress. */
	private void react_main_region_digitalWatch_time_stopped_buttons_brPress() {
		if (check_main_region_digitalWatch_time_stopped_buttons_brPress_tr0_tr0()) {
			effect_main_region_digitalWatch_time_stopped_buttons_brPress_tr0();
		} else {
			if (check_main_region_digitalWatch_time_stopped_buttons_brPress_tr1_tr1()) {
				effect_main_region_digitalWatch_time_stopped_buttons_brPress_tr1();
			}
		}
	}
	
	/* The reactions of state blPress. */
	private void react_main_region_digitalWatch_time_stopped_buttons_blPress() {
		if (check_main_region_digitalWatch_time_stopped_buttons_blPress_tr0_tr0()) {
			effect_main_region_digitalWatch_time_stopped_buttons_blPress_tr0();
		}
	}
	
	/* The reactions of state chronoOff. */
	private void react_main_region_digitalWatch_chrono_chronoOff() {
		if (check_main_region_digitalWatch_chrono_chronoOff_tr0_tr0()) {
			effect_main_region_digitalWatch_chrono_chronoOff_tr0();
		} else {
			if (check_main_region_digitalWatch_chrono_chronoOff_tr1_tr1()) {
				effect_main_region_digitalWatch_chrono_chronoOff_tr1();
			}
		}
	}
	
	/* The reactions of state chronoOn. */
	private void react_main_region_digitalWatch_chrono_chronoOn() {
		if (check_main_region_digitalWatch_chrono_chronoOn_tr0_tr0()) {
			effect_main_region_digitalWatch_chrono_chronoOn_tr0();
		} else {
			if (check_main_region_digitalWatch_chrono_chronoOn_tr1_tr1()) {
				effect_main_region_digitalWatch_chrono_chronoOn_tr1();
			} else {
				if (check_main_region_digitalWatch_chrono_chronoOn_tr2_tr2()) {
					effect_main_region_digitalWatch_chrono_chronoOn_tr2();
				}
			}
		}
	}
	
	/* The reactions of state lightOff. */
	private void react_main_region_digitalWatch_light_lightOff() {
		if (check_main_region_digitalWatch_light_lightOff_tr0_tr0()) {
			effect_main_region_digitalWatch_light_lightOff_tr0();
		}
	}
	
	/* The reactions of state noButton. */
	private void react_main_region_digitalWatch_light_lightOn_buttons_noButton() {
		if (check_main_region_digitalWatch_light_lightOn_buttons_noButton_tr0_tr0()) {
			effect_main_region_digitalWatch_light_lightOn_buttons_noButton_tr0();
		} else {
			if (check_main_region_digitalWatch_light_lightOn_buttons_noButton_tr1_tr1()) {
				effect_main_region_digitalWatch_light_lightOn_buttons_noButton_tr1();
			}
		}
	}
	
	/* The reactions of state trPress. */
	private void react_main_region_digitalWatch_light_lightOn_buttons_trPress() {
		if (check_main_region_digitalWatch_light_lightOn_buttons_trPress_tr0_tr0()) {
			effect_main_region_digitalWatch_light_lightOn_buttons_trPress_tr0();
		}
	}
	
	/* The reactions of state off. */
	private void react_main_region_digitalWatch_alarm_off() {
		if (check_main_region_digitalWatch_alarm_off_tr0_tr0()) {
			effect_main_region_digitalWatch_alarm_off_tr0();
		}
	}
	
	/* The reactions of state on. */
	private void react_main_region_digitalWatch_alarm_on() {
		if (check_main_region_digitalWatch_alarm_on_tr0_tr0()) {
			effect_main_region_digitalWatch_alarm_on_tr0();
		} else {
			if (check_main_region_digitalWatch_alarm_on_tr1_tr1()) {
				effect_main_region_digitalWatch_alarm_on_tr1();
			}
		}
	}
	
	/* The reactions of state snooze. */
	private void react_main_region_digitalWatch_alarm_alarmTriggered_on_snooze() {
		if (check_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_tr0_tr0()) {
			effect_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_tr0();
		} else {
			if (check_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_tr1_tr1()) {
				effect_main_region_digitalWatch_alarm_alarmTriggered_on_snooze_tr1();
			}
		}
	}
	
	/* The reactions of state lightOn. */
	private void react_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn() {
		if (check_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_tr0_tr0()) {
			effect_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_tr0();
		} else {
			if (check_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_tr0_tr0()) {
				effect_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_tr0();
			} else {
				if (check_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_tr1_tr1()) {
					effect_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_tr1();
				}
			}
		}
	}
	
	/* The reactions of state lightOff. */
	private void react_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff() {
		if (check_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_tr0_tr0()) {
			effect_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_tr0();
		} else {
			if (check_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff_tr0_tr0()) {
				effect_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff_tr0();
			}
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region__entry_Default() {
		enterSequence_main_region_digitalWatch_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_digitalWatch_main_time_buttons__entry_Default() {
		enterSequence_main_region_digitalWatch_main_time_buttons_noButton_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_digitalWatch_main_edit_edit__entry_Default() {
		enterSequence_main_region_digitalWatch_main_edit_edit_selection_default();
	}
	
	/* Default react sequence for shallow history entry  */
	private void react_main_region_digitalWatch_main_edit_edit_selection_flash__entry_Default() {
		/* Enter the region with shallow history */
		if (historyVector[0] != State.$NullState$) {
			shallowEnterSequence_main_region_digitalWatch_main_edit_edit_selection_flash();
		} else {
			enterSequence_main_region_digitalWatch_main_edit_edit_selection_flash_on_default();
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_digitalWatch_main__entry_Default() {
		enterSequence_main_region_digitalWatch_main_time_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_digitalWatch_time_running_buttons__entry_Default() {
		enterSequence_main_region_digitalWatch_time_running_buttons_noButton_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_digitalWatch_time_running_timeUpdate__entry_Default() {
		enterSequence_main_region_digitalWatch_time_running_timeUpdate_update_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_digitalWatch_time_stopped_buttons__entry_Default() {
		enterSequence_main_region_digitalWatch_time_stopped_buttons_noButton_default();
	}
	
	/* Default react sequence for shallow history entry  */
	private void react_main_region_digitalWatch_time__entry_Default() {
		/* Enter the region with shallow history */
		if (historyVector[1] != State.$NullState$) {
			shallowEnterSequence_main_region_digitalWatch_time();
		} else {
			enterSequence_main_region_digitalWatch_time_running_default();
		}
	}
	
	/* Default react sequence for shallow history entry  */
	private void react_main_region_digitalWatch_chrono__entry_Default() {
		/* Enter the region with shallow history */
		if (historyVector[2] != State.$NullState$) {
			shallowEnterSequence_main_region_digitalWatch_chrono();
		} else {
			enterSequence_main_region_digitalWatch_chrono_chronoOff_default();
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_digitalWatch_light__entry_Default() {
		enterSequence_main_region_digitalWatch_light_lightOff_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_digitalWatch_light_lightOn_buttons__entry_Default() {
		enterSequence_main_region_digitalWatch_light_lightOn_buttons_trPress_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1__entry_Default() {
		enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn_default();
	}
	
	/* Default react sequence for shallow history entry  */
	private void react_main_region_digitalWatch_alarm_alarmTriggered_on__entry_Default() {
		/* Enter the region with shallow history */
		if (historyVector[4] != State.$NullState$) {
			shallowEnterSequence_main_region_digitalWatch_alarm_alarmTriggered_on();
		} else {
			enterSequence_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_default();
		}
	}
	
	/* Default react sequence for deep history entry  */
	private void react_main_region_digitalWatch_alarm__entry_Default() {
		/* Enter the region with deep history */
		if (historyVector[3] != State.$NullState$) {
			deepEnterSequence_main_region_digitalWatch_alarm();
		} else {
			enterSequence_main_region_digitalWatch_alarm_off_default();
		}
	}
	
	public void runCycle() {
		if (!initialized)
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		clearOutEvents();
		for (nextStateIndex = 0; nextStateIndex < stateVector.length; nextStateIndex++) {
			switch (stateVector[nextStateIndex]) {
			case main_region_digitalWatch_main_chrono:
				react_main_region_digitalWatch_main_chrono();
				break;
			case main_region_digitalWatch_main_time_buttons_noButton:
				react_main_region_digitalWatch_main_time_buttons_noButton();
				break;
			case main_region_digitalWatch_main_time_buttons_blPress:
				react_main_region_digitalWatch_main_time_buttons_blPress();
				break;
			case main_region_digitalWatch_main_time_buttons_brPress:
				react_main_region_digitalWatch_main_time_buttons_brPress();
				break;
			case main_region_digitalWatch_main_edit_edit_selection_flash_on:
				react_main_region_digitalWatch_main_edit_edit_selection_flash_on();
				break;
			case main_region_digitalWatch_main_edit_edit_selection_flash_off:
				react_main_region_digitalWatch_main_edit_edit_selection_flash_off();
				break;
			case main_region_digitalWatch_main_edit_edit_blPress:
				react_main_region_digitalWatch_main_edit_edit_blPress();
				break;
			case main_region_digitalWatch_main_edit_edit_brPress:
				react_main_region_digitalWatch_main_edit_edit_brPress();
				break;
			case main_region_digitalWatch_time_running_buttons_brPress:
				react_main_region_digitalWatch_time_running_buttons_brPress();
				break;
			case main_region_digitalWatch_time_running_buttons_noButton:
				react_main_region_digitalWatch_time_running_buttons_noButton();
				break;
			case main_region_digitalWatch_time_running_buttons_blPress:
				react_main_region_digitalWatch_time_running_buttons_blPress();
				break;
			case main_region_digitalWatch_time_running_timeUpdate_update:
				react_main_region_digitalWatch_time_running_timeUpdate_update();
				break;
			case main_region_digitalWatch_time_stopped_buttons_noButton:
				react_main_region_digitalWatch_time_stopped_buttons_noButton();
				break;
			case main_region_digitalWatch_time_stopped_buttons_brPress:
				react_main_region_digitalWatch_time_stopped_buttons_brPress();
				break;
			case main_region_digitalWatch_time_stopped_buttons_blPress:
				react_main_region_digitalWatch_time_stopped_buttons_blPress();
				break;
			case main_region_digitalWatch_chrono_chronoOff:
				react_main_region_digitalWatch_chrono_chronoOff();
				break;
			case main_region_digitalWatch_chrono_chronoOn:
				react_main_region_digitalWatch_chrono_chronoOn();
				break;
			case main_region_digitalWatch_light_lightOff:
				react_main_region_digitalWatch_light_lightOff();
				break;
			case main_region_digitalWatch_light_lightOn_buttons_noButton:
				react_main_region_digitalWatch_light_lightOn_buttons_noButton();
				break;
			case main_region_digitalWatch_light_lightOn_buttons_trPress:
				react_main_region_digitalWatch_light_lightOn_buttons_trPress();
				break;
			case main_region_digitalWatch_alarm_off:
				react_main_region_digitalWatch_alarm_off();
				break;
			case main_region_digitalWatch_alarm_on:
				react_main_region_digitalWatch_alarm_on();
				break;
			case main_region_digitalWatch_alarm_alarmTriggered_on_snooze:
				react_main_region_digitalWatch_alarm_alarmTriggered_on_snooze();
				break;
			case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn:
				react_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOn();
				break;
			case main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff:
				react_main_region_digitalWatch_alarm_alarmTriggered_on_flashing_r1_lightOff();
				break;
			default:
				// $NullState$
			}
		}
		clearEvents();
	}
}

package org.yakindu.scr.test;

import org.yakindu.scr.RuntimeService;
import org.yakindu.scr.digitalwatch.DigitalwatchStatemachine;
import org.yakindu.scr.impl.DigitalWatchController;
import org.yakindu.scr.impl.DigitalWatchViewImpl;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DigitalwatchStatemachine sm = new DigitalwatchStatemachine();
		sm.setTimer(new TimerService());
		DigitalWatchController controller = new DigitalWatchController(sm.getSCILogicUnit());
		DigitalWatchViewImpl view = new DigitalWatchViewImpl(controller, sm.getSCIButtons(), sm);
		sm.getSCIDisplay().setSCIDisplayOperationCallback(view);
		sm.getSCILogicUnit().setSCILogicUnitOperationCallback(controller);

		sm.init();
		sm.enter();
		
		RuntimeService.getInstance().registerStatemachine(sm,1); 
	}

}

package pcd.ass01sol01.simtrafficexamples;

import pcd.ass01sol01.simengineseq.AbstractAgent;
import pcd.ass01sol01.simengineseq.AbstractEnvironment;

import java.util.List;

/**
 * Controller part of the application - passive part.
 * 
 * @author aricci
 *
 */
public class Controller implements ViewListener {

	private StartSynch synch;
	private Flag stopFlag;
	
	public Controller(StartSynch synch, Flag stopFlag){
		this.synch = synch;
		this.stopFlag = stopFlag;
	}
	
	public synchronized void started(int nStep){
		stopFlag.reset();
		synch.notifyStarted(nStep);
	}

	public synchronized void stopped() {
		stopFlag.set();
	}

}

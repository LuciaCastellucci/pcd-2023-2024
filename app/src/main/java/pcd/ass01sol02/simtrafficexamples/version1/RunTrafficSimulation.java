package pcd.ass01sol02.simtrafficexamples.version1;

/**
 * 
 * Main class to create and run a simulation
 * 
 */
public class RunTrafficSimulation {

	public static void main(String[] args) {
		StartSynch synch = new StartSynch();
		Flag stopFlag = new Flag();
		Controller controller = new Controller(synch, stopFlag);

		// var simulation = new TrafficSimulationSingleRoadTwoCars(stopFlag, synch);
		// var simulation = new TrafficSimulationSingleRoadSeveralCars(stopFlag, synch);
		// var simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars(stopFlag, synch);
		var simulation = new TrafficSimulationWithCrossRoads(stopFlag, synch);
		simulation.setup();

		StatisticsListener stat = new StatisticsListener();
		simulation.addSimulationListener(stat);

		RoadSimView view = new RoadSimView();
		StateListener stateListener = new StateListener(view);
		simulation.addSimulationListener(stateListener);

		view.setViewListener(controller);
		view.display();
		simulation.run();
	}
}

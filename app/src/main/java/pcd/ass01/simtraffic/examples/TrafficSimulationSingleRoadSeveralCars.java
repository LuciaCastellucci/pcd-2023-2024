package pcd.ass01.simtraffic.examples;

import pcd.ass01.sim.engine.conc.*;
import pcd.ass01.sim.model.AbstractSimulationModel;
import pcd.ass01.simtraffic.model.*;

/**
 * 
 * Traffic Simulation about a number of cars 
 * moving on a single road, no traffic lights
 * 
 */
public class TrafficSimulationSingleRoadSeveralCars extends AbstractSimulationModel {

	public TrafficSimulationSingleRoadSeveralCars() {
		super();
	}
	
	public void init() {

		RoadsEnv env = new RoadsEnv();
		this.setEnvironment(env);
		
		Road road = env.createRoad(new P2d(0,300), new P2d(1500,300));

		int nCars = 30;

		for (int i = 0; i < nCars; i++) {
			
			String carId = "car-" + i;
			// double initialPos = i*30;
			double initialPos = i*10;
			
			double carAcceleration = 1; //  + gen.nextDouble()/2;
			double carDeceleration = 0.3; //  + gen.nextDouble()/2;
			double carMaxSpeed = 7; // 4 + gen.nextDouble();
						
			AbstractCar car = new CarBasic(carId, env, 
									road,
									initialPos, 
									carAcceleration, 
									carDeceleration,
									carMaxSpeed);
			this.addAgent(car);
		}
	}	
}
	
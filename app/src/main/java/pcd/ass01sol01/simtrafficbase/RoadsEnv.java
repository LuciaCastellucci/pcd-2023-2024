package pcd.ass01sol01.simtrafficbase;

import pcd.ass01sol01.simengineseq.AbstractEnvironment;
import pcd.ass01sol01.simengineseq.Action;
import pcd.ass01sol01.simengineseq.Percept;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoadsEnv extends AbstractEnvironment {
		
	private static final int MIN_DIST_ALLOWED = 5;
	private static final int CAR_DETECTION_RANGE = 30;
	private static final int SEM_DETECTION_RANGE = 30;
	
	/* list of roads */
	private List<Road> roads;

	/* traffic lights */
	private List<TrafficLight> trafficLights;
	
	/* cars situated in the environment */	
	private HashMap<String, CarAgentInfo> registeredCars;

	private HashMap<String, Lock> locks;

	public RoadsEnv() {
		super("traffic-env");
		registeredCars = new HashMap<>();	
		trafficLights = new ArrayList<>();
		roads = new ArrayList<>();
		locks = new HashMap<>();
	}
	
	@Override
	public void init() {
		for (var tl: trafficLights) {
			tl.init();
		}
	}
	
	@Override
	public void step(int dt) {
		for (var tl: trafficLights) {
			tl.step(dt);
		}
	}
	
	public void registerNewCar(CarAgent car, Road road, double pos) {
		registeredCars.put(car.getId(), new CarAgentInfo(car, road, pos));
		locks.put(car.getId(), new ReentrantLock());
	}

	public Road createRoad(P2d p0, P2d p1) {
		Road r = new Road(p0, p1);
		this.roads.add(r);
		return r;
	}

	public TrafficLight createTrafficLight(P2d pos, TrafficLight.TrafficLightState initialState, int greenDuration, int yellowDuration, int redDuration) {
		TrafficLight tl = new TrafficLight(pos, initialState, greenDuration, yellowDuration, redDuration);
		this.trafficLights.add(tl);
		return tl;
	}

	@Override
	public Percept getCurrentPercepts(String agentId) {
		try {
			locks.get(agentId).lock();
			CarAgentInfo carInfo = registeredCars.get(agentId);
			double pos = carInfo.getPos();
			Road road = carInfo.getRoad();
			Optional<CarAgentInfo> nearestCar = getNearestCarInFront(agentId, road,pos, CAR_DETECTION_RANGE);
			Optional<TrafficLightInfo> nearestSem = getNearestSemaphoreInFront(road,pos, SEM_DETECTION_RANGE);
			return new CarPercept(pos, nearestCar, nearestSem);
		 }
		 finally {
			locks.get(agentId).unlock();
		}
	}

	private Optional<CarAgentInfo> getNearestCarInFront(String currentAgentId, Road road, double carPos, double range){
		Optional<CarAgentInfo> closestCar = Optional.empty();
		double minDistance = Double.MAX_VALUE;

		for (Map.Entry<String, CarAgentInfo> registeredCar : registeredCars.entrySet()) {
			CarAgentInfo carInfo = registeredCar.getValue();
			if (currentAgentId.equalsIgnoreCase(registeredCar.getKey())) {
				if (carInfo.getRoad() == road) {
					double dist = carInfo.getPos() - carPos;
					if (dist > 0 && dist <= range && dist < minDistance) {
						minDistance = dist;
						closestCar = Optional.of(carInfo);
					}
				}
			} else {
				try {
					locks.get(currentAgentId).lock();
					if (carInfo.getRoad() == road) {
						double dist = carInfo.getPos() - carPos;
						if (dist > 0 && dist <= range && dist < minDistance) {
							minDistance = dist;
							closestCar = Optional.of(carInfo);
						}
					}
				} finally {
					locks.get(currentAgentId).unlock();
				}
			}
		}
		return closestCar;
	}

	private Optional<TrafficLightInfo> getNearestSemaphoreInFront(Road road, double carPos, double range){
		return
				road.getTrafficLights()
				.stream()
				.filter((TrafficLightInfo tl) -> tl.roadPos() > carPos)
				.min((c1, c2) -> (int) Math.round(c1.roadPos() - c2.roadPos()));
	}
	
	
	@Override
	public void doAction(String agentId, Action act) {
		try {
			locks.get(agentId).lock();
			switch (act) {
			case MoveForward mv: {
				CarAgentInfo info = registeredCars.get(agentId);
				Road road = info.getRoad();
				Optional<CarAgentInfo> nearestCar = getNearestCarInFront(agentId, road, info.getPos(), CAR_DETECTION_RANGE);

				if (nearestCar.isPresent()) {
					double dist = nearestCar.get().getPos() - info.getPos();
					if (dist > mv.distance() + MIN_DIST_ALLOWED) {
						info.updatePos(info.getPos() + mv.distance());
					}
				} else {
					info.updatePos(info.getPos() + mv.distance());
				}

				if (info.getPos() > road.getLen()) {
					info.updatePos(0);
				}
				break;
			}
			default: break;
			}
		} finally {
			locks.get(agentId).unlock();
		}
	}
	
	public List<CarAgentInfo> getAgentInfo(){
		return this.registeredCars.values().stream().toList();
	}

	public List<Road> getRoads(){
		return roads;
	}
	
	public List<TrafficLight> getTrafficLights(){
		return trafficLights;
	}
}

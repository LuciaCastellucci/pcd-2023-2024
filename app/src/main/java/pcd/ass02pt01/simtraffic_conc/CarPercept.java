package pcd.ass02pt01.simtraffic_conc;

import pcd.ass02pt01.simengine_conc.Percept;

import java.util.Optional;

/**
 * 
 * Percept for Car Agents
 * 
 * - position on the road
 * - nearest car, if present (distance)
 * - nearest semaphore, if present (distance)
 * 
 */
public record CarPercept(double roadPos, Optional<AbstractCar> nearestCarInFront, Optional<TrafficLightInfo> nearestSem) implements Percept { }
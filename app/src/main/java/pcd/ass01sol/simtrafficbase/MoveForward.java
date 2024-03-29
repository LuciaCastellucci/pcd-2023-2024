package pcd.ass01sol.simtrafficbase;

import pcd.ass01sol.simengineseq.Action;

/**
 * Car agent move forward action
 */
public record MoveForward(double distance) implements Action {}

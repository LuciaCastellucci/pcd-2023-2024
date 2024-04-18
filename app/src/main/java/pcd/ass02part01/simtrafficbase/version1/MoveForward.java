package pcd.ass02part01.simtrafficbase.version1;

import pcd.ass02part01.simengineseq.version1.Action;

/**
 * Car agent move forward action
 */
public record MoveForward(String agentId, double distance) implements Action {}

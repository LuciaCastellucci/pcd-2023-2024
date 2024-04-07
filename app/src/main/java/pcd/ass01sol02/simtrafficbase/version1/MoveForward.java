package pcd.ass01sol02.simtrafficbase.version1;

import pcd.ass01sol02.simengineseq.version1.Action;

/**
 * Car agent move forward action
 */
public record MoveForward(String agentId, double distance) implements Action {}

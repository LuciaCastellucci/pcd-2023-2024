package pcd.ass01.simtrafficbase_improved;

import pcd.ass01.simengineseq_improved.Action;

/**
 * Car agent move forward action
 */
public record MoveForward(String agentId, double distance) implements Action {}

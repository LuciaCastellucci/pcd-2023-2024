package pcd.ass01sol01.simtrafficbase;

import pcd.ass01.simengineseq.Action;

/**
 * Car agent move forward action
 */
public record MoveForward(double distance) implements Action {}

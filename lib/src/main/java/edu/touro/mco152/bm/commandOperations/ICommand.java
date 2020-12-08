package edu.touro.mco152.bm.commandOperations;

/**
 * This is a basic command interface which stipulates one boolean method named execute(). The returned boolean value can
 * be used to determine success or failure.
 */
public interface ICommand {
    boolean execute();
}

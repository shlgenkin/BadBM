package edu.touro.mco152.bm.commandOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a simple serial invoker/executor. It contains one method titled executeCommand(), which accepts an
 * ICommand object (a type of a command object), adds the command object to a list, runs that object's execute() method,
 * and returns the boolean result. The command objects are stored to facilitate the implementation of some future
 * features, such as an undo feature.
 */
public class SerialCommandExecutor {
    private final List<ICommand> commandsList = new ArrayList<>();

    public boolean executeCommand(ICommand inputCommand) {
        commandsList.add(inputCommand);
        return inputCommand.execute();
    }
}

package edu.touro.mco152.bm.commandOperations;

import edu.touro.mco152.bm.DiskWorkerInterface;
import edu.touro.mco152.bm.persist.DiskRun;

import java.util.InputMismatchException;

/**
 * This is a builder class that allows for better usage of the various command classes. This class accepts all of the
 * same arguments that the command classes accept in addition to a string commandType parameter, which specifies the
 * type of command. This is accomplished via a switch statement that matches (case-insensitive) the provided string to
 * preset options such as "read" and "write" when build() is called. If an unmatched string is set when the build()
 * method is invoked, an exception is thrown.
 */
public class CommandBuilder {

    private String commandType;
    private DiskWorkerInterface dWModel;
    private int numOfMarks;
    private int numOfBlocks;
    private int blockSizeKB;
    private DiskRun.BlockSequence blockSeq;

    public CommandBuilder setCommandType(String commandType) {
        this.commandType = commandType.toLowerCase();
        return this;
    }

    public CommandBuilder setDWModel(DiskWorkerInterface dWModel) {
        this.dWModel = dWModel;
        return this;
    }

    public CommandBuilder setNumOfMarks(int numOfMarks) {
        this.numOfMarks = numOfMarks;
        return this;
    }

    public CommandBuilder setNumOfBlocks(int numOfBlocks) {
        this.numOfBlocks = numOfBlocks;
        return this;
    }

    public CommandBuilder setBlockSizeKB(int blockSizeKB) {
        this.blockSizeKB = blockSizeKB;
        return this;
    }

    public CommandBuilder setBlockSeq(DiskRun.BlockSequence blockSeq) {
        this.blockSeq = blockSeq;
        return this;
    }

    public ICommand build() {
        //Return a different command depending on the string provided for commandType,
        switch (commandType) {
            case "write":
                return new WriteCommand(dWModel, numOfMarks, numOfBlocks, blockSizeKB, blockSeq);
            case "read":
                return new ReadCommand(dWModel, numOfMarks, numOfBlocks, blockSizeKB, blockSeq);
            default:
                throw new InputMismatchException("An incorrect command type string was specified.");
                //Throw an error if the string provided doesn't match one of the options.
        }
    }
}

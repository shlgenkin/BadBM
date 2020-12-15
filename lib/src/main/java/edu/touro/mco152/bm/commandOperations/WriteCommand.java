package edu.touro.mco152.bm.commandOperations;

import edu.touro.mco152.bm.DiskWorkerInterface;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * This command class implements the ICommand interface and encapsulates all of the necessary steps for a write
 * operation in the execute() method. However, this class's constructor requires multiple arguments, including a
 * DiskWorkerInterface, that are passed into the receiver that actually performs the specified task.
 */
public class WriteCommand implements ICommand {

    private ReceiverWriteRead receiverWriteRead;

    public WriteCommand(DiskWorkerInterface inputDWModel,
                        int inNumOMarks, int inNumOBlocks, int inBlockSizeKB, DiskRun.BlockSequence inBlockSeq) {
        receiverWriteRead = new ReceiverWriteRead(inputDWModel, inNumOMarks, inNumOBlocks, inBlockSizeKB, inBlockSeq);
    }

    @Override
    public boolean execute() {
        return receiverWriteRead.writeOperation();
    }
}

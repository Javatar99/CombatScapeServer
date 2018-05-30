package RS2.model.player.packets.commands;

import RS2.model.player.Client;
import RS2.model.player.PlayerHandler;

import java.util.Arrays;

/**
 * @author david (Javatar)
 */

public class CommandParser {

    private String splitPrefix;
    private String[] commandData;
    private int position;

    public CommandParser(String splitPrefix, String commandString) {
        this.splitPrefix = splitPrefix;
        this.commandData = commandString.split(this.splitPrefix);
        this.position = 1;
    }

    @Override
    public String toString() {
        return "PreFix: " + this.splitPrefix + " :: " + Arrays.toString(commandData);
    }

    public int size(){
        return this.commandData.length - 1;
    }

    public CommandParser(String commandString) {
        this(commandString, "");
    }

    public void setSplitPrefix(String splitPrefix){
        this.splitPrefix = splitPrefix;
    }

    public String getCommandName(){
        return commandData[0];
    }

    public int readInt() throws NoPrefixException, NotEnoughDataException {
        if(splitPrefix.isEmpty())
            throw new NoPrefixException();
        if(this.position >= commandData.length)
            throw new NotEnoughDataException("Not Enough Data!");
        return Integer.parseInt(commandData[this.position++]);
    }

    public double readDouble() throws NoPrefixException, NotEnoughDataException {
        if(splitPrefix.isEmpty())
            throw new NoPrefixException();
        if(this.position >= commandData.length)
            throw new NotEnoughDataException("Not Enough Data!");
        return Double.parseDouble(commandData[this.position++]);
    }

    public Client getClient() throws NoPrefixException, NotEnoughDataException {
        if(splitPrefix.isEmpty())
            throw new NoPrefixException("Invalid name.");
        if(this.position >= commandData.length)
            throw new NotEnoughDataException("Not Enough Data!");
        return PlayerHandler.getPlayerByName(commandData[this.position++]);
    }

}

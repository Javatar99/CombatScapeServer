package RS2.model.player.packets.commands;

import RS2.model.player.Client;
import RS2.model.player.PlayerHandler;

import java.util.Arrays;

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
        return this.commandData.length;
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

    public int readInt() throws NoPrefixException {
        if(splitPrefix.isEmpty())
            throw new NoPrefixException();
        return Integer.parseInt(commandData[this.position++]);
    }

    public double readDouble() throws NoPrefixException {
        if(splitPrefix.isEmpty())
            throw new NoPrefixException();
        return Double.parseDouble(commandData[this.position++]);
    }

    public Client getClient() throws NoPrefixException {
        if(splitPrefix.isEmpty())
            throw new NoPrefixException("Invalid name.");
        return PlayerHandler.getPlayerByName(commandData[this.position++]);
    }

}
package RS2.model.player.packets.commands;

/**
 * @author david (Javatar)
 */

public class NoPrefixException extends Exception{

    public NoPrefixException() {
        super();
    }

    public NoPrefixException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "No prefix set.";
    }
}

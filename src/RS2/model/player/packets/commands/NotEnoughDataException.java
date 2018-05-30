package RS2.model.player.packets.commands;

/**
 * @author david (Javatar)
 */

public class NotEnoughDataException extends Exception{
    public NotEnoughDataException(String message) {
        super(message);
    }
}

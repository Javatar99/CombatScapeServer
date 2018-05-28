package RS2.model.player.packets.commands;

public class NotEnoughDataException extends Exception{
    public NotEnoughDataException(String message) {
        super(message);
    }
}

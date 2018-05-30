package RS2.model.player.dialogues;

import RS2.model.player.Client;

/**
 * @author david (Javatar)
 */

@FunctionalInterface
public interface OptionAction {

    /**
     *
     * @param c - The player
     * @return if the option dialogue is finished, if the dialogue is finished all windows are closed and the current
     * player option dialogue is set to null.
     */

    boolean doAction(Client c);

}

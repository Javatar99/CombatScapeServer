package RS2.model.skilling;

import RS2.tick.Tick;

public abstract class SkillingTask<T extends AbstractSkill> extends Tick {

    private T skillingValue;

    /**
     * Creates a new Skilling Task with the specified delay.
     *
     * @param delay The number of cycles between consecutive executions of this
     *              task.
     * @throws IllegalArgumentException if the {@code delay} is not positive.
     */
    public SkillingTask(int delay, T skillingValue) {
        super(delay);
        this.skillingValue = skillingValue;
    }

    @Override
    protected abstract void execute();
}

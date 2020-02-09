package frc.lib5k.statemachines;

/**
 * Mapping of statemachine states to biconsumers
 */
public interface IStateMapping {

    /**
     * Execute the state
     * 
     * @param timestamp Time passed since first run of the state
     * @param isNew     Is this the first run of the state?
     */
    public void execute(double dt, boolean isNew);
}
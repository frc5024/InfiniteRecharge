package frc.lib5k.ui.intpus;

import java.util.ArrayList;

import frc.lib5k.ui.intpus.actions.InputAction;

public class InputMapping {

    // List of actions
    private ArrayList<InputAction> m_actions = new ArrayList<>();

    /**
     * Add an action to be run
     * 
     * @param action Action
     */
    public void addAction(InputAction action) {
        m_actions.add(action);
    }

    /**
     * Update all actions
     */
    public void update() {

        // Update all actions
        for (InputAction a : m_actions) {
            a.update();
        }

    }
}
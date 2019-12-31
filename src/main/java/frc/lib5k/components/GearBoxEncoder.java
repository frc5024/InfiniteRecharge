package frc.lib5k.components;

import frc.lib5k.components.sensors.EncoderBase;

@Deprecated(since = "Kickoff 2020", forRemoval = true)
public class GearBoxEncoder extends EncoderBase {
    GearBox box;

    public GearBoxEncoder(GearBox box) {
        this.box = box;
    }

    @Override
    public int getRawTicks() {
        return box.getTicks();
    }

}
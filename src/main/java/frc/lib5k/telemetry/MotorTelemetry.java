package frc.lib5k.telemetry;

import java.util.function.DoubleSupplier;
import java.util.function.Function;

import edu.wpi.first.wpilibj.SpeedController;

public class MotorTelemetry {

    private SpeedController m_controller;
    private DoubleSupplier m_sensor;
    private Function<Double, Double> m_filter;

    public MotorTelemetry(SpeedController controller) {

        this.m_controller = controller;

    }

    public MotorTelemetry setSensor(DoubleSupplier reading) {

        this.m_sensor = reading;

        return this;
    }

    public MotorTelemetry setFilter(Function<Double, Double> filter) {

        this.m_filter = filter;

        return this;
    }

    private void update(){
        
    }
}

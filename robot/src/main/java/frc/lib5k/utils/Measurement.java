package frc.lib5k.utils;

public class Measurement{

    //Conversion Constants
    static final double MM = 1;
    static final double CM = 10;
    static final double M = 1000;
    static final double IN = 25.4;
    static final double FT = 304.8;
    static final double YD = 914.4;

    /**
    *Converts a measurement into specified units
    *
    *@param value the value of the measurement e.g. 10
    *@param units_from the unit type to convert from e.g. Measurement.MM
    *@param units_to the unit type to convert to e.g. Measurement.IN
    */
    public static double convert(double value, double units_from, double units_to){

        //Convert to mm, then to desired output
        return (value * units_from)/units_to;
    };
}
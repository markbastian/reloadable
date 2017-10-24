package converter;

public interface RelativeTemperature extends Temperature {
    double getRelativeOffset();

    Temperature toAbsolute();
}

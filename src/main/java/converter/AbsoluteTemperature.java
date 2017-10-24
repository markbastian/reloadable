package converter;

public interface AbsoluteTemperature extends Temperature {
    double getRelativeOffset();
    Temperature toRelative();
}

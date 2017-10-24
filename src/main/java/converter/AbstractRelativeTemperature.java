package converter;

public abstract class AbstractRelativeTemperature implements RelativeTemperature {
    private final double offset;

    public AbstractRelativeTemperature(double offset) {
        this.offset = offset;
    }

    @Override
    public final double getRelativeOffset() {
        return offset;
    }
}

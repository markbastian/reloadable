package converter;

public abstract class AbstractAbsoluteTemperature implements AbsoluteTemperature {
    private final double offset;

    public AbstractAbsoluteTemperature(double offset) {
        this.offset = offset;
    }

    @Override
    public final double getRelativeOffset() {
        return offset;
    }
}

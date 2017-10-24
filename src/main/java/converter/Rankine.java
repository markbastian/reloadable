package converter;

public final class Rankine extends AbstractAbsoluteTemperature {
    public static final double OFFSET = 459.67;
    private final double value;

    public Rankine(double value) {
        super(OFFSET);
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public Temperature toRelative() {
        return new Farenheit(getValue() - getRelativeOffset());
    }

    @Override
    public Temperature toFarenheit() {
        return toRelative();
    }

    @Override
    public Temperature toCelsius() {
        return toKelvin().toCelsius();
    }

    @Override
    public Temperature toKelvin() {
        return new Kelvin(value * 5.0 / 9.0);
    }

    @Override
    public Temperature toRankine() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rankine rankine = (Rankine) o;

        return Double.compare(rankine.value, value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }
}

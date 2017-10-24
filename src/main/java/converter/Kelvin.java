package converter;

public final class Kelvin extends AbstractAbsoluteTemperature {
    public static final double OFFSET = 273.15;
    private final double value;

    public Kelvin(double degrees) {
        super(OFFSET);
        this.value = degrees;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public Temperature toRelative() {
        return new Celsius(getValue() - getRelativeOffset());
    }

    @Override
    public Temperature toFarenheit() { return toRankine().toFarenheit(); }

    @Override
    public Temperature toCelsius() {
        return toRelative();
    }

    @Override
    public Temperature toKelvin() {
        return this;
    }

    @Override
    public Temperature toRankine() {
        return new Rankine(value * 9.0 / 5.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Kelvin kelvin = (Kelvin) o;

        return Double.compare(kelvin.value, value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }
}

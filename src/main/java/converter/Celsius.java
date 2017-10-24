package converter;

public final class Celsius extends AbstractRelativeTemperature {
    private final double value;

    public Celsius(double degrees) {
        super(273.15);
        this.value = degrees;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public Temperature toAbsolute() {
        return new Kelvin(getValue() + getRelativeOffset());
    }

    @Override
    public Temperature toFarenheit() {
        return toRankine().toFarenheit();
    }

    @Override
    public Temperature toCelsius() {
        return this;
    }

    @Override
    public Temperature toKelvin() {
        return toAbsolute();
    }

    @Override
    public Temperature toRankine() {
        return toKelvin().toRankine();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Celsius celsius = (Celsius) o;

        return Double.compare(celsius.value, value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }
}

package converter;

public final class Farenheit extends AbstractRelativeTemperature {
    private final double value;

    public Farenheit(double degrees) {
        super(Rankine.OFFSET);
        this.value = degrees;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public Temperature toAbsolute() {
        return new Rankine(getValue() + getRelativeOffset());
    }

    @Override
    public Temperature toFarenheit() {
        return this;
    }

    @Override
    public Temperature toCelsius() {
        return toKelvin().toCelsius();
    }

    @Override
    public Temperature toKelvin() {
        return toRankine().toKelvin();
    }

    @Override
    public Temperature toRankine() {
        return toAbsolute();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Farenheit farenheit = (Farenheit) o;

        return Double.compare(farenheit.value, value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }
}

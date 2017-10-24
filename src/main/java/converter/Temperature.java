package converter;

public interface Temperature {
    double getValue();
    Temperature toFarenheit();
    Temperature toCelsius();
    Temperature toKelvin();
    Temperature toRankine();
}

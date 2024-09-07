package part3;
/*
 * Author: 
 * Andrew ID#1: alannac
 * Andrew ID#2: peitongz
 */

/**
 * The DigitalThermometer class is an implementation of the Thermometer interface.
 * It allows setting and getting the current temperature in both Celsius and Fahrenheit.
 */
public class DigitalThermometer implements Thermometer {

    // Temperature stored in Celsius internally.
    private double temperatureInCelsius;

    /**
     * Constructor that initializes the thermometer with a temperature in Celsius.
     *
     * @param temperatureInCelsius initial temperature in Celsius.
     */
    public DigitalThermometer(double temperatureInCelsius) {
        this.temperatureInCelsius = temperatureInCelsius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTemperatureInCelsius() {
        return temperatureInCelsius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTemperatureInFahrenheit() {
        return celsiusToFahrenheit(temperatureInCelsius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemperatureInCelsius(double temperature) {
        this.temperatureInCelsius = temperature;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemperatureInFahrenheit(double temperature) {
        this.temperatureInCelsius = fahrenheitToCelsius(temperature);
    }

    /**
     * Converts Celsius to Fahrenheit.
     *
     * @param celsius temperature in Celsius.
     * @return temperature in Fahrenheit.
     */
    private double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }

    /**
     * Converts Fahrenheit to Celsius.
     *
     * @param fahrenheit temperature in Fahrenheit.
     * @return temperature in Celsius.
     */
    private double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }
}


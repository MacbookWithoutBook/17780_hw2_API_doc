package part3;
/*
 * Author: 
 * Andrew ID#1: alannac
 * Andrew ID#2: peitongz
 */

/**
 * The {@code Thermometer} interface represents a device capable of reporting the current temperature 
 * in two common temperature scales: Celsius and Fahrenheit.
 * 
 * <p>This interface is designed to allow the retrieval and updating of temperature values in both
 * Celsius and Fahrenheit. An instance of a class implementing this interface represents a thermometer
 * with a mutable temperature state that can be modified and retrieved as necessary.</p>
 *
 * <p>Thread-safety: This interface does not impose any requirements on thread-safety. It is expected
 * that implementations provide their own guarantees of thread-safety if needed. For example, a mutable
 * implementation should ensure atomic updates and retrievals of the temperature to avoid inconsistent states.</p>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create an instance of a class implementing Thermometer, initializing with a temperature in Celsius
 * Thermometer thermometer = new DigitalThermometer(20.0);
 * 
 * // Retrieve temperature in Celsius and Fahrenheit
 * double tempInCelsius = thermometer.getTemperatureInCelsius();
 * double tempInFahrenheit = thermometer.getTemperatureInFahrenheit();
 * 
 * // Update temperature in Fahrenheit
 * thermometer.setTemperatureInFahrenheit(98.6);
 * }</pre>
 * 
 * @implSpec Implementations must store and retrieve the temperature values accurately in both Celsius 
 * and Fahrenheit and should handle the necessary conversions between these units. The state transitions 
 * (temperature updates) affect both scales, meaning an update to the Celsius temperature will also 
 * affect the Fahrenheit temperature and vice versa.
 */
public interface Thermometer {

    /**
     * Retrieves the current temperature in Celsius.
     *
     * <p>The value returned represents the current temperature recorded by the thermometer, 
     * measured in degrees Celsius. This method performs no side effects and simply returns the 
     * current temperature value in Celsius.</p>
     *
     * <p>Calling this method after a call to {@link #setTemperatureInFahrenheit(double)} will reflect 
     * the updated temperature, automatically converted from Fahrenheit to Celsius.</p>
     * 
     * @return the current temperature in Celsius as a double.
     */
    double getTemperatureInCelsius();

    /**
     * Retrieves the current temperature in Fahrenheit.
     *
     * <p>The value returned represents the current temperature recorded by the thermometer, 
     * measured in degrees Fahrenheit. This method performs no side effects and simply returns the 
     * current temperature value in Fahrenheit.</p>
     *
     * <p>Calling this method after a call to {@link #setTemperatureInCelsius(double)} will reflect 
     * the updated temperature, automatically converted from Celsius to Fahrenheit.</p>
     * 
     * @return the current temperature in Fahrenheit as a double.
     */
    double getTemperatureInFahrenheit();

    /**
     * Updates the current temperature in Celsius.
     *
     * <p>This method updates the state of the thermometer by setting a new temperature in Celsius. 
     * The internal state of the thermometer will be updated to reflect this temperature, and the 
     * next call to {@link #getTemperatureInFahrenheit()} will return the equivalent temperature in 
     * Fahrenheit, converted from the newly set Celsius value.</p>
     * 
     * <p>After setting the temperature in Celsius, the updated value will affect both Celsius and Fahrenheit readings.</p>
     *
     * @param temperature the new temperature in degrees Celsius.
     * @throws IllegalArgumentException if the provided temperature is outside of realistic bounds 
     * for thermometer readings (e.g., below absolute zero).
     */
    void setTemperatureInCelsius(double temperature);

    /**
     * Updates the current temperature in Fahrenheit.
     *
     * <p>This method updates the state of the thermometer by setting a new temperature in Fahrenheit. 
     * The internal state of the thermometer will be updated to reflect this temperature, and the 
     * next call to {@link #getTemperatureInCelsius()} will return the equivalent temperature in 
     * Celsius, converted from the newly set Fahrenheit value.</p>
     * 
     * <p>After setting the temperature in Fahrenheit, the updated value will affect both Fahrenheit and Celsius readings.</p>
     *
     * @param temperature the new temperature in degrees Fahrenheit.
     * @throws IllegalArgumentException if the provided temperature is outside of realistic bounds 
     * for thermometer readings (e.g., below absolute zero).
     */
    void setTemperatureInFahrenheit(double temperature);
}

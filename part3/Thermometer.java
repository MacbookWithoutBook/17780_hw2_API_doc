package part3;
/*
 * Author: 
 * Andrew ID#1: alannac
 * Andrew ID#2: peitongz
 */

/**
 * The {@code Thermometer} interface represents a device capable of reporting and updating the current temperature 
 * in two common temperature scales: Celsius and Fahrenheit.
 * 
 * <p>This interface provides a contract for temperature retrieval and modification in both Celsius and Fahrenheit. 
 * It leaves the actual temperature conversion logic (between Celsius and Fahrenheit) to the implementing classes.
 * This allows for flexibility in how the conversions are done and ensures that various implementations may offer 
 * different approaches for handling temperatures, potentially with additional functionality.</p>
 *
 * <p>Thread-safety: This interface does not impose any thread-safety requirements. Implementations should handle 
 * any necessary synchronization if they are to be used in a concurrent environment. Mutable implementations must 
 * ensure consistent temperature updates and retrievals.</p>
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
 * @implSpec Implementations must handle the storage and retrieval of temperatures in both Celsius and Fahrenheit.
 * Conversions between these scales are the responsibility of the implementation, ensuring that any update in one scale
 * is reflected in the other.
 */
public interface Thermometer {

    /**
     * Retrieves the current temperature in Celsius.
     *
     * <p>This method returns the current temperature in degrees Celsius. The value reflects the 
     * latest temperature update, whether set in Celsius or Fahrenheit. The conversion between 
     * Fahrenheit and Celsius is handled by the implementation.</p>
     * 
     * <p>If the temperature was last set in Fahrenheit using {@link #setTemperatureInFahrenheit(double)},
     * this method will return the temperature converted to Celsius.</p>
     * 
     * @return the current temperature in degrees Celsius as a {@code double}.
     */
    double getTemperatureInCelsius();

    /**
     * Retrieves the current temperature in Fahrenheit.
     *
     * <p>This method returns the current temperature in degrees Fahrenheit. The value reflects the 
     * latest temperature update, whether set in Celsius or Fahrenheit. The conversion between 
     * Celsius and Fahrenheit is handled by the implementation.</p>
     * 
     * <p>If the temperature was last set in Celsius using {@link #setTemperatureInCelsius(double)},
     * this method will return the temperature converted to Fahrenheit.</p>
     * 
     * @return the current temperature in degrees Fahrenheit as a {@code double}.
     */
    double getTemperatureInFahrenheit();

    /**
     * Updates the current temperature in Celsius.
     *
     * <p>This method sets a new temperature in Celsius. The implementing class will update the internal state
     * of the thermometer and ensure that the next call to {@link #getTemperatureInFahrenheit()} returns the 
     * corresponding temperature in Fahrenheit, converted from the new Celsius value.</p>
     * 
     * <p>Temperature range: The valid temperature range should be above absolute zero (-273.15°C). Some implementations 
     * may impose additional constraints on the maximum allowable temperature.</p>
     *
     * @param temperature the new temperature in degrees Celsius.
     * @throws IllegalArgumentException if the provided temperature is below absolute zero or exceeds implementation-specific limits.
     */
    void setTemperatureInCelsius(double temperature);

    /**
     * Updates the current temperature in Fahrenheit.
     *
     * <p>This method sets a new temperature in Fahrenheit. The implementing class will update the internal state
     * of the thermometer and ensure that the next call to {@link #getTemperatureInCelsius()} returns the corresponding 
     * temperature in Celsius, converted from the new Fahrenheit value.</p>
     * 
     * <p>Temperature range: The valid temperature range should be above absolute zero (-459.67°F). Some implementations 
     * may impose additional constraints on the maximum allowable temperature.</p>
     * 
     * @param temperature the new temperature in degrees Fahrenheit.
     * @throws IllegalArgumentException if the provided temperature is below absolute zero or exceeds implementation-specific limits.
     */
    void setTemperatureInFahrenheit(double temperature);
}

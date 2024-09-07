package part3;
/*
 * Author: 
 * Andrew ID#1: alannac
 * Andrew ID#2: peitongz
 */

// This is just a test run for the Thermometer API and implementation.
public class Main {
    public static void main(String[] args) {
        Thermometer thermometer = new DigitalThermometer(25.0); // Initialize in Celsius
        System.out.println("Temperature in Celsius: " + thermometer.getTemperatureInCelsius());
        System.out.println("Temperature in Fahrenheit: " + thermometer.getTemperatureInFahrenheit());

        thermometer.setTemperatureInFahrenheit(98.6);
        System.out.println("Updated Temperature in Celsius: " + thermometer.getTemperatureInCelsius());
        System.out.println("Updated Temperature in Fahrenheit: " + thermometer.getTemperatureInFahrenheit());
    }
}


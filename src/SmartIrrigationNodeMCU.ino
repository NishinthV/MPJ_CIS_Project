#include <DHT.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

// Pin Definitions
#define DHTPIN D5          // DHT22 data pin (GPIO14)
#define DHTTYPE DHT22
#define RELAY_PIN D0       // Relay connected to GPIO16 (D0)
#define SOIL_PIN A0        // Soil moisture analog input (A0)

// Initialize DHT sensor
DHT dht(DHTPIN, DHTTYPE);

// Initialize LCD with I2C address, 16 columns, 2 rows
LiquidCrystal_I2C lcd(0x3F, 16, 2); // Change 0x27 if needed (e.g., 0x3F, 0x38)

// Moisture threshold for the pump (calibrate based on your soil sensor)
const int moistureThreshold = 500;  // Change this based on calibration
bool pumpState = false;

void setup() {
  Serial.begin(115200);
  dht.begin();

  pinMode(RELAY_PIN, OUTPUT);
  digitalWrite(RELAY_PIN, HIGH); // Relay is active LOW → HIGH = OFF

  lcd.begin(16,2);               // Initialize the LCD
  lcd.backlight();           // Turn on the backlight
  lcd.setCursor(0, 0);       // Move cursor to row 0, column 0
  lcd.print("Initializing...");

  delay(2000); // Sensor warm-up
}

void loop() {
  // Read DHT22 sensor data
  float humidity = dht.readHumidity();
  float temperature = dht.readTemperature();
  int soilMoistureRaw = analogRead(SOIL_PIN);
  float soilMoisturePercent = map(soilMoistureRaw, 1023, 0, 0, 100);

  // Check if any DHT22 readings failed
  if (isnan(humidity) || isnan(temperature)) {
    Serial.println("Failed to read from DHT22 sensor!");
    return; // Skip rest of loop
  }

  // Control logic for relay (pump)
  if (soilMoistureRaw > moistureThreshold) {
    digitalWrite(RELAY_PIN, LOW);  // Turn pump ON
    pumpState = true;
  } else {
    digitalWrite(RELAY_PIN, HIGH); // Turn pump OFF
    pumpState = false;
  }

  // Send data over Serial for GUI (debugging)
  Serial.print("T:");
  Serial.print(temperature, 1); // 1 decimal place
  Serial.print(",H:");
  Serial.print(humidity, 1);
  Serial.print(",M:");
  Serial.print(soilMoisturePercent, 1);
  Serial.print(",P:");
  Serial.println(pumpState ? 1 : 0);

  // Update LCD
  lcd.clear();                  // Clear previous LCD content
  lcd.setCursor(0, 0);          // Set cursor to top row
  lcd.print("T:");
  lcd.print(temperature, 1);    // Print temperature
  lcd.print("C");

  lcd.setCursor(0, 1);          // Move to second row
  lcd.print("H:");
  lcd.print(humidity, 1);       // Print humidity
  lcd.print("%");

  // Optionally, display soil moisture & pump status
  lcd.setCursor(8, 0);          // Move cursor to the middle
  lcd.print("M:");
  lcd.print(soilMoisturePercent, 1);  // Soil moisture percentage
  
  lcd.setCursor(8, 1);          // Move to second row again
  lcd.print("P:");
  lcd.print(pumpState ? "ON" : "OFF");  // Display pump status

  delay(2000); // Delay to update every 2 seconds
}

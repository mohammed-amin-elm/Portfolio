#include <FastLED.h>

#define LED_PIN       5   // Pin connected to the NeoPixels
#define LED_COUNT     82  // Number of NeoPixels

const int sampleWindow = 50; // Sample window width in mS (50 mS = 20Hz)
unsigned int sample;

CRGB leds[LED_COUNT];

void setup() {
  Serial.begin(9600);
  FastLED.addLeds<WS2812B, LED_PIN, GRB>(leds, LED_COUNT);
  FastLED.setBrightness(255);  // Set initial brightness
}

void loop() {
  unsigned long startMillis = millis();  // Start of sample window
  unsigned int peakToPeak = 0;   // peak-to-peak level
  unsigned int signalMax = 0;
  unsigned int signalMin = 1024;

  // collect data for 50 mS
  while (millis() - startMillis < sampleWindow) {
    sample = analogRead(A0);
    if (sample < 1024)  // toss out spurious readings
    {
      if (sample > signalMax) {
        signalMax = sample;  // save just the max levels
      } else if (sample < signalMin) {
        signalMin = sample;  // save just the min levels
      }
    }
  }

  peakToPeak = signalMax - signalMin;  // max - min = peak-peak amplitude
  double volts = (peakToPeak * 5.0) / 1024;  // convert to volts
  double decibel = abs(25.5755 * log(8.726 * volts));

  Serial.println(decibel);


  // Map the decibel value to LED brightness levels
  int brightness = map(decibel, 0, 80.20, 0, 255);

  // Set the colors for each section of the LED strip
  for (int i = 0; i < LED_COUNT; i++) {
    if (i < LED_COUNT / 8) {
      leds[i] = CRGB::Green; // Lower part (Green)
    } else if (i < LED_COUNT * 2 / 8) {
      leds[i] = CRGB::Green; // Middle part (Green)
    } else if (i < LED_COUNT * 3 / 8) {
      leds[i] = CRGB::Green; // Middle part (Green)
    } else if (i < LED_COUNT * 4 / 8) {
      leds[i] = CRGB::Yellow; // Middle part (Yellow)
    } else if (i < LED_COUNT * 5 / 8) {
      leds[i] = CRGB::Yellow; // Middle part (Yellow)
    } else if (i < LED_COUNT * 6 / 8) {
      leds[i] = CRGB::Orange; // Middle part (orange)
    } else if (i < LED_COUNT * 7 / 8) {
      leds[i] = CRGB::Red; // Middle part (Orange
    } else {
      leds[i] = CRGB::Red; // Top part (Red)
    }
  }

  // Light up LEDs based on decibel level
  int numLitLeds = map(decibel, 0, 80.20, 0, LED_COUNT);
  for (int i = 0; i < LED_COUNT; i++) {
    if (i < numLitLeds) {
      leds[i].fadeToBlackBy(255 - brightness);
    } else {
      leds[i].fadeToBlackBy(255);
    }
  }

  FastLED.show();
}
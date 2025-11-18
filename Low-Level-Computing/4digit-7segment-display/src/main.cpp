#include <avr/io.h>
#include <util/atomic.h>
#include <avr/delay.h>

#define BLANK 10

/*
  PB1 -> MOSI
  PB2 -> Serial Clock
  PB3 -> Latch
  PB4 -> Chip Select
*/

void init(); // Similar to the setup() function

uint8_t digits[11]{
    // LSBFIRST
    0b00111111, // digit 0
    0b00000110, // digit 1
    0b01011011, // digit 2
    0b01001111, // digit 3
    0b01100110, // digit 4
    0b01101101, // digit 5
    0b01111101, // digit 6
    0b00000111, // digit 7
    0b01111111, // digit 8
    0b01101111, // digit 9
    0b00000000  // BLANK
};

void writeDigit(int num1, int num2, int num3, int num4); // Use to write a digit to the seven segment display
uint8_t addDot(uint8_t digitPattern);

uint8_t transfer(uint8_t data); // Transfer and receive data via SPI

void setCalibrationData();
uint16_t T1;
uint16_t T2;
uint16_t T3;

void initializeSensor();

int main()
{
  init();

  setCalibrationData();
  initializeSensor();

  while (true) // Similar to the loop() function
  {

    PORTB &= ~(1 << PB4);
    transfer(0xFA);
    long adc_T = ((long)transfer(0) << 12) | ((long)transfer(0) << 4) | ((long)transfer(0) >> 4);
    PORTB |= (1 << PB4);

    long var1 = ((((adc_T >> 3) - ((long)T1 << 1))) * ((long)T2)) >> 11;
    long var2 = (((((adc_T >> 4) - ((long)T1)) * ((adc_T >> 4) - ((long)T1))) >> 12) * ((long)T3)) >> 14;
    float temp = (var1 + var2) / 5120.0;

    int firstDigit = (int)(temp / 10);
    int secondDigit = (int)(temp) % 10;
    int thirdDigit = (int)(temp * 10) % 10;
    int fourthDigit = (int)(temp * 100) % 10;

    writeDigit(firstDigit, secondDigit, thirdDigit, fourthDigit);
  }
}

void init()
{
  PORTB = 0; // Clear PORT B

  DDRB |= (1 << PB2) | (1 << PB1) | (1 << PB3) | (1 << PB4); // Set PB1, PB2 and PB3 to OUTPUT

  USICR |= (1 << USIWM0) | (1 << USICS1) | (1 << USICLK); // Set USIWM0 for Three-wire mode
                                                          // Set USICS1 for positive edge
}

uint8_t transfer(uint8_t data)
{
  USISR |= (1 << USIOIF);                                                         // Clearing Counter Overflow Flag
  USISR &= ~(1 << USICNT0) | ~(1 << USICNT1) | ~(1 << USICNT2) | ~(1 << USICNT3); // Initialize 4-bit counter to 0;

  USIDR = data; // Setting 8-bit transmit data in Data Register

  ATOMIC_BLOCK(ATOMIC_RESTORESTATE) // Disable interrupt
  {
    while (!(USISR & (1 << USIOIF)))
    {
      USICR |= (1 << USITC);
    }
  }

  return USIDR; // Return Data Register
}

void writeDigit(int num1, int num2, int num3, int num4)
{
  PORTB &= ~(1 << PB3);
  transfer(0b00011100);
  transfer(digits[num1]);
  PORTB |= (1 << PB3);

  PORTB &= ~(1 << PB3);
  transfer(0b00011010);
  transfer(addDot(digits[num2]));
  PORTB |= (1 << PB3);

  PORTB &= ~(1 << PB3);
  transfer(0b00010110);
  transfer(digits[num3]);
  PORTB |= (1 << PB3);

  PORTB &= ~(1 << PB3);
  transfer(0b00001110);
  transfer(digits[num4]);
  PORTB |= (1 << PB3);
}

uint8_t addDot(uint8_t digitPattern)
{
  return 0b10000000 | digitPattern;
}

void setCalibrationData()
{
  PORTB &= ~(1 << PB4);
  transfer(0x88);
  uint8_t Lb = transfer(0);
  uint16_t Hb = transfer(0);
  T1 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  T2 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  T3 = (Hb << 8) | Lb;
  PORTB |= (1 << PB4);
}

void initializeSensor()
{
  PORTB &= ~(1 << PB4);
  transfer(0x74);
  transfer(0b01000011);
  PORTB |= (1 << PB4);
}
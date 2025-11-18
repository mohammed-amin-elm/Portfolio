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
    0b01111110, // digit 0
    0b00001100, // digit 1
    0b10110110, // digit 2
    0b10011110, // digit 3
    0b11001100, // digit 4
    0b11011010, // digit 5
    0b11111010, // digit 6
    0b00001110, // digit 7
    0b11111110, // digit 8
    0b11011110,
    0b00000000 // digit 9
};

void writeDigit(int num1, int num2); // Use to write a digit to the seven segment display
uint8_t transfer(uint8_t data);      // Transfer and receive data via SPI

void initializeSensor();

int main()
{
  init();
  initializeSensor();

  while (true) // Similar to the loop() function
  {
    PORTB &= ~(1 << PB4);
    transfer(0xD0);
    uint8_t id = transfer(0);
    PORTB |= (1 << PB4);

    if (id == 0x58)
    {
      writeDigit(1, 1);
    }
    else
    {
      writeDigit(0, 0);
    }
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

void writeDigit(int num1, int num2)
{
  PORTB &= ~(1 << PB3);
  transfer(digits[num2]);
  transfer(digits[num1]);
  PORTB |= (1 << PB3);
}

void initializeSensor()
{
  PORTB &= ~(1 << PB4);
  transfer(0x74);
  transfer(0b01000011);
  PORTB |= (1 << PB4);
}
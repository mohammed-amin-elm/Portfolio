#include <avr/io.h>
#include <util/atomic.h>
#include <avr/interrupt.h>

#define BLANK 10
#define CPU_FREQ 1000000 // Clock frequency

// Memory array that stores matrix-pattern
#define MEM_SIZE 8
uint8_t memory[MEM_SIZE] = {0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF};
int memPointer = 0;
double temp;

uint8_t tempToBitpattern(double temp, double max, double min); // Converts temperature data to matrix bit-pattern
void shiftMemoryLeft(); // Shifts matrix-rows to the left

void init(); // Initialize PORTB and USI Control register

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

// Write four digits to display + writes bit patterns to dot matrix
void writeDigit(int num1, int num2, int num3, int num4, bool dot); // Use to write a digit to the seven segment display
uint8_t addDot(uint8_t digitPattern); // Adds a dot to digit pattern


uint8_t transfer(uint8_t data); // Transfer and receive data via SPI

void initializeSensor();
double calibrateTemp(long adc_P); // Returns calibrated temperature
long adc_T = 0;
long t_fine;

double calibratePress(long adc_P); // Returns calibrated pressure
long adc_P = 0;

// Initiliazes variables below with calibration data
void setCalibrationData(); 
uint16_t T1;
uint16_t T2;
uint16_t T3;

uint16_t P1;
int16_t P2;
int16_t P3;
int16_t P4;
int16_t P5;
int16_t P6;
int16_t P7;
int16_t P8;
int16_t P9;

enum State{TEMP, PRESS};

enum State sensorState = TEMP;

// Create a timer intterupt using timer1
void timerInterrupt1(float freq);
int timerCounter = 0;

int main()
{
  init();

  setCalibrationData();
  initializeSensor();

  timerInterrupt1(0.5); // Set timer-interrupt every 500ms

  while (true) // Similar to the loop() function
  {
    temp = calibrateTemp(adc_T);

    if(sensorState == TEMP) {
      double data = calibrateTemp(adc_T); // Temperature data Celcius
      int firstDigit = (int)(data / 10);
      int secondDigit = (int)(data) % 10;
      int thirdDigit = (int)(data * 10) % 10;
      int fourthDigit = (int)(data * 100) % 10;

      writeDigit(firstDigit, secondDigit, thirdDigit, fourthDigit, true);
    } else {
      double data = calibratePress(adc_P) * 0.01; // Pressure data Pascal

      if(data < 1000.0) {
        int firstDigit = (int)(data / 100);
        int secondDigit = (int)(data / 10) % 10;
        int thirdDigit = (int)(data) % 10;
        int fourthDigit = (int)(data * 10) % 10;
        writeDigit(firstDigit, secondDigit, thirdDigit, fourthDigit, false);
      } else {
        int firstDigit = (int)(data / 1000);
        int secondDigit = (int)(data / 100) % 10;
        int thirdDigit = (int)(data / 10) % 10;
        int fourthDigit = (int)(data) % 10;
        writeDigit(firstDigit, secondDigit, thirdDigit, fourthDigit, false);
      }
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

void writeDigit(int num1, int num2, int num3, int num4, bool dot)
{
  PORTB &= ~(1 << PB3);
  transfer(0b10000000);  // Columns / active HIGH
  transfer(memory[0]);   // Rows    / active LOW
  transfer(0b00011100);  // xxxx1110x
  transfer(digits[num1]);
  PORTB |= (1 << PB3);

  PORTB &= ~(1 << PB3);
  transfer(0b01000000);
  transfer(memory[1]);
  transfer(0b00011010); // xxxx1101x
  if(dot) {
    transfer(addDot(digits[num2]));
  } else {
    transfer(digits[num2]);
  }
  PORTB |= (1 << PB3);

  PORTB &= ~(1 << PB3);
  transfer(0b00100000);
  transfer(memory[2]);
  transfer(0b00010110); // xxxx1011x
  transfer(digits[num3]);
  PORTB |= (1 << PB3);

  PORTB &= ~(1 << PB3);
  transfer(0b00010000);
  transfer(memory[3]);
  transfer(0b00001110); // xxxx0111x
  transfer(digits[num4]);
  PORTB |= (1 << PB3);

  // ROUND 2

  PORTB &= ~(1 << PB3);
  transfer(0b00001000);
  transfer(memory[4]);
  transfer(0b00011100);  // xxxx1110x
  transfer(digits[num1]);
  PORTB |= (1 << PB3);

  PORTB &= ~(1 << PB3);
  transfer(0b00000100);
  transfer(memory[5]);
  transfer(0b00011010); // xxxx1101x
  if(dot) {
    transfer(addDot(digits[num2]));
  } else {
    transfer(digits[num2]);
  }
  PORTB |= (1 << PB3);

  PORTB &= ~(1 << PB3);
  transfer(0b00000010);
  transfer(memory[6]);
  transfer(0b00010110); // xxxx1011x
  transfer(digits[num3]);
  PORTB |= (1 << PB3);

  PORTB &= ~(1 << PB3);
  transfer(0b00000001);
  transfer(memory[7]);
  transfer(0b00001110); // xxxx0111x
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

  Lb = transfer(0);
  Hb = transfer(0);
  P1 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  P2 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  P3 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  P4 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  P5 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  P6 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  P7 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  P8 = (Hb << 8) | Lb;

  Lb = transfer(0);
  Hb = transfer(0);
  P9 = (Hb << 8) | Lb;

  PORTB |= (1 << PB4);
}

void initializeSensor()
{
  PORTB &= ~(1 << PB4);
  transfer(0x74);
  transfer(0b01001011);
  PORTB |= (1 << PB4);
}

void timerInterrupt1(float freq)
{
  cli();
  TCCR1 = (1 << CTC1) | (1 << CS10) | (1 << CS11) | (1 << CS12) | (1 << CS13);
  OCR1A = CPU_FREQ / 16384.0 / (1/freq) - 1;
  OCR1C = OCR1A;
  TIMSK |= (1 << OCIE1A);
  sei();
}

ISR(TIM1_COMPA_vect)
{
  shiftMemoryLeft();
  memory[MEM_SIZE-1] = tempToBitpattern(temp, 25, 15);

  if(timerCounter >= 3) {
    if(sensorState == TEMP) {
      PORTB &= ~(1 << PB4);
      transfer(0xFA);
      adc_T = ((long)transfer(0) << 12) | ((long)transfer(0) << 4) | ((long)transfer(0) >> 4);
      PORTB |= (1 << PB4);
      sensorState = PRESS;
    } else {
      PORTB &= ~(1 << PB4);
      transfer(0xF7);
      adc_P = ((long)transfer(0) << 12) | ((long)transfer(0) << 4) | ((long)transfer(0) >> 4);
      PORTB |= (1 << PB4);
      sensorState = TEMP;
    }
    timerCounter = 0;
  } else {
    timerCounter++;
  }
}

double calibrateTemp(long adc_T) {
  double var1, var2, T;
  var1 = (((double)adc_T)/16384.0 - ((double)T1)/1024.0) * ((double)T2);
  var2 = ((((double)adc_T)/131072.0 - ((double)T1)/8192.0) *
  (((double)adc_T)/131072.0 - ((double) T1)/8192.0)) * ((double)T3);
  t_fine = (long)(var1 + var2);
  T = (var1 + var2) / 5120.0;
  return T;
}

double calibratePress(long adc_P) {
  double var1, var2, p;
  var1 = ((double)t_fine/2.0) - 64000.0;
  var2 = var1 * var1 * ((double)P6) / 32768.0;
  var2 = var2 + var1 * ((double)P5) * 2.0;
  var2 = (var2/4.0)+(((double)P4) * 65536.0);
  var1 = (((double)P3) * var1 * var1 / 524288.0 + ((double)P2) * var1) / 524288.0;
  var1 = (1.0 + var1 / 32768.0)*((double)P1);
  if (var1 == 0.0) {
    return 0; // avoid exception caused by division by zero
  }
  p = 1048576.0 - (double)adc_P;
  p = (p - (var2 / 4096.0)) * 6250.0 / var1;
  var1 = ((double)P9) * p * p / 2147483648.0;
  var2 = p * ((double)P8) / 32768.0;
  p = p + (var1 + var2 + ((double)P7)) / 16.0;
  return p;
}

uint8_t tempToBitpattern(double temp, double max, double min) { // 15 - 35
  double delta = (max - min)/7; // Delta value required to add a dot to matrix
  
  if(temp >= max) {
    return 0b00000000;
  } else if(temp >= max - delta) {
    return 0b10000000;
  } else if(temp >= max - delta * 2) {
    return 0b11000000;
  } else if(temp >= max - delta * 3) {
    return 0b11100000;
  } else if(temp >= max - delta * 4) {
    return 0b11110000;
  } else if(temp >= max - delta * 5) {
    return 0b11111000;
  } else if(temp >= max - delta * 6) {
    return 0b11111100;
  } else if(temp >= max - delta * 7) {
    return 0b11111110;
  } else {
    return 0b11111111;
  }
}

void shiftMemoryLeft() {
  for(int i=0; i<MEM_SIZE-1; i++) {
    memory[i] = memory[i+1];
  }
}
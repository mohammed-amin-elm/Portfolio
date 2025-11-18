#include <util/atomic.h>
#include <avr/io.h>
#include <util/delay.h>

float temp;

uint8_t digits[11]{
    // LSBFIRST
    0b01111110, // 0
    0b00001100, // 1
    0b10110110, // 2
    0b10011110, // 3
    0b11001100, // 4
    0b11011010, // 5
    0b11111010, // 6
    0b00001110, // 7
    0b11111110, // 8
    0b11011110, // 9
    0b00000000 //  blank
};

uint16_t T1;
uint16_t T2;
uint16_t T3;

void init(){
    PORTB = 0;

    //set pins as output
    DDRB |= (1 << PB0) | (1 << PB1) | (1 << PB2) | (1 << PB3) | (1 << PB4);

    //set USI to 3 wire mode for SPI
    USICR |= (1 << USIWM0) | (1 << USICS1) | (1 << USICLK);
}

uint8_t transfer(uint8_t data)
{
    USISR |= (1 << USIOIF);                                                         // Clear Counter Overflow Flag
    USISR &= ~(1 << USICNT0) | ~(1 << USICNT1) | ~(1 << USICNT2) | ~(1 << USICNT3); // Initialize 4-bit counter to 0;

    USIDR = data; // set data in dataRegister

    ATOMIC_BLOCK(ATOMIC_RESTORESTATE) // Disable interrupt
    {
        while (!(USISR & (1 << USIOIF)))
        {
            USICR |= (1 << USITC);
        }
    }

    return USIDR; // Return Data Register
}

void writeDigit(int num){
    PORTB &= ~(1 << PB3);
    transfer(digits[num]);
    PORTB |= (1 << PB3);
}

void calibrateSensor(){
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
    //select mode
    PORTB &= ~(1 << PB4);
    transfer(0x74);
    transfer(0b01000011);
    PORTB |= (1 << PB4);
}


int main() {
    init();
    calibrateSensor();
    initializeSensor();
    while (true) {
        //read temp
        PORTB &= ~(1 << PB4);
        transfer(0xFA);
        long adc_T = ((long)transfer(0) << 12) | ((long)transfer(0) << 4) | ((long)transfer(0) >> 4);
        PORTB |= (1 << PB4);

        //calc temp
        long var1 = ((((adc_T >> 3) - ((long)T1 << 1))) * ((long)T2)) >> 11;
        long var2 = (((((adc_T >> 4) - ((long)T1)) * ((adc_T >> 4) - ((long)T1))) >> 12) * ((long)T3)) >> 14;
        temp = (var1 + var2) / 5120.0;

        //separate
        int firstDigit = (int)(temp / 10);
        int secondDigit = (int)(temp) % 10;
        int thirdDigit = (int)(temp * 10) % 10;
        int fourthDigit = (int)(temp * 100) % 10;

        writeDigit(firstDigit);
        _delay_ms(100);

        writeDigit(secondDigit);
        _delay_ms(100);

        writeDigit(thirdDigit);
        _delay_ms(100);

        writeDigit(fourthDigit);
        _delay_ms(900);
    }
    return 0;
}
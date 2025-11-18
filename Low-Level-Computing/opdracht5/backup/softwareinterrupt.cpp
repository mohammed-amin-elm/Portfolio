#include <avr/io.h>
#include <avr/interrupt.h>

void softwareInterrupt(float freq);

int main()
{
  DDRB = (1 << DDB3) | (1 << DDB4);
  PORTB = (1 << PB3);

  softwareInterrupt(20);

  while (true)
  {
  }
}

void softwareInterrupt(float freq)
{
  cli();

  TCCR1 = (1 << CTC1) | (1 << CS13) | (1 << CS12) | (1 << CS11) | (1 << CS10);

  OCR1A = F_CPU / 16384.0 / freq - 1.0;
  OCR1C = OCR1A;

  TIMSK = (1 << OCIE1A);

  sei();
}

ISR(TIM1_COMPA_vect)
{
  PINB = (1 << PINB3) | (1 << PINB4);
}
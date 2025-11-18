#include <Arduino.h>

void displayDigit(int digit, int index);

int digits[4][7] = {
    {1, 1, 1, 1, 1, 1, 0},
    {0, 1, 1, 0, 0, 0, 0},
    {1, 1, 0, 1, 1, 0, 1},
    {1, 1, 1, 1, 0, 0, 1}};

void setup()
{
  for (int i = 2; i <= 12; i++)
  {
    pinMode(i, OUTPUT);
  }

  for (int i = 2; i <= 5; i++)
  {
    digitalWrite(i, HIGH);
  }
}

void loop()
{
  displayDigit(0, 0);
  delay(500);
  digitalWrite(2, HIGH);

  displayDigit(1, 1);
  delay(500);
  digitalWrite(3, HIGH);

  displayDigit(2, 2);
  delay(500);
  digitalWrite(4, HIGH);

  displayDigit(3, 3);
  delay(500);
  digitalWrite(5, HIGH);
}

void displayDigit(int digit, int index)
{
  for (int i = 0; i < 7; i++)
  {
    digitalWrite(i + 6, (uint8_t)digits[digit][i]);
  }
  digitalWrite(index + 2, LOW);
}
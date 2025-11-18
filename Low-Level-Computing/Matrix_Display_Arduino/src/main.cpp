#include <Arduino.h>

#define DS 8    // Data
#define SHCP 9  // Clk
#define SCTP 10 // Latch
#define DELAY 2

byte nums[8] = {
                0b00000001,
                0b00000011,
                0b00000111,
                0b00001111,
                0b00011111,
                0b00111111,
                0b01111111,
                0b11111111};

byte data[8];

void setup() {
  pinMode(DS, OUTPUT);
  pinMode(SHCP, OUTPUT);
  pinMode(SCTP, OUTPUT);
}

unsigned long previous = millis();

void loop() {
  digitalWrite(SCTP, LOW); 
  shiftOut(DS, SHCP, MSBFIRST, 0b10000000);
  shiftOut(DS, SHCP, MSBFIRST, data[0]);
  digitalWrite(SCTP, HIGH);
  
  digitalWrite(SCTP, LOW); 
  shiftOut(DS, SHCP, MSBFIRST, 0b01000000);
  shiftOut(DS, SHCP, MSBFIRST, data[1]);
  digitalWrite(SCTP, HIGH);
  
  digitalWrite(SCTP, LOW);
  shiftOut(DS, SHCP, MSBFIRST, 0b00100000);
  shiftOut(DS, SHCP, MSBFIRST, data[2]);
  digitalWrite(SCTP, HIGH);

  digitalWrite(SCTP, LOW);
  shiftOut(DS, SHCP, MSBFIRST, 0b00010000);
  shiftOut(DS, SHCP, MSBFIRST, data[3]);
  digitalWrite(SCTP, HIGH);
  
  digitalWrite(SCTP, LOW); 
  shiftOut(DS, SHCP, MSBFIRST, 0b00001000);
  shiftOut(DS, SHCP, MSBFIRST, data[4]);
  digitalWrite(SCTP, HIGH);
  
  digitalWrite(SCTP, LOW); 
  shiftOut(DS, SHCP, MSBFIRST, 0b00000100);
  shiftOut(DS, SHCP, MSBFIRST, data[5]);
  digitalWrite(SCTP, HIGH);
  
  digitalWrite(SCTP, LOW); 
  shiftOut(DS, SHCP, MSBFIRST, 0b00000010);
  shiftOut(DS, SHCP, MSBFIRST, data[6]);
  digitalWrite(SCTP, HIGH);
  
  digitalWrite(SCTP, LOW); 
  shiftOut(DS, SHCP, MSBFIRST, 0b00000001);
  shiftOut(DS, SHCP, MSBFIRST, data[7]);
  digitalWrite(SCTP, HIGH);
   

  if(millis() - previous > 1000) {
    for(int i=0; i<8; i++) {
      data[i] = nums[random(8)];
    }
    previous = millis();
  }
}
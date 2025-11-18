#define DS   4
#define STCP 3
#define SHCP 2

#define BTN1 8
#define BTN2 10

#define LED1 9
#define LED2 11

#define GREEN_LED 5
#define RED_LED 6

#define IN1 12

void displayDigit(int digit);

int digits[10] = {
  B01111110, // digit 0
  B00110000, // digit 1
  B01101101, // digit 2
  B01111001, // digit 3
  B00110011, // digit 4
  B01011011, // digit 5
  B01011111, // digit 6
  B01110000, // digit 7
  B01111111, // digit 8
  B01111011  // digit 9
};

void setup() {
  Serial.begin(9600);
  
  pinMode(DS, OUTPUT);
  pinMode(STCP, OUTPUT);
  pinMode(SHCP, OUTPUT);

  pinMode(BTN1, INPUT);
  pinMode(BTN2, INPUT);

  pinMode(LED1, OUTPUT);
  pinMode(LED2, OUTPUT);

  pinMode(GREEN_LED, OUTPUT);
  pinMode(RED_LED, OUTPUT);

  pinMode(IN1, INPUT);

  digitalWrite(GREEN_LED, HIGH);
  digitalWrite(RED_LED, HIGH);

  displayDigit(0);
}

unsigned long previous = millis();
int count = 0;

void loop() {
  Serial.println(digitalRead(IN1));

  if(digitalRead(BTN1) == 0) {
      digitalWrite(LED1, HIGH);  
      
      count++;
      if(count > 9) count = 0;

      displayDigit(count);
      delay(250);
    } else {
      digitalWrite(LED1, LOW);  
    }

    if(digitalRead(BTN2) == 0) {
      digitalWrite(LED2, HIGH);  

      count--;
      if(count < 0) count = 9;

      displayDigit(count);
      delay(250);
    } else {
      digitalWrite(LED2, LOW);  
    }
}

void displayDigit(int digit) {
  digitalWrite(STCP,LOW);
  shiftOut(DS, SHCP, LSBFIRST, digits[digit]);
  digitalWrite(STCP, HIGH); 
}

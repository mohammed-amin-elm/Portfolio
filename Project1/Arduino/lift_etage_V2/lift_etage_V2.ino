#include <arduinio.h>
#include <Wire.h>

//i2c etage = etage nummer + 8, verander voor elke etage
#define etage 10

//arduino pins===============
//shift register
#define clock 2
#define store 3
#define input 4

//sensor
#define sensor 12

//lift knoppen
#define knopBoven 8
#define knopOnder 10
#define ledKnopBoven 9
#define ledKnopOnder 11

//deur leds
#define deurDicht 5
#define deurOpen 6
//===========================

//functions
void displayDigit();
void sensorDetect();
void knopIn();
void deur();
int manualDisplayInput();
void sendData();
void recieveData();

//values
int liftLocatie; //recieve
int liftGestopt;

int sensorInput;  //send
int up = 0;
int down = 0;


//7-segment display table
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
  B01111011, // digit 9
};

void setup() {
  Wire.begin(etage);
  Wire.onRequest(sendData);
  Wire.onReceive(recieveData);
  Serial.begin(9600);
  pinMode(input, OUTPUT);
  pinMode(store, OUTPUT);
  pinMode(clock, OUTPUT);
  pinMode(sensor, INPUT);
  pinMode(knopBoven, INPUT);
  pinMode(knopOnder, INPUT);
  pinMode(ledKnopBoven, OUTPUT);
  pinMode(ledKnopOnder, OUTPUT);
  pinMode(deurDicht, OUTPUT);
  pinMode(deurOpen, OUTPUT);
}


void loop() {
  // functies
  sensorDetect();
  displayDigit(); // manualDisplayInput() or digit
  knopIn();
  deur();

  //debug spul
  Serial.print(liftLocatie);
  Serial.print(liftGestopt);
  Serial.print(" ");
  Serial.print(sensorInput);
  Serial.print(up);
  Serial.print(down);
  Serial.print("\n");

  //timer
  delay(250);
}

void sensorDetect(){
  if (digitalRead(sensor)==LOW){
    sensorInput = 1;
  }
  else{
    sensorInput = 0;
  }
}

void knopIn(){
  if(digitalRead(knopBoven)==LOW){
    up = 1;
  }
  if(digitalRead(knopOnder)==LOW){
    down = 1;
  }
  if(sensorInput == 1){
    up = 0;
    down = 0;
  }
  (up == 1) ? digitalWrite(ledKnopBoven, HIGH) : digitalWrite(ledKnopBoven, LOW);
  (down == 1) ? digitalWrite(ledKnopOnder, HIGH) : digitalWrite(ledKnopOnder, LOW);
}

int manualDisplayInput(){ //alleen voor testen
  int n;
  cout << "\ngetal: ";
  cin >> n;
  return n;
}

void displayDigit() {
  digitalWrite(store,LOW);
  shiftOut(input, clock, LSBFIRST, digits[liftLocatie]);
  digitalWrite(store, HIGH);
}

void deur(){
  if(sensorInput == 1 && liftGestopt == 1){ // lift staat stil op de etage dus deuren moeten open
    digitalWrite(deurOpen, HIGH);//groen
    digitalWrite(deurDicht, LOW);//rood
  }else if(sensorInput == 1 && liftGestopt == 0){ //lift is op etage maar beweegt nog dus deuren dicht
    digitalWrite(deurOpen, LOW);//groen
    digitalWrite(deurDicht, HIGH);//rood
  }else if(sensorInput == 0 && liftGestopt == 0){ //lift niet op etage dus deuren zijn niet zichtbaar
    digitalWrite(deurOpen, LOW);//groen
    digitalWrite(deurDicht, LOW);//rood
  }else if(sensorInput == 0 && liftGestopt == 1){ //kan niet maar is wel nodig
    digitalWrite(deurOpen, LOW);//groen
    digitalWrite(deurDicht, HIGH);//rood
  }
}

void sendData(){
  Wire.write(sensorInput);
  Wire.write(up);
  Wire.write(down);
}

void recieveData(){
  while(Wire.available()) {
    liftLocatie = Wire.read();
    liftGestopt = Wire.read();
  }
}

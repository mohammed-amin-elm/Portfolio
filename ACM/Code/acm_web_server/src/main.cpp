#include <Arduino.h>
#include <WiFi.h>
#include <WebServer.h>
#include <WebSocketsServer.h>
#include <ArduinoJson.h>

// SSID and Password for WiFi Network
String ssid = "tesla iot";
String pass = "fsL6HgjN";

// Initializing HTTP and WebSocket Servers
WebServer server(80);
WebSocketsServer webSocket = WebSocketsServer(8080);

// Creating JSON Documents
StaticJsonDocument<200> doc_tx;
StaticJsonDocument<200> doc_rx;

// Declaring WiFi and Server init functions
void initWiFi(String ssid, String password);
void initServer();
void webSocketEvent(byte client_num, WStype_t type, uint8_t* payload, size_t length);

// Converts char array to string
String charArrayToString(uint8_t* array, size_t size);

// index.html source code for the website
String index_html = "<!DOCTYPE html><html lang='en'><head> <meta charset='UTF-8'> <meta name='viewport' content='width=device-width, initial-scale=1.0'> <title>ACM | Control Enviroment</title> <style> :root { --header-top: -10px; } body { font-family: Arial; margin: 0; overflow: hidden; } header { height: 100px; border-bottom: solid 2px #2B2D42; } #acm-container { background-color: #2B2D42; color: #fff; display: inline-block; padding: 50px; margin: 0; height: 0px; width: 10%; text-align: center; } #acm-container span { position: relative; top: var(--header-top); font-size: 21px; font-weight: bold; } #ci-container { display: inline-block; text-align: center; width: 70%; } #ci-container span{ position: relative; top: var(--header-top); font-size: 21px; font-weight: bold; } .blocks { width: 25%; height: 450px; border: solid 1px black; border-radius: 10px; display: inline-block; margin-right: 15px; margin-left: 15px; position: relative; top: 3vh; overflow: hidden; } .blocks h3 { margin-bottom: 15px; } .block-header { background-color: #2B2D42; color: #fff; padding: 10px; } .block-header span { font-weight: bold; } #system-button { height: 40px; width: 40%; font-size: 25px; font-weight: bold; background-color: #fc4138; border: none; border-radius: 20px; color: #fff; cursor: pointer; transition: 0.5s; appearance: nonex; } .control-input { width: 30%; height: 20px; } hr { background-color: black; margin-top: 20px; } #base { position: relative; width: 100px; height: 150px; border: solid 1px black; top: -90px; } .wheel { width: 20px; height: 50px; border: solid 1px black; } #wheel1 { position: relative; left: 61px; top: 21px; } #wheel2 { position: relative; left: -61px; top: -30px; } #wheel3 { position: relative; left: -61px; top: -155px; } #wheel4 { position: relative; left: 61px; top: -206px; } .circle { width: 25px; height: 25px; border-radius: 50%; display: inline-block; background-color: #fc4138; position: relative; top: 2px; } .ir-container { margin: 10px; } table th { width: 15%; } .row-text { text-align: left; } .row-status { text-align: right; } .parameter-submit { height: 40px; width: 50%; font-size: 18px; background-color: #2B2D42; color: #fff; border: solid 1px #2B2D42; border-radius: 4px; cursor: pointer; margin-top: 12.5px; } @media only screen and (max-width: 600px) { body { overflow: scroll; } .blocks { width: 75%; display: block; margin: 15px; } #ci-container { width: 50%; } } </style></head><body> <div class='container'> <header> <div id='acm-container'> <span>ACM</span> </div> <div id='ci-container'> <span>Control Interface</span> </div> </header> <section> <center> <div class='blocks' id='control'> <div class='block-header'> <span>Control</span> </div> <h3>System</h3> <button id='system-button'>OFF</button> <hr> <h3>Turn Delay</h3> <input id='turn-delay-input' class='control-input' type='number' min='0' max='7000' placeholder='0 - 7000 (ms)' oninput='setEqual(this, document.querySelector(`#turn-delay-range`), 0, 7000)'> <input id='turn-delay-range' type='range' min='0' max='7000' value='750' oninput='setEqual(this, document.querySelector(`#turn-delay-input`), 0, 7000)'> <hr> <h3>Obstacle Threshold</h3> <input id='obstacle-input' class='control-input' type='number' min='0' max='30' placeholder='0 - 30 (cm)' oninput='setEqual(this, document.querySelector(`#obstacle-range`), 0, 7000)'> <input id='obstacle-range' type='range' min='0' max='30' value='3' oninput='setEqual(this, document.querySelector(`#obstacle-input`), 0, 30)'> <hr> <button id='data-submit' class='parameter-submit'>Submit</button> </div> <div class='blocks'> <div class='block-header'> <span>Motors</span> </div> <h3>Motor Speed</h3> <input id='speed-input' class='control-input' type='number' min='0' max='255' placeholder='0 - 255' oninput='setEqual(this, document.querySelector(`#speed-range`), 0, 255)'> <input id='speed-range' type='range' min='0' max='255' value='255' oninput='setEqual(this, document.querySelector(`#speed-input`), 0, 255)'> <hr> <h3>Motor Layout</h3> <div class='car'> <div class='wheel' id='wheel1'></div> <div class='wheel' id='wheel2'></div> <div id='base'></div> <div class='wheel' id='wheel3'></div> <div class='wheel' id='wheel4'></div> </div> </div> <div class='blocks'> <div class='block-header'> <span>Sensors</span> </div> <div id='ir-sensors'> <h3>IR Sensors</h3> <hr> <table> <tr> <th class='row-text'>Left</th> <th class='row-status'><div class='circle' id='ir-left'></div></th> </tr> <tr> <th class='row-text'>Front</th> <th class='row-status'><div class='circle' id='ir-front'></div></th> </tr> <tr> <th class='row-text'>Right</th> <th class='row-status'><div class='circle' id='ir-right'></div></th> </tr> </table> </div> <hr> <div id='ultrasonic-sensor-container'> <h3>Ultrasonic Distance Sensor</h3> <hr> <table> <tr> <th class='row-text'>Front</th> <th class='row-status'><span id='us-value'></span>cm</th> </tr> </table> <hr> </div> </div> </center> </section> </div> <script> var Socket; function init() { Socket = new WebSocket('ws://' + window.location.hostname + ':8080/'); Socket.onmessage = (event) => { if(event.data == 'TOO_MANY_CLIENTS') { alert('Another client is already using the interface!'); } else if(event.data == 'SUCCESS_ON') { system_btn.style.backgroundColor = '#53ff40'; system_btn.innerHTML = 'ON'; } else if(event.data == 'SUCCESS_OFF') { system_btn.style.backgroundColor = '#fc4138'; system_btn.innerHTML = 'OFF'; } else if(event.data == 'SUCCESS_DATA') { alert('Parameters have been succesfully updated!'); } else { let obj = JSON.parse(event.data); updateSensors(obj); updateMotors(obj); } }; } window.onload = () => { init(); }; let system_btn = document.querySelector('#system-button'); system_btn.addEventListener('click', () => { if(system_btn.innerHTML == 'ON') { Socket.send('OFF'); } else { Socket.send('ON'); } }); let submit_btn = document.querySelector('#data-submit'); submit_btn.addEventListener('click', () => { let delay_input = document.querySelector('#turn-delay-input'); let obstacle_input = document.querySelector('#obstacle-input'); let speed_input = document.querySelector('#speed-input'); if(!delay_input.value || !obstacle_input.value || !speed_input.value) { alert('Empty fields are not allowed!'); } else { let obj = { delay_input: delay_input.value, obstacle_input: obstacle_input.value, speed_input: speed_input.value }; let json_obj = JSON.stringify(obj); Socket.send(json_obj); alert('Parameters have been send to the ESP32!'); } }); function setEqual(el1, el2, min, max) { if(el1.value < min) { el1.value = min; } else if(el1.value > max) { el1.value = max; } el2.value = el1.value; } function updateSensors(obj) { document.querySelector('#us-value').innerHTML = obj.obstacle; if(obj.ir1 == 1) { document.querySelector('#ir-left').style.backgroundColor = '#53ff40'; } else { document.querySelector('#ir-left').style.backgroundColor = '#fc4138'; } if(obj.ir2 == 1) { document.querySelector('#ir-front').style.backgroundColor = '#53ff40'; } else { document.querySelector('#ir-front').style.backgroundColor = '#fc4138'; } if(obj.ir3 == 1) { document.querySelector('#ir-right').style.backgroundColor = '#53ff40'; } else { document.querySelector('#ir-right').style.backgroundColor = '#fc4138'; } } function updateMotors(obj) { if(obj.motor1 == 1) { document.querySelector('#wheel1').style.backgroundColor = '#fc4138'; } else { document.querySelector('#wheel1').style.backgroundColor = '#ffffff'; } if(obj.motor2 == 1) { document.querySelector('#wheel2').style.backgroundColor = '#fc4138'; } else { document.querySelector('#wheel2').style.backgroundColor = '#ffffff'; } if(obj.motor3 == 1) { document.querySelector('#wheel3').style.backgroundColor = '#fc4138'; } else { document.querySelector('#wheel3').style.backgroundColor = '#ffffff'; } if(obj.motor4 == 1) { document.querySelector('#wheel4').style.backgroundColor = '#fc4138'; } else { document.querySelector('#wheel4').style.backgroundColor = '#ffffff'; } } </script></body></html>";

// millis timer variables for the websocket refresh
unsigned long previousTime = millis();
int intervalTime = 250;

// functions to queue and dequeue webclients
void enqueue(int value);
byte dequeue();
byte showFront();

// client queue variables
const int CLIENTS_MAX_SIZE = 100;
int rear = -1;
int front = -1;

int active_clients = 0;
byte clients[CLIENTS_MAX_SIZE];

// Motor driver pins
/*  IN1+IN2 LV
    IN3+IN4 RV
    IN5+IN6 LA
    IN7+IN8 RA 
*/
#define IN1 32
#define IN2 33
#define IN3 27
#define IN4 14

#define IN5 17
#define IN6 16
#define IN7 2
#define IN8 15

// Infrared sensor pins
#define IR_LEFT 34
#define IR_RIGHT 36
#define IR_FRONT 39

// Ultrasonic sensor pins
#define TRIG_PIN 26
#define ECHO_PIN 25

// Reed Switch pin
#define REED_PIN 23

// Motorstatus is to turn on/off
bool motorStatus = false;

// Millis variables used to control the turn delay
unsigned long turnPrevious = millis();
unsigned long turnInterval = 350;

// Variables for the ultrasonic distance sensor
float distanceThreshold = 5.0;
long distance;

// Variables that track the status of each wheel
bool motorOneStatus = false;
bool motorTwoStatus = false;
bool motorThreeStatus = false;
bool motorFourStatus = false;

// Possible states after making a turn
enum directionState {RIGHT, LEFT, BACKWARD, ULTRA_RIGHT, ULTRA_LEFT} previousDirection;

// motor turn functions
void turnRight() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);

  digitalWrite(IN5, LOW);
  digitalWrite(IN6, HIGH);
  digitalWrite(IN7, HIGH);
  digitalWrite(IN8, LOW);

  motorOneStatus = false;
  motorTwoStatus = true;
  motorThreeStatus = false;
  motorFourStatus = true;
}

void turnLeft() {
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);

  digitalWrite(IN5, HIGH);
  digitalWrite(IN6, LOW);
  digitalWrite(IN7, LOW);
  digitalWrite(IN8, HIGH);

  motorOneStatus = true;
  motorTwoStatus = false;
  motorThreeStatus = true;
  motorFourStatus = false;
}

void moveForward() {
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);

  digitalWrite(IN5, HIGH);
  digitalWrite(IN6, LOW);
  digitalWrite(IN7, HIGH);
  digitalWrite(IN8, LOW);

  motorOneStatus = true;
  motorTwoStatus = true;
  motorThreeStatus = true;
  motorFourStatus = true;
}

void moveBackward() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);

  digitalWrite(IN5, LOW);
  digitalWrite(IN6, HIGH);
  digitalWrite(IN7, LOW);
  digitalWrite(IN8, HIGH);

  motorOneStatus = true;
  motorTwoStatus = true;
  motorThreeStatus = true;
  motorFourStatus = true;
}

void motorStop() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
  
  digitalWrite(IN5, LOW);
  digitalWrite(IN6, LOW);
  digitalWrite(IN7, LOW);
  digitalWrite(IN8, LOW);

  motorOneStatus = false;
  motorTwoStatus = false;
  motorThreeStatus = false;
  motorFourStatus = false;
}

// Sets distance of ultrasonic distance sensor in the global "distance" variable
void sensorDistance() {
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(100);
  digitalWrite(TRIG_PIN, LOW);

  long duration = pulseIn(ECHO_PIN, HIGH);
  distance = duration * 0.034 / 2;
}

void setup() {
  Serial.begin(921600);

  // Define pinmodes
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);

  pinMode(IN5, OUTPUT);
  pinMode(IN6, OUTPUT);
  pinMode(IN7, OUTPUT);
  pinMode(IN8, OUTPUT);

  pinMode(IR_LEFT, INPUT);
  pinMode(IR_FRONT, INPUT);
  pinMode(IR_RIGHT, INPUT);

  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);

  pinMode(REED_PIN, INPUT);

  // Initialize WiFi and HTTP Server
  initWiFi(ssid, pass);
  initServer();
}

void loop() {
  // Handles clients
  server.handleClient();
  webSocket.loop();

  // Sends data to the website every 250ms
  if(millis() - previousTime >= intervalTime) {
    // Set global "distance" variable
    sensorDistance();

    // Creating JSON object and json output string
    String jsonString = "";
    JsonObject object = doc_tx.to<JsonObject>();
   
    // Setting data into JSON object
    object["obstacle"] = distance;
    object["ir1"] = digitalRead(IR_LEFT);
    object["ir2"] = digitalRead(IR_FRONT);
    object["ir3"] = digitalRead(IR_RIGHT);

    object["motor1"] = motorOneStatus;
    object["motor2"] = motorTwoStatus;
    object["motor3"] = motorThreeStatus;
    object["motor4"] = motorFourStatus;

    // Turning JSON object into string
    serializeJson(doc_tx, jsonString);

    // Broadcasting JSON string
    webSocket.broadcastTXT(jsonString);

    previousTime = millis();
  }

  // ACM path logic
  if(motorStatus && digitalRead(REED_PIN) == LOW) {
    if(millis() - turnPrevious >= turnInterval) {
      if(distance <= distanceThreshold) {
        moveBackward();
        delay(500);
        
        switch(previousDirection) {
          case RIGHT:
            turnRight();
            previousDirection = ULTRA_RIGHT;
            turnPrevious = millis();
            break;
          case LEFT:
            turnLeft();
            previousDirection = ULTRA_LEFT;
            turnPrevious = millis();
            break;
          case ULTRA_RIGHT:
            turnLeft();
            previousDirection = ULTRA_LEFT;
            turnPrevious = millis();
            break;
          case ULTRA_LEFT:
            turnRight();
            previousDirection = ULTRA_RIGHT;
            break;
        }
        turnPrevious = millis();
      } else if(digitalRead(IR_LEFT) == LOW && digitalRead(IR_RIGHT) == LOW) {
        moveForward();
      } else if(digitalRead(IR_RIGHT) == LOW) {
        moveBackward();
        delay(300);
        turnLeft();
        previousDirection = LEFT;
        turnPrevious = millis();
      } else if(digitalRead(IR_LEFT) == LOW) {
        moveBackward();
        delay(300);
        turnRight();
        previousDirection = RIGHT;
        turnPrevious = millis();
      } else if(digitalRead(IR_FRONT) == LOW) {
        moveBackward();
        delay(300);
        if(previousDirection == RIGHT) {
          turnRight();
          turnPrevious = millis();
        } else if(previousDirection == LEFT) {
          turnLeft();
          turnPrevious = millis();
        }
        turnPrevious = millis();  
      }
    }
  } else {
    motorStop();
  }
}

void initWiFi(String ssid, String password) {
  WiFi.begin(ssid, password);
  Serial.println("Establishing connection to WiFi with ssid: " + ssid);

  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();

  Serial.print("Connected to network with IP address: ");
  Serial.print(WiFi.localIP());
  Serial.println("/9271d6eecedd55fcfa6143a33029d496");
}

void initServer() {
  server.on("/9271d6eecedd55fcfa6143a33029d496", []() {
    server.send(200, "text/html", index_html);
  });

  server.begin();

  webSocket.begin();
  webSocket.onEvent(webSocketEvent);
}

void webSocketEvent(byte client_num, WStype_t type, uint8_t* payload, size_t length) {
  switch(type) {
    case WStype_CONNECTED:
      Serial.println("Client connected!");
      enqueue(client_num);
      active_clients += 1;
      break;
    case WStype_DISCONNECTED:
      Serial.println("Client disconnected!");
      active_clients -= 1;
      dequeue();
      break;
    case WStype_TEXT:
      if(client_num == showFront()) {
        String data = charArrayToString(payload, length);
        Serial.println(data);
        if(data == "ON") {
          motorStatus = true;
          webSocket.sendTXT(client_num, "SUCCESS_ON");
        } else if(data == "OFF") {
          motorStatus = false;
          webSocket.sendTXT(client_num, "SUCCESS_OFF");
        } else {
          DeserializationError error = deserializeJson(doc_rx, payload);
          // IMPLEMENT: Update parameters of ACM
          Serial.println((int)doc_rx["delay_input"]);
          Serial.println((int)doc_rx["obstacle_input"]);
          Serial.println((int)doc_rx["speed_input"]);

          turnInterval = (int)doc_rx["delay_input"];
          distanceThreshold = (float)doc_rx["obstacle_input"];
          webSocket.sendTXT(client_num, "SUCCESS_DATA");
        }

      } else {
        webSocket.sendTXT(client_num, "TOO_MANY_CLIENTS");
      }
  }
}

String charArrayToString(uint8_t* array, size_t size) {
  String str = "";
  for(int i=0; i<size; i++) {
    str += (char)array[i];
  }
  return str;
}

void enqueue(int value) {
  if (rear == CLIENTS_MAX_SIZE - 1) {
    // Queue is full
    Serial.println("Queue is full!");
  } else {
    if (front == -1) {
      // If the queue is empty, set front to 0
      front = 0;
    }
    rear++;
    clients[rear] = value;
  }
}

byte dequeue() {
  byte value = -1;
  if (front == -1) {
    Serial.println("Queue is empty!");
  } else {
    value = clients[front];
    if (front == rear) {
      front = rear = -1;
    } else {
      front++;
    }
  }
  return value;
}

byte showFront() {
  if (front == -1) {
    return 0;
  } else {
    return clients[front];
  }
}
#include <ArduinoJson.h>

StaticJsonDocument<200> docRx;
StaticJsonDocument<200> docTx;

void setup() {
  Serial.begin(9600);
}

void loop() {

}

void transferNumber(String type, int num) {
  docTx["type"] = type;
  docTx["data"] = num;

  serializeJson(docTx, Serial);
}

void transferString(String type, String data) {
  docTx["type"] = type;
  docTx["data"] = data;

  serializeJson(docTx, Serial);
  Serial.println();
}

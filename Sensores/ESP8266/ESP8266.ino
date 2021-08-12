//ESP8266
#include <ESP8266WebServer.h>
#include <ESP8266WiFi.h>

ESP8266WebServer server;

const char* ssid = "NETLIFE-BARATAU";
const char* contraseña = "Antonio68";
void setup() {
  // put your setup code here, to run once:
  WiFi.begin(ssid,contraseña);
  Serial.begin(9600);
  while (WiFi.status()!=WL_CONNECTED){
    Serial.print(".");
    delay(500);
    
  }
  Serial.print(" ");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  // put your main code here, to run repeatedly:

}

//ESP8266
#include <ESP8266WebServer.h>
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#define FIREBASE_HOST "pruebas-782c9-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "kUM0iAZER1fY3T5Q3T0JWGdzXKbDKG3mdtw37nvK"

ESP8266WebServer server;
WiFiClient client;
FirebaseData firebaseData;
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

  Firebase.begin(FIREBASE_HOST,FIREBASE_AUTH);
}

void loop() {
  // put your main code here, to run repeatedly:
  Firebase.setString(firebaseData,"temperatura",String("30"));
  Firebase.setString(firebaseData,"humedad",String("1"));
}

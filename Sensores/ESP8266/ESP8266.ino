//ESP8266
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#define FIREBASE_HOST "analizadoraire-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "fg5qwePSPATyMEn5OPm6y1nkTyXB6lyCwSrM9UuJ"
#include "DHT.h"
#define DHTTYPE DHT11
#define DHTpin 14
FirebaseData firebaseData;
const char* ssid = "NETLIFE-BARATAU";
const char* contraseña = "Antonio68";
float temperatura = 0;
float humedad = 0;
float calidad = 0;
String mac = "50:02:91:EC:B8:6B";
bool isRegistered = false;
bool alarma = false;
float bateria = 100;
String regCode = "98765432";
DHT dht(DHTpin, DHTTYPE); //iniciar dht11

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  dht.begin();
  /*while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
    }*/
  WiFi.begin(ssid, contraseña);
  WiFi.begin(ssid, contraseña);
  while (WiFi.status() != WL_CONNECTED) {
    while (WiFi.status() != WL_CONNECTED) {
      Serial.print(".");
      delay(500);
    }
    Serial.print(" ");
    Serial.print("IP: ");
    Serial.println(WiFi.localIP());
    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
    Firebase.setString(firebaseData, regCode+ "/regCode", regCode);
  }
}

void loop() {
  // put your main code here, to run repeatedly:
  temperatura = dht.readTemperature();
  humedad = dht.readHumidity();
  calidad = analogRead(A0);
  //Advertencias
  if (calidad < 181) {
    alarma = false;
  } else if (calidad >= 181 && calidad < 225) {
    alarma = false;
  } else if (calidad >= 225 && calidad < 300) {
    alarma = true;
  } else if (calidad >= 300 && calidad < 350) {
    alarma = true;
  } else if (calidad
             >= 350) {
    alarma = true;
  }

  if (temperatura < 22) {
    alarma = false;
  } else if (temperatura >= 22 && temperatura < 24) {
    alarma = false;
  } else if (temperatura >= 24 && temperatura < 25) {
    alarma = false;
  } else if (temperatura >= 25 && temperatura < 27) {
    alarma = false;
  } else if (temperatura > 27) {
    alarma = true;
  }

  if (humedad >= 50) {
    alarma = true;
  } else if (humedad >= 33 && humedad < 40) {
    alarma = false;
  }
  Firebase.setFloat(firebaseData, regCode + "/temperatura", temperatura);
  Firebase.setFloat(firebaseData, regCode +"/humedad", humedad);
  Firebase.setFloat(firebaseData, regCode +"/calidad", calidad);
  Firebase.setFloat(firebaseData, regCode +"/bateria", bateria);
  Firebase.setBool(firebaseData, regCode +"/isRegistered", isRegistered);
  Firebase.setBool(firebaseData, regCode +"/alarma", alarma);
  delay(5000);
}

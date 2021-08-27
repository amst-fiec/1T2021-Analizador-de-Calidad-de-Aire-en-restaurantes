#include "DHT.h"
#include <SoftwareSerial.h>
#define DHTPIN 2
#define DHTTYPE DHT11 // DHT 11
#define LEDVERDE 5
#define LEDROJO 6
#define PILA 1
int analogValor;
float voltaje = 0;
float minimo = 2.7;
SoftwareSerial espSerial(5, 6);//Definición pines de serial
DHT dht(DHTPIN, DHTTYPE);
String str;
void setup() {
  Serial.begin(115200);
  espSerial.begin(115200);
  dht.begin();
  pinMode(LEDVERDE, OUTPUT);
  pinMode(LEDROJO, OUTPUT);
  delay(2000);
}
void loop(){
  //Lecturas de sensores
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  float calidad = analogRead(A0);
  str = String(h) + String(";") + String(t)+ String(";") + String(calidad);//Envío de info a ESP
  
  analogValor = analogRead(PILA);
  voltaje = 0.0048 * analogValor;

/*Codigo para leer batería*/
  if (voltaje >= minimo) {
    digitalWrite (LEDVERDE, HIGH);
    digitalWrite (LEDROJO, LOW);
  } else {
    digitalWrite (LEDROJO, HIGH);
    digitalWrite (LEDVERDE, LOW);
  }delay(4000);
  
}

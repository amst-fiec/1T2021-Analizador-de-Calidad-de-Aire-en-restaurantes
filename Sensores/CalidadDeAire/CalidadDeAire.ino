#include "DHT.h"
#define DHTTYPE DHT11

float temperatura = 0;
float humedad = 0;
float medidaMQ135 = 0;
float calidadAire = 0;
int pinMQ135 =A1;
int pinDHT11=3;
//int pinESP =;
int pinLEDR=4;
int pinVERDE=5;
int R0=10;
DHT dht(pinDHT11,DHTTYPE);//iniciar dht11

void setup() {
  // put your setup code here, to run once:
  //pinmode(,OUTPUT);
  dht.begin();
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  temperatura = dht.readTemperature();
  humedad = dht.readHumidity();
  medidaMQ135 = analogRead(pinMQ135);
  Serial.println(medidaMQ135,DEC);
  Serial.println(temperatura);
  Serial.print("°C");
  Serial.println(humedad);
  Serial.print("%");
  delay(10000);
}

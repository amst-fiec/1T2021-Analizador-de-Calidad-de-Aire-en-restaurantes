#include "DHT.h"
#define DHTTYPE DHT11

float temperatura = 0;
float humedad = 0;
float medidaMQ135 = 0;
float calidadAire = 0;
int pinMQ135 =A1;
int pinDHT11=3;
int pinSalTemp = 5;
int pinSalHum = 6;

int pinLEDR=4;
int pinVERDE=5;
float enviarTemp = 0;
float enviarHum = 0;
float enviarCal = 0;

DHT dht(pinDHT11,DHTTYPE);//iniciar dht11

void setup() {
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
  
  //Advertencias
  if (medidaMQ135<181){
      Serial.print("BUEN NIVEL DE GAS");  
  }else if(medidaMQ135>=181 && medidaMQ135<225){
    Serial.print("POBRE NIVEL DE GAS");  
  }else if(medidaMQ135>=225 && medidaMQ135<300){
    Serial.print("MAL NIVEL DE GAS");  
  }else if(medidaMQ135>=300 && medidaMQ135<350){
    Serial.print("Muerto");  
  }else if(medidaMQ135>=350){
    Serial.print("TOXICO");  
  }
  

  if (temperatura<22){
    Serial.print("PELIGRO MUY FRIO");  
  }else if(temperatura>=22 && temperatura<24){
    Serial.print("FRIO");  
  }else if(temperatura>=24 && temperatura<25){
    Serial.print("CONFORT");  
  }else if(temperatura>=25 && temperatura<27){
    Serial.print("CALIENTE");  
  }else if(temperatura>27){
    Serial.print("PELIGRO SOFOCANTE");  
  }

  if(humedad>=50){
    Serial.print("PELIGRO HUMEDAD");
  }else if(humedad>=33 && humedad<40){
    Serial.print("CONFORT HUMEDAD");
  }

 // analogWrite(pinSalTemp,temperatura);
 // analogWrite(pinSalHum, humedad);
  delay(10000);
}

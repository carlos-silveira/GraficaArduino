int pot;

void setup() {
  // put your setup code here, to run once:
  pinMode(A0, INPUT);
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  Serial.begin(9600);
  Serial.setTimeout(100);
}

void loop() {
  // put your main code here, to run repeatedly:
  pot = analogRead(A0);
//  pot = (pot*100*5)/1023;
  pot= map(pot,0,1023,0,100);
  
  if (pot > 50){
    digitalWrite(2, HIGH);  
  }
  else {
    digitalWrite(2, LOW);
    }
  if(Serial.available()>0){
    if (Serial.readString() == "Hola") {
      if (digitalRead(3) == HIGH) {
          digitalWrite(3, LOW);    
        }
        else {
            digitalWrite(3, HIGH);
          }
//    digitalWrite(3, HIGH);  
  }
//  if (Serial.readString() == "Holi") {
      
//  } 
  }
  Serial.println(pot);
  delay(200);
}

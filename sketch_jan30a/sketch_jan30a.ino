int pot;

void setup() {
  // put your setup code here, to run once:
  pinMode(A0, INPUT);

  Serial.begin(9600);

}

void loop() {
  // put your main code here, to run repeatedly:
  pot = analogRead(A0);
//  pot = (pot*100*5)/1023;
  pot= map(pot,0,1023,0,10);
  Serial.println(pot);
 
}

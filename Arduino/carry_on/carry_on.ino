int ledRedPin = 5;//2
int ledBluePin =6;//4
int ledGreenPin = 7;//3
int switchPin = A0;
int pressSensor_1_Pin = A1;
int pressSensor_2_Pin = A2;
double flexi_porce_resiter;
 double pound;
 double k_gram;
 
int data= 0;
String inString;
String pressValueString;
int inputData;
int flag= LOW; // for checking switch
double pressValue= 0;
double changdWeight(double mass);
void setup() {
    Serial.begin(115200);               //initial the Serial
    // initial setting for pin
    pinMode(ledRedPin,OUTPUT);
    pinMode(ledBluePin,OUTPUT);
    pinMode(ledGreenPin,OUTPUT);
    pinMode(switchPin,INPUT);
    pinMode(pressSensor_1_Pin,INPUT);
    pinMode(pressSensor_2_Pin,INPUT);
    
    // LED off
    digitalWrite(ledRedPin,LOW);
    digitalWrite(ledBluePin,LOW);
    digitalWrite(ledGreenPin,LOW);
    
}


void loop()
{
    data = digitalRead(switchPin);  // check the value of pushing the switch
    if(data ==HIGH && flag == LOW){ // if switch is pushed
      pressValue=changdWeight(analogRead(pressSensor_1_Pin));
      pressValue+=changdWeight(analogRead(pressSensor_2_Pin));
      pressValueString = String(pressValue);
      Serial.write("weight=");        
      Serial.println(pressValue,2);
      //char* cPressValueString = (char*) malloc(sizeof(char)*(pressValueString.length() + 1));
     // pressValueString.toCharArray(cPressValueString, pressValueString.length() + 1);
     // Serial.write(cPressValueString);
     //Serial.write("\n");
      flag=HIGH; // change the flag to high
      data=0; // initialize value various 
    }else if(flag == HIGH && data == 0){ 
      flag= LOW;  
    }
    delay(20);
    
    
    if(Serial.available())  // signal input from smart phone
    {
         while (Serial.available() > 0) { // checking the message number
            int inChar = Serial.read();  // save the value
            if (isDigit(inChar)) {
              // convert the incoming byte to a char
              // and add it to the string:
              inString += (char)inChar;
            }
            // if you get a newline, print the string,
            // then the string's value:
          }
          inputData=inString.toInt();// string to integer
          // clear the string for new input:
          inString = "";

          //arduino works function following the massage from smart phone
          ledSet();      
          switch(inputData){
            case 100:
              digitalWrite(ledRedPin,HIGH);
              break;
            case 101:
              digitalWrite(ledGreenPin,HIGH);
              digitalWrite(ledRedPin,HIGH);
              //digitalWrite(ledBluePin,HIGH);
              break;
            case 102:
              digitalWrite(ledGreenPin,HIGH);
              break;
            case 103:
              digitalWrite(ledGreenPin,HIGH);
              digitalWrite(ledRedPin,HIGH);
              digitalWrite(ledBluePin,HIGH);
              break;
          }
          inputData = 0;
    }
}

void ledSet(){
    digitalWrite(ledRedPin,LOW);
    digitalWrite(ledBluePin,LOW);
    digitalWrite(ledGreenPin,LOW);
}
double changdWeight(double mass)
{
  flexi_porce_resiter = 56.5*(1023-(double)mass)/((double)mass+1);//R1 is 526k ohm
pound = 1333.33/flexi_porce_resiter; // 
k_gram =pound* 0.45359237;
return k_gram;
}

int ledRedPin = 2;
int ledOrrangePin =3;
int ledGreenPin = 4;
int switchPin = A0;
int pressSensorPin = A1;

int data= 0;
String inString;
String pressValueString;
int inputData;
int flag= LOW; // for checking switch
int pressValue= 0;

void setup() {
    Serial.begin(115200);               //initial the Serial
    // initial setting for pin
    pinMode(ledRedPin,OUTPUT);
    pinMode(ledOrrangePin,OUTPUT);
    pinMode(ledGreenPin,OUTPUT);
    // LED off
    digitalWrite(ledRedPin,HIGH);
    digitalWrite(ledOrrangePin,HIGH);
    digitalWrite(ledGreenPin,HIGH);
    
}


void loop()
{
    data = analogRead(switchPin);  // check the value of pushing the switch
    if(data>500 && flag == LOW){ // if switch is pushed
      pressValue = analogRead(pressSensorPin);
      pressValueString = String(pressValue);
      Serial.write("weight=");        
      char* cPressValueString = (char*) malloc(sizeof(char)*(pressValueString.length() + 1));
      pressValueString.toCharArray(cPressValueString, pressValueString.length() + 1);
      Serial.write(cPressValueString);
      Serial.write("\n");
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
              digitalWrite(ledRedPin,LOW);
              break;
            case 101:
              digitalWrite(ledOrrangePin,LOW);
              break;
            case 102:
              digitalWrite(ledGreenPin,LOW);
              break;
          }
          inputData = 0;
    }
}

void ledSet(){
    digitalWrite(ledRedPin,HIGH);
    digitalWrite(ledOrrangePin,HIGH);
    digitalWrite(ledGreenPin,HIGH);
}


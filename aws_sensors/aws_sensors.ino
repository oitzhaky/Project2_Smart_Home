#include <Arduino.h>
#include <Stream.h>
#include <IRsend.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ArduinoJson.h>

//AWS
#include "sha256.h"
#include "Utils.h"
#include "AWSClient2.h"

//WEBSockets
#include <Hash.h>
#include <WebSocketsClient.h>

//MQTT PAHO
#include <SPI.h>
#include <IPStack.h>
#include <Countdown.h>
#include <MQTTClient.h>

//AWS MQTT Websocket
#include "Client.h"
#include "AWSWebSocketClient.h"
#include "CircularByteBuffer.h"

//AWS IOT config, change these:
char wifi_ssid[]       = "Izhaki";
char wifi_password[]   = "0543273582";
char aws_endpoint[]    = "a3n7np6iiz4al5.iot.us-east-1.amazonaws.com";
char aws_key[]         = "AKIAILSQ743W4BJSZ67Q";
char aws_secret[]      = "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";
char aws_region[]      = "us-east-1";
const char* aws_topic  = "sensors/data";
int port = 443;

//MQTT config
const int maxMQTTpackageSize = 512;
const int maxMQTTMessageHandlers = 1;

ESP8266WiFiMulti WiFiMulti;

AWSWebSocketClient awsWSclient(1000);

IPStack ipstack(awsWSclient);
MQTT::Client<IPStack, Countdown, maxMQTTpackageSize, maxMQTTMessageHandlers> *client = NULL;

//# of connections
long connection = 0;

//generate random mqtt clientID
char* generateClientID () {
  char* cID = new char[23]();
  for (int i = 0; i < 22; i += 1)
    cID[i] = (char)random(1, 256);
  return cID;
}

//count messages arrived
int arrivedcount = 0;

//IR Remote
IRsend irsend(4);  // An IR LED is controlled by GPIO pin 4 (D2)

// Example of data captured by IRrecvDumpV2.ino
uint16_t rawData[105] = {3472, 1778,  404, 466,  402, 1346,  404, 466,  402, 468,  400, 466,  400, 466,  402, 466,  400, 466,  400, 466,  402, 466,  400, 466,  400, 466,  400, 468,  400, 1346,  446, 426,  442, 426,  442, 430,  438, 424,  444, 424,  444, 424,  444, 422,  446, 426,  442, 422,  444, 1302,  446, 432,  436, 428,  440, 430,  438, 428,  440, 434,  434, 430,  436, 430,  436, 434,  432, 440,  468, 394,  472, 398,  468, 400,  430, 436,  436, 1304,  404, 478,  426, 440,  392, 480,  426, 436,  392, 470,  440, 430,  394, 474,  394, 1346,  404, 494,  374, 1342,  404, 10980,  414, 224,  196, 20160,  218}; 


//LED config
bool lights = false;
//int Led = 2;
char* lightsOff = "{\"state\":{\"reported\":{\"on\":false}}}";
char* lightsOn = "{\"state\":{\"reported\":{\"on\":true}}}";
char* tvVolOn = "{\"tv\":{\"on\"}}";

//motion detection
int inputPin = D1;               // choose the input pin (for PIR sensor)
int pirState = LOW;             // we start, assuming no motion detected
int val = 0;                    // variable for reading the pin status
char* state = "";
char* SENSOR_NAME = "motion";

//json lib
#define MESSAGE_LENGTH 512
StaticJsonBuffer<MESSAGE_LENGTH> jsonBuffer;
JsonObject& root = jsonBuffer.createObject();


//callback to handle mqtt messages
void messageArrived(MQTT::MessageData& md)
{
  MQTT::Message &message = md.message;

  Serial.print("Message ");
  Serial.print(++arrivedcount);
  Serial.print(" arrived: qos ");
  Serial.print(message.qos);
  Serial.print(", retained ");
  Serial.print(message.retained);
  Serial.print(", dup ");
  Serial.print(message.dup);
  Serial.print(", packetid ");
  Serial.println(message.id);
  Serial.print("Payload ");
  char* msg = new char[message.payloadlen + 1]();
  memcpy (msg, message.payload, message.payloadlen);
  
  JsonObject& updatedRoot = jsonBuffer.parseObject(msg);
  root["climate"] = updatedRoot["climate"];
  root["time"] = updatedRoot["time"];
  root["location"] = updatedRoot["location"];
  sendmessage();
  
//  char* msgWithoutspaces = removeSpaces(msg);
//  Serial.println(msgWithoutspaces);

//  //logic to parse the message and change
//  if (strcmp(msgWithoutspaces, lightsOn) == 0) {
//    lights = true;
//  } else if(strcmp(msgWithoutspaces, lightsOff) == 0) {
//    lights = false;
//  } else if(strcmp(msgWithoutspaces, tvVolOn) == 0){
//      irsend.sendRaw(rawData, 105, 38);  // Send a raw data capture at 38kHz.
//      delay(2000);
//      irsend.sendRaw(rawData, 105, 38);  // Send a raw data capture at 38kHz.
//  }
//  //lights = !lights;
//
//  if (lights == true) {
//    digitalWrite(LED_BUILTIN, LOW);
//  } else {
//    digitalWrite(LED_BUILTIN, HIGH);
//  }


  delete msg;
}

//connects to websocket layer and mqtt layer
bool connect () {

  if (client == NULL) {
    client = new MQTT::Client<IPStack, Countdown, maxMQTTpackageSize, maxMQTTMessageHandlers>(ipstack);
  } else {

    if (client->isConnected ()) {
      client->disconnect ();
    }
    delete client;
    client = new MQTT::Client<IPStack, Countdown, maxMQTTpackageSize, maxMQTTMessageHandlers>(ipstack);
  }


  //delay is not necessary... it just help us to get a "trustful" heap space value
  delay (1000);
  Serial.print (millis ());
  Serial.print (" - conn: ");
  Serial.print (++connection);
  Serial.print (" - (");
  Serial.print (ESP.getFreeHeap ());
  Serial.println (")");




  int rc = ipstack.connect(aws_endpoint, port);
  if (rc != 1)
  {
    Serial.println("error connection to the websocket server");
    return false;
  } else {
    Serial.println("websocket layer connected");
  }


  Serial.println("MQTT connecting");
  MQTTPacket_connectData data = MQTTPacket_connectData_initializer;
  data.MQTTVersion = 3;
  char* clientID = generateClientID ();
  data.clientID.cstring = clientID;
  rc = client->connect(data);
  delete[] clientID;
  if (rc != 0)
  {
    Serial.print("error connection to MQTT server");
    Serial.println(rc);
    return false;
  }
  Serial.println("MQTT connected");
  return true;
}

//subscribe to a mqtt topic
void subscribe () {
  //subscript to a topic
  int rc = client->subscribe(aws_topic, MQTT::QOS0, messageArrived);
  if (rc != 0) {
    Serial.print("rc from MQTT subscribe is ");
    Serial.println(rc);
    return;
  }
  Serial.println("MQTT subscribed");
}

//send a message to a mqtt topic
void sendmessage () {
  //send a message
  MQTT::Message message;
  char buf[MESSAGE_LENGTH];
  //strcpy(buf, "{\"state\":{\"reported\":{\"on\": false}}}");
  root[SENSOR_NAME] = state;
  root.printTo((char*)buf, root.measureLength() + 1);
  message.qos = MQTT::QOS0;
  message.retained = false;
  message.dup = false;
  message.payload = (void*)buf;
  message.payloadlen = strlen(buf) + 1;
  int rc = client->publish(aws_topic, message);
}


void setup() {
  irsend.begin();
  Serial.begin (115200);
  pinMode(LED_BUILTIN, OUTPUT);   // Initialize the LED_BUILTIN pin as an output
  delay (2000);
  Serial.setDebugOutput(1);

  //fill with ssid and wifi password
  WiFiMulti.addAP(wifi_ssid, wifi_password);
  Serial.println ("connecting to wifi");
  while (WiFiMulti.run() != WL_CONNECTED) {
    delay(100);
    Serial.print (".");
  }
  Serial.println ("\nconnected");

  //fill AWS parameters
  awsWSclient.setAWSRegion(aws_region);
  awsWSclient.setAWSDomain(aws_endpoint);
  awsWSclient.setAWSKeyID(aws_key);
  awsWSclient.setAWSSecretKey(aws_secret);
  awsWSclient.setUseSSL(true);

  if (connect ()) {
    subscribe ();
    sendmessage ();
  }

  // motion setup
  pinMode(inputPin, INPUT);

  //json lib
  //root = jsonBuffer.createObject();
  root["climate"] = "";
  root["motion"] = "";
  root["time"] = "";
  root["location"] = "";
}


void loop() {
  //keep the mqtt up and running
  if (awsWSclient.connected ()) {
    client->yield();
  } else {
    //handle reconnection
    if (connect ()) {
      subscribe ();
    }
  }

  // motion detection
  val = digitalRead(inputPin);  // read input value
  if (val == HIGH) {
    if (pirState == LOW) {
      Serial.println("Motion detected!");
      pirState = HIGH;
      state = "when_arriving";
      sendmessage();
    }
  } else {
    if (pirState == HIGH){
      Serial.println("Motion ended!");
      pirState = LOW;
      state = "when_leaving";
      sendmessage();
    }
  }

  
}


/*Private Methods*/
char* removeSpaces(char *str){
    // To keep track of non-space character count
    int count = 0;
 
    // Traverse the given string. If current character
    // is not space, then place it at index 'count++'
    for (int i = 0; str[i]; i++)
        if (str[i] != ' ')
            str[count++] = str[i]; // here count is
                                   // incremented
    str[count] = '\0';
    return str;
}



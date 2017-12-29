// the setup function runs once when you press reset or power the board

// the loop function runs over and over again until power down or reset


//#include <Stream.h>
//#include <string>

//ESP general
#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>

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

//Json Library
#include <ArduinoJson.h>

//ir
#include <IRsend.h>


//AWS IOT config, change these:
char wifi_ssid[] = "Izhaki";
char wifi_password[] = "0543273582";
char aws_endpoint[] = "a3n7np6iiz4al5.iot.us-east-1.amazonaws.com";
char aws_key[] = "AKIAILSQ743W4BJSZ67Q";
char aws_secret[] = "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";
char aws_region[] = "us-east-1";
const char* aws_topic = "actions";
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
char* generateClientID() {
	char* cID = new char[23]();
	for (int i = 0; i < 22; i += 1)
		cID[i] = (char)random(1, 256);
	return cID;
}

//count messages arrived
int arrivedcount = 0;

//ArduinoJson
const int messageLength = 256;
char reportedValue[16];

//output
const char* outputName = "myoutput";
const char* operation1 = "on";
const char* operation2 = "off";
const char* operation3 = "other_operation";

//ir
IRsend irsend(4);  // An IR LED is controlled by GPIO pin 4 (D2)

//output
const int rawDataSize = 256;
uint16_t rawData[rawDataSize] = {};

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

	char* msg = new char[message.payloadlen + 1]();
	memcpy(msg, message.payload, message.payloadlen);

	StaticJsonBuffer<messageLength> jsonBuffer;
	JsonObject& root = jsonBuffer.parseObject(msg);
	if (strcmp(root[outputName], operation1) == 0) {
		// call operation1 function
		doOperation1();
	}
	else if (strcmp(root[outputName], operation2) == 0) {
		// call operation2 function
		doOperation2();
	}
	else if (strcmp(root[outputName], operation3) == 0) {
		// call operation3 function
	}

	delete msg;
}

void doOperation1() {
	irsend.sendRaw(rawData, rawDataSize, 38);
}
void doOperation2() {
	irsend.sendRaw(rawData, rawDataSize, 38);
}
void doOperation3() {
}


//connects to websocket layer and mqtt layer
bool connect() {

	if (client == NULL) {
		client = new MQTT::Client<IPStack, Countdown, maxMQTTpackageSize, maxMQTTMessageHandlers>(ipstack);
	}
	else {

		if (client->isConnected()) {
			client->disconnect();
		}
		delete client;
		client = new MQTT::Client<IPStack, Countdown, maxMQTTpackageSize, maxMQTTMessageHandlers>(ipstack);
	}

	//delay is not necessary... it just help us to get a "trustful" heap space value
	delay(1000);
	Serial.print(millis());
	Serial.print(" - conn: ");
	Serial.print(++connection);
	Serial.print(" - (");
	Serial.print(ESP.getFreeHeap());
	Serial.println(")");

	int rc = ipstack.connect(aws_endpoint, port);
	if (rc != 1)
	{
		Serial.println("error connection to the websocket server");
		return false;
	}
	else {
		Serial.println("websocket layer connected");
	}

	Serial.println("MQTT connecting");
	MQTTPacket_connectData data = MQTTPacket_connectData_initializer;
	data.MQTTVersion = 3;
	char* clientID = generateClientID();
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
void subscribe() {
	//subscript to a topic
	int rc = client->subscribe(aws_topic, MQTT::QOS0, messageArrived);
	if (rc != 0) {
		Serial.print("rc from MQTT subscribe is ");
		Serial.println(rc);
		return;
	}
	Serial.println("MQTT subscribed");
}


void setup() {
	Serial.begin(115200);
	delay(2000);
	Serial.setDebugOutput(1);

	//fill with ssid and wifi password
	WiFiMulti.addAP(wifi_ssid, wifi_password);
	Serial.println("connecting to wifi");
	while (WiFiMulti.run() != WL_CONNECTED) {
		delay(100);
		Serial.print(".");
	}
	Serial.println("\nconnected");

	//fill AWS parameters
	awsWSclient.setAWSRegion(aws_region);
	awsWSclient.setAWSDomain(aws_endpoint);
	awsWSclient.setAWSKeyID(aws_key);
	awsWSclient.setAWSSecretKey(aws_secret);
	awsWSclient.setUseSSL(true);

	//ir
	irsend.begin();
}


void loop() {
	//keep the mqtt up and running
	if (awsWSclient.connected()) {
		client->yield();
	}
	else {
		//handle reconnection
		if (connect()) {
			subscribe();
		}
	}
}



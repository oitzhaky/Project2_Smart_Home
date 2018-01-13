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

//time
#include <NTPClient.h>
#include <WiFiUdp.h>

//climate
#include <Wire.h>
#include <SFE_BMP180.h>



//AWS IOT config, change these:
char wifi_ssid[] = "Izhaki";
char wifi_password[] = "0543273582";
char aws_endpoint[] = "a3n7np6iiz4al5.iot.us-east-1.amazonaws.com";
char aws_key[] = "AKIAILSQ743W4BJSZ67Q";
char aws_secret[] = "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";
char aws_region[] = "us-east-1";
const char* aws_topic = "sensors/data";
const char* aws_sensors_info = "sensors/info";
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


//sensor
const char* sensorName = "time_room";
const char* sensorType = "time";

//motion detection
int motionInputPin = D1; // input pin for PIR sensor
int motionState = LOW; // we start, assuming no motion detected
int motionMeasurement = 0; // variable for reading the pin status

						   //temp sensor
SFE_BMP180 pressure;
int prevTemp = 0;

//time
#define NTP_OFFSET   60 * 60 * 2// in seconds
#define NTP_INTERVAL 60 * 1000 // in miliseconds
#define NTP_ADDRESS  "europe.pool.ntp.org"
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, NTP_ADDRESS, NTP_OFFSET, NTP_INTERVAL);
int timeCounter;



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
	if (strcmp(root["sender"], sensorName) == 0) {
		delete msg;
		return;
	}
	root[sensorName] = reportedValue;
	root["sender"] = sensorName;

	char buf[messageLength];
	root.printTo(buf, messageLength);

	//send a message
	MQTT::Message messageToSend;
	messageToSend.qos = MQTT::QOS0;
	messageToSend.retained = false;
	messageToSend.dup = false;
	messageToSend.payload = (void*)buf;
	messageToSend.payloadlen = strlen(buf) + 1;
	int rc = client->publish(aws_topic, messageToSend);

	delete msg;
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


//send a message to a mqtt topic
void sendmessage() {
	StaticJsonBuffer<messageLength> jsonBuffer;
	JsonObject& root = jsonBuffer.createObject();
	root[sensorName] = reportedValue;
	root["sender"] = sensorName;

	char buf[messageLength];
	root.printTo(buf, messageLength);

	//send a message
	MQTT::Message message;
	message.qos = MQTT::QOS0;
	message.retained = false;
	message.dup = false;
	message.payload = (void*)buf;
	message.payloadlen = strlen(buf) + 1;
	int rc = client->publish(aws_topic, message);
}

void sendSensorInfo() {
	StaticJsonBuffer<messageLength> jsonBuffer;
	JsonObject& root = jsonBuffer.createObject();
	root[sensorType] = sensorName;

	char buf[messageLength];
	root.printTo(buf, messageLength);

	//send a message
	MQTT::Message message;
	message.qos = MQTT::QOS0;
	message.retained = false;
	message.dup = false;
	message.payload = (void*)buf;
	message.payloadlen = strlen(buf) + 1;
	int rc = client->publish(aws_sensors_info, message);
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

	////motion setup
	//pinMode(motionInputPin, INPUT);

	////climate setup
	//Wire.pins(0, 2);
	//if (pressure.begin())
	//	Serial.println("BMP180 init success");
	//else
	//	Serial.println("BMP180 init fail\n\n");

	//time setup
	timeClient.begin();
	timeCounter = 0;
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

	sendSensorInfo();

	//// motion loop
	//motionMeasurement = digitalRead(motionInputPin);  // read input value
	//if (motionState != motionMeasurement) {
	//	if (motionMeasurement == HIGH) {
	//		Serial.println("Motion detected!");
	//		memcpy(reportedValue, "when_arriving", 16);
	//	}
	//	else if (motionMeasurement == LOW) {
	//		Serial.println("Motion ended!");
	//		memcpy(reportedValue, "when_leaving", 16);
	//	}
	//	sendmessage();
	//	motionState = motionMeasurement;
	//}

	////climate loop
	//char status;
	//double T;
	//delay(1000);
	//status = pressure.startTemperature();
	//if (status == 1)
	//{
	//	delay(status);
	//	status = pressure.getTemperature(T);
	//	if (status == 1)
	//	{
	//		//Serial.print(T, 2);
	//		char temprature[16];
	//		int t = T;
	//		sprintf(temprature, "%d", t);
	//		if (prevTemp != t) {
	//			memcpy(reportedValue, temprature, 16);
	//			sendmessage();
	//			prevTemp = t;
	//		}
	//	}
	//}

	//time loop
	timeCounter++;
	timeClient.update();
	char minutes[3];
	sprintf(minutes, "%02d", timeClient.getMinutes());
	String formattedTime = String(timeClient.getHours()) + "." + String(minutes);
	formattedTime.toCharArray(reportedValue, 16);
	if (timeCounter % 10 == 0) {
		sendmessage();
	}

}



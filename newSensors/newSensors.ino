// the setup function runs once when you press reset or power the board

// the loop function runs over and over again until power down or reset


#include <Arduino.h>
#include <Stream.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <Wire.h>
#include <SFE_BMP180.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <string>

//#include <IRsend.h>

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

//ArduinoJson
#include <ArduinoJson.h>

//AWS IOT config, change these:
char wifi_ssid[] = "Izhaki";
char wifi_password[] = "0543273582";
char aws_endpoint[] = "a3n7np6iiz4al5.iot.us-east-1.amazonaws.com";
char aws_key[] = "AKIAILSQ743W4BJSZ67Q";
char aws_secret[] = "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";
char aws_region[] = "us-east-1";
const char* aws_topic = "sensors/data";
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

//motion detection
int sensorInputPin = D1; // input pin for PIR sensor
int sensorState = LOW; // we start, assuming no motion detected
int sensorMeasurement = 0; // variable for reading the pin status

//json lib
const int messageLength = 256;
const char* sensorName = "climate";
char report[16] = "-10";

//temp sensor
SFE_BMP180 pressure;
int prevTemp = 0;

//time
#define NTP_OFFSET   60 * 60      // In seconds
#define NTP_INTERVAL 60 * 1000    // In miliseconds
#define NTP_ADDRESS  "europe.pool.ntp.org"
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, NTP_ADDRESS, NTP_OFFSET*2, NTP_INTERVAL);

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
	root[sensorName] = report;
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
	root[sensorName] = report;
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


void setup() {
	//irsend.begin();
	Serial.begin(115200);
	//pinMode(LED_BUILTIN, OUTPUT);   // Initialize the LED_BUILTIN pin as an output
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

	// motion setup
	pinMode(sensorInputPin, INPUT);

	//Temp sensor
	Wire.pins(0, 2);
	if (pressure.begin())
		Serial.println("BMP180 init success");
	else
	{
		// Oops, something went wrong, this is usually a connection problem,
		// see the comments at the top of this sketch for the proper connections.

		Serial.println("BMP180 init fail\n\n");
	}

	if (connect()) {
		subscribe();
		sendmessage();
	}

	//time
	timeClient.begin();

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
	/*
	// motion detection
	sensorMeasurement = digitalRead(sensorInputPin);  // read input value
	if (sensorMeasurement == HIGH) {
		if (sensorState == LOW) {
			Serial.println("Motion detected!");
			sensorState = HIGH;
			memcpy(report, "when_arriving", 16);
			sendmessage();
		}
	}
	else if (sensorMeasurement == LOW) {
		if (sensorState == HIGH) {
			Serial.println("Motion ended!");
			sensorState = LOW;
			memcpy(report, "when_leaving", 16);
			sendmessage();
		}
	}
	*/
	//Temp detector
	char status;
	double T;
	delay(1000);
	status = pressure.startTemperature();
	if (status != 0)
	{
		// Wait for the measurement to complete:
		delay(status);

		// Retrieve the completed temperature measurement:
		// Note that the measurement is stored in the variable T.
		// Function returns 1 if successful, 0 if failure.


		status = pressure.getTemperature(T);
		if (status != 0)
		{
			// Print out the measurement:
			//Serial.print(T, 2);
			//char temprature = char(T);
			char temprature[16];
			int t = T;
			sprintf(temprature, "%d", t);
			if (prevTemp != t) {
				memcpy(report, temprature, 16);
				sendmessage();
				prevTemp = t;
			}
		}

	}

	//time
	timeClient.update();
	char minutes[3];
	sprintf(minutes, "%02d", timeClient.getMinutes());
	String formattedTime = String(timeClient.getHours()) + "." + String(minutes);


}



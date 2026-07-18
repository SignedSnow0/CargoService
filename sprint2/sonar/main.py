from machine import Pin, time_pulse_us
import machine
import network
import time
from umqtt.simple import MQTTClient

led = Pin(46, Pin.OUT)

TRIG = Pin("D2", Pin.OUT)
ECHO = Pin("D3", Pin.IN)

blink_led = False

def parseEnv():
    envData = {}
    try:
        with open(".env", "r") as file:
            for line in file:
                line = line.strip()
                if not line or line.startswith("#"):
                    continue
                
                if "=" in line:
                    key, value = line.split("=", 1)
                    envData[key.strip()] = value.strip()
    except OSError:
        print("Error: .env file not found on the device!")
        
    return envData

def connectWifi(env):
    wifi = network.WLAN(network.STA_IF)
    
    wifi.active(False)
    time.sleep(0.1)
    wifi.active(True)
    time.sleep(0.1)
    
    ssid = env.get("WIFI_SSID")
    password = env.get("WIFI_PASSWORD")
    
    if not wifi.isconnected():
        print(f"Connecting to wifi: '{ssid}'...")
        wifi.connect(ssid, password)
        
        timeout = 20
        start_time = time.time()
        
        while not wifi.isconnected():
            if time.time() - start_time > timeout:
                print("\nConnection timed out!")
                return wifi
            
            print(".", end="")
            time.sleep(0.5)
            
    print("\nConnected to Wi-Fi successfully!")
    return wifi
    
def connectMqtt(env):
    client_id = env.get("MQTT_CLIENT_ID", "sonar")
    broker_ip = env.get("MQTT_BROKER")
    mqtt_port = int(env.get("MQTT_PORT", 1883))
    
    print(f"\nConnecting to MQTT broker: {broker_ip}:{mqtt_port}")
    client = MQTTClient(
        client_id = client_id,
        server    = broker_ip,
        port      = mqtt_port,
        keepalive = 60
    )
    
    connected = False
    while not connected:
        try:
            client.connect()
            print(f"Connected!")
            connected = True
               
        except OSError as e:
            print(f"Failed to connect to MQTT broker {broker_ip}:{mqtt_port}: {e}, retrying...")
            time.sleep(5)
            
    return client

def measureDistance():
    TRIG.value(0)
    time.sleep_us(2)
    
    TRIG.value(1)
    time.sleep_us(10)
    TRIG.value(0)
    
    dt = time_pulse_us(ECHO, 1, 30000)
    
    if dt < 0:
        return -1
    
    return (dt * 0.0343) / 2

def on_message(topic, msg):
    global blink_led
    
    msg_str = msg.decode()
    if "blinkLed(True)" in msg_str: 
        blink_led = True 
        print("Blinking enabled")
    elif "blinkLed(False)" in msg_str: 
        blink_led = False 
        print("Blinking disabled")

env = parseEnv()

wlan = connectWifi(env)
client = connectMqtt(env)
client.set_callback(on_message)

led_topic = "sonar/led"
client.subscribe(led_topic.encode())

topic = env.get("MQTT_TOPIC", "sonar/data")

index = 0
while True:
    try:
        client.check_msg()
        if blink_led:
            led.value(not led.value())
        else:
            led.value(True) #True if off
        
        if index == 2:
            distance = measureDistance()
            if distance >= 0:
                payload = f"msg(sonardata, dispatch, sonar, sonarwrapper, sonardata({distance:.2f}), 1)"
                print(f"Publishing: {payload} cm to topic '{topic}'")
                client.publish(topic.encode(), payload.encode())
            else:
                print("Sensor error")
        
        index = index + 1
        index = index % 3
        time.sleep(1)
        
    except OSError as e:
        print("Connection lost, attempting to reconnect...")
        try:
            if not wlan.isconnected():
                wlan.connect(env.get("WIFI_SSID"), env.get("WIFI_PASSWORD"))
                time.sleep(2)
                
            client.connect()
            client.subscribe(led_topic.encode())
        except Exception:
            time.sleep(2)
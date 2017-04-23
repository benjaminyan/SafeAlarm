# SafeAlarm

## Description

You know how old school fire alarms and CO detectors are still in use in many homes today?  
Because of that, people outside of their homes will not come to know if any alarm or detector  ssssss
goes off. However, we aim to find a potential solution using SafeAlarm, a smart IOT home-device.  


## How it works

Several audio recorders are placed within the proximity of the target device (i.e. Kitchen Alarm)  
and try to learn the noise around it given a test input. Then, the recorders will actually listen  
for an alarm and the maximum squares of amplitude is computed. The data that was collected from the  
alarm will be sent to the server, which relays it to your smart device via push notifications. That  
smart-device is actually paired with the sound recorders through a really quick QR scanner.

## Framework

The audio recording was done in Python using the Pi Audio module while the server was coded using Node JS,  
specifically the Express version. The app was coded in Android to meet the latest device standards (API 25).  

# SafeAlarm

## Description

Old school alarms and detectors have now become "smarter" using SafeAlarm, a smart-IOT-application.  


## How it works

Several audio recorders are placed within the proximity of the target device (i.e. Kitchen Alarm)  
and try to learn the noise around it given a test input. A maximum of squares of amplitude will be  
computed. Then, the recorders will actually listen for an alarm and compare its own calcualtions to  
the threshold. If the new calculation is greater than or equal to the threshold, the data that was  
collected from the alarm will be sent to the server, which will then relay it to your smart device  
via push notifications. Pairing your device with the sound recorder is simple and quick with a built-  
in QR scanner.

## Framework

The audio recording was done in Python using the Pi Audio module while the server was coded using Node JS,  
specifically the Express version. The app was coded in Android to meet the latest device standards (API 25).  

## Collaborators

* Vamshikrishnan Balakrishnan
* Ramaseshan Parthasarathy
* Harankumar Nallasivan
* Benjamin Yan

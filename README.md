# SafeAlarm

## Description

Old-school alarms and detectors have now become "smarter" using SafeAlarm, a smart-IOT-application.  

## Inspiration

Haran was asleep late in the morning. The carbon monoxide alarm went off, and his dog was panicking and crying out of fear because he didn't understand what was happening. It turned out that the alarm had just reached the end of its life cycle, but if he hadn't been there, it would have still caused his dog unreasonable levels of stress by ringing the entire day until his parents would have came back.


## How it works

It is an IoT audio recorder meant to be mounted close to a house alarm of any sort that itself isn't network-capable. Once it sounds, the recorder detects that the ambient sound has exceeded a threshold and sends a log message to the server. The mobile app allows the user to scan the device's QR code to pair with it. It was intended to ping the server on a timed basis and retrieve any new notifications to be immediately displayed to the user. So far, the audio recorder only runs as a python script on a laptop, and the timed notification feature has not been implemented yet.

## Framework

The audio recording was done in Python using the pyauudio module (Ben) while the server was coded using Node JS,  
specifically the Express version (Haran). The app was coded in Android to meet the latest stable device standards, which  
is currently API 25 (Ram).  

## Collaborators

* Vamshikrishnan Balakrishnan
* Ramaseshan Parthasarathy
* Harankumar Nallasivan
* Benjamin Yan

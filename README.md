# behavioralDetection android application

##Installing and running the client application :
1. Import the source code in Android Studio
2. Click Path -> 
   *. Build
   *. Build APK
3. APK will be generated under the system path (if default paths are used)
C:\Users\{User}\AndroidStudioProjects\WalkPattern\app\build\outputs\apk
4. Copy this apk file to an android device and install it

##What the application aims to acheive :
It tries to use a form of indentity verification to record and compare different users. It uses accelerometer and magnetometer to define the way a user walks/moves. This data is then used to mark a user.

[Link to server for this android app](https://github.com/siddharthmudgal/behavioralDetectionServer.git)

Technologies and frameworks used : 
1. Android
2. MEAN stack
  *. MongoDB
  *. Express
  *. Node
3. Handelbars
4. fusion charts


To run this application use an Android IDE to import this project. Play this project via AVD or export a apk file to your android device and run it.

Settings on UI : 

1. Seconds to record the data for (this is the duration of the latest x seconds of the data for the sensor)
2. Host Name : give the IP of the server 
3. username

How the application works : 

1. It records the last x seconds of the data stored under a username. 
2. User needs to record the sensor data three times
3. once the user records the data three times it is sent over to the server to be processed.

Sample Screen

![alt text](https://github.com/siddharthmudgal/behavioralDetection/blob/master/screen/screen.png "Sample screen")

# Emerald Termux
A Java application to be run inside Termux on an Android Smartphone. Uses Termux:API to read sensor data.

## Prerequisites
1. Have access to an Android Smartphone capable of running F-Droid and has an Accelerometer
2. A laptop or PC in the same TCP/IP network as your Smartphone (e.g. your local WiFi network) 
3. Install F-Droid from [f-droid.org](https://f-droid.org/) on your Smartphone
4. Install Termux from within F-Droid
5. Install Termux:API from within F-Droid
6. Clone this repository onto your laptop or PC
7. Clone [Emerald DL4J Data Recorder](https://github.com/emerald-iot-ai/emerald-dl4j-recorder) onto your laptop or PC
8. Make sure you've got Java SE version 21 or higher installed on your laptop or PC
9. Make sure you've got a current Maven installed on your laptop or PC
10. Make sure you aren't using TCP port 5000 for any other server on your laptop or PC
11. Follow the instructions to start and run Emerald DL4J Data Recorder given in its repository's README

## How to install Emerald Termux
1. Start the Termux:API app. This will enable access to Termux:API within Termux
2. Start the Termux app
3. Install Java 21 in Termux (run `pkg install openjdk-21`)
4. Install OpenSSH in Termux (run `pkg install openssh`)
5. Install tmux in Termux (run `pkg install tmux`)
6. Run `whoami` in Termux to find out your Termux user
7. Run `passwd` in Termux to set the password for your Termux user. Important: Remember this password well - you'll need it later
8. Run `sshd` in Termux. This will start the ssh server.
9. Run `ifconfig` in Termux to find out your Smartphone's IP address in your local WiFi
10. Open a terminal on your laptop or PC (e.g. cmd on Windows)
11. Go to the directory you cloned Emerald Termux into
12. Run `mvn clean package` to build the executable emerald-termux jar file
13. Use scp to copy the emerald-termux jar file to your Smartphone (On Windows you might want to use the Git Bash) (e.g. run `scp -P 8022 ./target/emerald-termux-0.0.1-SNAPSHOT.jar <your Termux user>@<IP address of your Smartphone>:.`)
14. Use scp to copy the emerald-termux-config.json file to your Smartphone (e.g. run `scp -P 8022 ./config/emerald-termux-config.json <your Termux user>@<IP address of your Smartphone>:.`)
15. Find out the local IP address of the machine Emerald DL4J Data Recorder is running on (e.g. run `ipconfig` in cmd on Windows)
16. Again in Termux: Run `nano ~/emerald-termux-config.json` and enter the IP address you just found out into the respective field

## How to run Emerald Termux
1. Run `tmux` in Termux to open a detachable shell
2. Make sure Emerald DL4J Data Recorder is actually running and running on the IP address and port configured
3. Run `java -jar emerald-termux-0.0.1-SNAPSHOT.jar` in tmux
4. Press Ctrl+b and then d in tmux to detach from it
5. Run `curl http://localhost:8080/emerald-termux/api/status` in Termux. You should get a JSON answer with details about the running components of Emerald Termux
6. Switch to the Emerald DL4J Data Recorder application on your laptop or PC
7. Perceive that the client has connected
8. Press a `Start recording` button and move your Smartphone according to the given label to start recording sensor data
9. After recording your sample press the `Stop recording` button
10. Follow the instructions in the README of [Emerald Termux CLI](https://github.com/emerald-iot-ai/emerald-termux-cli) on how to pause and resume sensor data sampling with Emerald Termux and shutting down the application when you're done

## Cleanup
After shutting down the Emerald Termux app, run `termux-sensor -s Accelerometer` and kill it with Ctrl+C to clear the sensor and be able to run the Emerald Termux app anew, if you so choose.

That's it!

**Happy recording! :-)**

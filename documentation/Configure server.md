# Configuring a RaspberryPi 5 to act as SignalingServer and TurnServer

> [!Info]
> *This can be also done in any laptop or server*
> Both servers will be allocated **in the same machine** for testing

## 1. Set up the TurnServer

- Open a terminal on your Raspberry and type the following:

```shell
cd /tmp
git clone https://github.com/Secure-Development-in-Kotlin/TurnServer.git
cd TurnServer
chmod +x setup_turn_server.sh && ./setup_turn_server.sh && rm -rf ./TurnServer && cd
```

## 2. Set up the SignalingServer

- [Install nodejs](https://nodejs.org/en/download/package-manager)
- Once installed, open a terminal on your Raspberry and type the following:

```shell
cd /tmp
git clone https://github.com/Secure-Development-in-Kotlin/SignalingServer.git
cd SignalingServer && npm install
chmod + x signaling_server_daemon.sh && ./signaling_server_daemon.sh
```

- Now you can run, stop or restart the signaling server as a daemon

### Run it

```shell
sudo systemctl start signalingserver
```

### Stop it

```shell
sudo systemctl stop signalingserver
```

### Restart it

```shell
sudo systemctl restart signalingserver
```

## 3. Configure Burner Chat to use those servers

- Now go to `BurnerChat/app/src/main/java/com/example/burnerchat/webrtc/WebRtcClient.kt` and change this section:
	- *Change `<YOUR_IP>` by yours*

![](img/Pasted%20image%2020241016094539.png)

- Then go to `BurnerChat/app/src/main/java/com/example/burnerchat/webrtc/socket/SocketClient.kt` and change this section:
	- *Change `<YOUR_IP>` by yours*

![](img/Pasted%20image%2020241016094754.png)

*Now you are ready to go!  🦜*
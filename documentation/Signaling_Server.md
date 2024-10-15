## Cómo alojar el servidor en una Raspberry Pi

1. Instalar un sistema en la Raspberry Pi
2. Instalar Node.js y npm en la Raspberry Pi

```shell
curl -sL https://deb.nodesource.com/setup_16.x | sudo -E bash -
sudo apt install -y nodejs
```

3. Configurar el signaling server:

```shell
mkdir signaling-server && cd signaling-server
npm init -y
npm install express socket.io
```

- Crearemos el fichero `server.js` con el siguiente contenido:

```js
const express = require('express');
const http = require('http');
const socketIO = require('socket.io');

// Inicializar el servidor de Express
const app = express();
const server = http.createServer(app);
const io = socketIO(server);

// Cuando un cliente se conecta
io.on('connection', socket => {
    console.log('Nuevo cliente conectado: ' + socket.id);

    // Manejo de ofertas WebRTC (SDP)
    socket.on('offer', (data) => {
        console.log('Oferta recibida de ' + socket.id);
        socket.broadcast.emit('offer', data);
    });

    // Manejo de respuestas WebRTC (SDP)
    socket.on('answer', (data) => {
        console.log('Respuesta recibida de ' + socket.id);
        socket.broadcast.emit('answer', data);
    });

    // Manejo de candidatos ICE
    socket.on('iceCandidate', (data) => {
        console.log('Candidato ICE recibido de ' + socket.id);
        socket.broadcast.emit('iceCandidate', data);
    });

    // Cuando un cliente se desconecta
    socket.on('disconnect', () => {
        console.log('Cliente desconectado: ' + socket.id);
    });
});

// Configurar el servidor para que escuche en un puerto
const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`Servidor de señalización en funcionamiento en el puerto ${PORT}`);
});
```

5. Ejecutamos el servidor:

```shell
node server.js
```

### ¿Cómo sabemos la ip de nuestra Raspberry (LOCAL)?

Usar:

```shell
hostname -I
```
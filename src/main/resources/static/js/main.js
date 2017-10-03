const PORT = 8443;
const MAPPING = "/room";
var ws;
var localStream;
var uuid;

/**
 * this initiate websocket connection
 * it is caled on page reload
 */
function init() {

	// get a local stream, show it in a self-view and add it to be sent
	navigator.mediaDevices.getUserMedia({video: true, audio: true}).then(function (stream) {
		console.log("Stream OK");
		localStream = stream;
		selfView.srcObject = localStream;
		ws = new WebSocket('wss://' + window.location.hostname + ':' + PORT + MAPPING);
		ws.onmessage = processWsMessage;
		ws.onopen = logMessage;
		ws.onclose = logMessage;
		ws.onerror = logMessage;
	}).catch(function (error) {
		console.log("Stream NOT OK: " + error.name + ': ' + error.message);
	});

}

function processWsMessage(message) {
	var signal = JSON.parse(message.data);

	// you have logged in
	switch (signal.type) {
		case 'login':
			logMessage(signal);
			uuid = signal.receiver;
			break;
		case 'init':
			logMessage(signal);
			break;
		case 'logout':
			logMessage(signal);
			break;
	}

}


function logMessage(message) {
	console.log(message);
}

function disconnect() {
	console.log('Disconnecting ');
	if(ws != null) {
		ws.close();
	}
}

// start
window.onload = init;
const PORT = 8443;
const MAPPING = "/room";
var ws;


/**
 * this initiate websocket connection
 * it is caled on page reload
 */
function init() {

	ws = new WebSocket('wss://' + window.location.hostname + ':' + PORT + MAPPING);
	ws.onmessage = processWsMessage;
	ws.onopen = logMessage;
	ws.onclose = logMessage;
	ws.onerror = logMessage;

}

function processWsMessage(message) {
	var signal = JSON.parse(message.data);

	// you have logged in
	switch (signal.type) {
		case 'login':
			logMessage(signal);
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
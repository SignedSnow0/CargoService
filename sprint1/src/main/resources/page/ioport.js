const socket = new WebSocket('ws://localhost:8080/');

const message = 'msg(loadRequest, request, pushbutton, cargoservice, RequestToLoad, 0)';
 
// Connection opened
socket.addEventListener('open', (event) => {
    console.log('Connected to the WebSocket server on port 8080!');
    
    const button = document.getElementById('request-button');
	button.addEventListener('click', () => {
        console.log('Request to load');
        
		socket.send(message);
	});
});

// Listen for messages from the server
socket.addEventListener('message', (event) => {
    console.log('Message from server:', event.data);
});

// Handle potential errors
socket.addEventListener('error', (error) => {
    console.error('WebSocket Error:', error);
});

// Handle connection closure
socket.addEventListener('close', (event) => {
    console.log('Connection closed:', event.reason);
});
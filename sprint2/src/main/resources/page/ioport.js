const socket = new WebSocket('ws://localhost:8080/');

const requestSpan = document.getElementById("request-result");
const statusSpan = document.getElementById("current-status")

// Connection opened
socket.addEventListener('open', (event) => {
    console.log('Connected to the WebSocket server on port 8080!');
    
    const button = document.getElementById('request-button');
	button.addEventListener('click', () => {
        console.log('Request to load');
		
		const request = `msg(loadRequest, request, pushbutton, cargoservice, loadRequest(RequestToLoad), 1)`;
		socket.send(request);
	});
});

// Listen for messages from the server
socket.addEventListener('message', (event) => {
    console.log('Message from server:', event.data);
	
	const response = JSON.parse(event.data);
	
	if (response.msgId === 'accepted') {
		const str = response.msgContent;
		const slotId = parseInt(str.slice(str.indexOf('(') + 1, str.indexOf(')')), 10)
		
		requestSpan.textContent = `request accepted at slot ${slotId}`;
	} else if (response.msgId === 'rejected') {
		requestSpan.textContent = `request rejected`;
	} else if (response.msgId === 'retryLater') {
		requestSpan.textContent = `retry later`;
	}
});

// Handle potential errors
socket.addEventListener('error', (error) => {
    console.error('WebSocket Error:', error);
});

// Handle connection closure
socket.addEventListener('close', (event) => {
    console.log('Connection closed:', event.reason);
});
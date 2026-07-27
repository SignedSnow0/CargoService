const socket = new WebSocket('ws://localhost:8080/')

const requestSpan = document.getElementById("request-result")
const statusSpan = document.getElementById("current-status")
const statusLed = document.getElementById("status-led")
const requestButton = document.getElementById("request-button")

// Connection opened
socket.addEventListener('open', (event) => {
    console.log('Connected to the WebSocket server on port 8080!')
    
    const button = document.getElementById('request-button')
	button.addEventListener('click', () => {
        console.log('Request to load')
		
		const request = `msg(loadRequest, request, pushbutton, cargoservice, loadRequest(RequestToLoad), 1)`
		socket.send(request)
	})
})

// Listen for messages from the server
socket.addEventListener('message', (event) => {
    console.log('Message from server:', event.data)
	
	const response = JSON.parse(event.data)
	
	if (response.msgId === 'accepted') {
		const str = response.msgContent
		const slotId = parseInt(str.slice(str.indexOf('(') + 1, str.indexOf(')')), 10)
		
		requestSpan.textContent = `Accepted at slot ${slotId}`
	} else if (response.msgId === 'rejected') {
		requestSpan.textContent = `Request rejected`;
	} else if (response.msgId === 'retryLater') {
		requestSpan.textContent = `Retry later`
	} else if (response.msgId == 'outOfServiceMsg') {
		statusSpan.textContent = `out of service`
		requestButton.disabled = true
		statusLed.className = "led out-of-service"
	} else if (response.msgId == 'serviceWorkingMsg') {
		statusSpan.textContent = `service working`
		requestButton.disabled = false
		statusLed.className = "led working"
	} else if (response.msgId == 'serviceBusyMsg') {
		statusSpan.textContent = `service busy`
		requestButton.disabled = true
		statusLed.className = "led busy"
	}
})

// Handle potential errors
socket.addEventListener('error', (error) => {
    console.error('WebSocket Error:', error)
})

// Handle connection closure
socket.addEventListener('close', (event) => {
    console.log('Connection closed:', event.reason)
})
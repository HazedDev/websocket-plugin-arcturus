# WebSocket Plugin #

## What is WebSocket Plugin? ##
Adds a WebSocket server to Arcturus Morningstar. Uses netty and accepts JSON messages.

## How do I connect to the WebSockets? ##
I have provided the following compatible websocket client-overlay: https://github.com/dank074/youtube-overlay

To use it simply include the scripts in your client, add a div with id=app, and call `startYTOverlay(sso, wsUrl)`

However, if you would like to create your own, make sure to implement the authentication method and the protocol.

### Authentication ###
Authentication is done using the user's SSO ticket. You must send the user's SSO ticket in the SSOTicketComposer message.

After a 30 second timeout without receiving the user's sso ticket, the connection will be closed.

For added security, the user must also be connected to the client for authentication to be successful. This means that the SSOTicketComposer must be sent *after* flash client authentication. I suggest listening for the "AuthOK" event using the Flash ExternalInterface.
### Protocol ###
The server and client will communicate using JSON messages. The messages will have the following structure:
```json
{
"header": "", 
"data": {}
}
```

The client should respond with a PongComposer when receiving a PingEvent message in order to keep the connection alive.
## How do I enable Secure WebSockets ? ##
You will need to add your SSL certificate as follows:
```
%your_morningstar_directory%/ssl/cert.pem
```
and your private key:
```
%your_mornigstar_directory%/ssl/privkey.pem
```

If your certificate is password protected, don't forget to add the password to your database under `emulator_settings`.`ws.cert.pass`

By default, if the above directory does not exist or any of the files above are missing, regular unsecured ws will be used.

## How do I add my own messages ? ##

### Incoming message ###
#### Step 1: Create a new Class for the message ####
Under `websockets->incoming` create a new class for your message. Your class must extend from the abstract super class `IncomingWebMessage`

You will also have to create a static class within this one that has the structure of the JSON message.
```java
public class ExampleEvent extends IncomingWebMessage<ExampleEvent.JSONExampleEvent> {

    public ExampleEvent() {
        super(JSONExampleEvent.class);
    }

    @Override
    public void handle(WebSocketClient client, JSONExampleEvent message) {
        //here write the code that gets executed when this message is received
        System.out.println("Received a string " + message.message + " and a number " + message.aNumber);
    }

    static class JSONExampleEvent {
        String message;
        int aNumber;
    }
}
```
The above code will expect the JSON structure `{"message":"", "aNumber":5}`, where 5 can by any number.

#### Step 2: Register the new message to the list of incoming messages ####
Inside the class `WebSocketManager`, add your message to the list of registered messages:
```java
public void initializeMessages() {
        this.registerMessage("ping", PingEvent.class);
        // add your own message with the header as the key
        this.registerMessage("example", ExampleEvent.class);
}
```

#### Step 3: Send the message to the Server ###
So the plugin is ready to listen for the message. The Server is expecting a message with the following JSON format:
```json
{
  "header": "example", 
  "data": {
    "message": "",
    "aNumber": 5
  }
}
```

Now all you gotta do is configure your websocket client to send the above message and you're good to go.

### Outgoing message ###

#### Step 1: Create a new Class for your message ####
Under `websockets->outgoing` create a new class for your outgoing message. This class should extend from the superclass `OutgoingWebMessage`.
```java
public class ExampleComposer extends OutgoingWebMessage {
    public ExampleComposer(String name, int age, boolean isHuman) {
        super("example"); // this is the message header
        this.data.add("name", new JsonPrimitive(name));
        this.data.add("age", new JsonPrimitive(age));
        this.data.add("idk", new JsonPrimitive(isHuman));
    }
}
```
The above message will produce the following JSON structure:
```json
{
  "header": "example", 
  "data": {
    "name": "",
    "age": 5,
    "idk": false
  }
}
```
#### Step 2: Send the message to the websocket client ####
Now it's as simple as one line of code to send the above message to a all the connected websocket clients:
```java
WebSocketManager.getInstance().getClientManager().broadcastMessage(new ExampleComposer("Jacob", 5, false));
```

## Implemented features: ##
- [x] Youtube TV
- [x] Keyboard movement
- [ ] HTML commands pop-up

## License ##
WebSocket Plugin is released under the MIT LICENSE.

## Credits ##
skeletor

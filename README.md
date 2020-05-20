# netty-example

Implement a simple echo-server based on chapter 2 of Netty in Action with "clojure".


## server

The server folder contains a simple tcp server implementation.

## client

The client folder contains a simple tcp client implementation.

## Usage

In the repos root folder a terminal. Start the server:
```
./server $ clj -m netty.core
```

Open another terminal start the client:
```
./client $ clj -m netty.core
```

The server prints something like:
```
Server received: 12 Bytes
read complete
```

The client prints:
```
added
registered
active
read
received:  Netty rocks!
readComplete
readComplete
inactive
unregistered
removed
```
and exits.
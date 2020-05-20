(ns netty.core
  (:import [io.netty.bootstrap Bootstrap]
           [io.netty.buffer ByteBuf Unpooled]
           [io.netty.channel ChannelInboundHandler ChannelInitializer]
           [io.netty.channel.nio NioEventLoopGroup]
           [io.netty.channel.socket.nio NioSocketChannel]
           [io.netty.util CharsetUtil]
           [java.net InetSocketAddress]))

(defn channel-handler []
  (reify ChannelInboundHandler
    ; the channel of the ctx has been registered with its eventloop
    (channelRegistered [this ctx]
      (println "registered")
      (.fireChannelRegistered ctx))
    ; the channel of the ctx has been unregistered from its eventloop
    (channelUnregistered [this ctx]
      (println "unregistered")
      (.fireChannelUnregistered ctx))
    ; the channel of the ctx is now active
    (channelActive [this ctx]
      (println "active")
      (.writeAndFlush
       ctx
       (Unpooled/copiedBuffer "Netty rocks!" CharsetUtil/UTF_8))
      (.fireChannelActive ctx))
    ; the channel of the ctx is now inactive
    (channelInactive [this ctx]
      (println "inactive")
      (.fireChannelInactive ctx))
    ; invoked when the channel has read a message
    (channelRead [this ctx msg]
      (println "read")
      ;(.fireChannelRead ctx)
      (let [buf (cast ByteBuf msg)]
        (println "received: " (.toString
                               buf
                               CharsetUtil/UTF_8))))
    ; invoked when the last message from a CURRENT read operation
    ; has been consumed from channelRead
    (channelReadComplete [this ctx]
      (println "readComplete")
      ;(.fireChannelReadComplete ctx)
      )
    ; an user event was triggered
    (userEventTriggered [this ctx event]
      (println "event triggered")
      (.fireUserEventTriggered ctx event))
    ; called once the writable state of a channel changed
    (channelWritabilityChanged [this ctx]
      (println "writable changed")
      (.fireChannelWritabilityChanged ctx))
    ; called if throwable was thrown
    (exceptionCaught [this ctx cause]
      (println "exception")
      (.printStackTrace cause)
      (.close ctx))
    
    ; inherited from ChannelHandler

    ; called after the handler was added to the ctx
    (handlerAdded [this ctx]
      (println "added"))
    ; called after the handler was removed from the ctx
    (handlerRemoved [this ctx]
      (println "removed"))))

(defn initializer []
  (proxy [ChannelInitializer] []
    (initChannel [ch]
      (.addLast
       (.pipeline ch)
       (channel-handler)))))

(defn connect [host port]
  (let [group (NioEventLoopGroup.)]
    (try
      (let [b (Bootstrap.)]
        (.handler (.remoteAddress (.channel (.group b
                                                    group)
                                            NioSocketChannel)
                                  (InetSocketAddress. host port))
                  (initializer))
        (.sync
         (.closeFuture
          (.channel
           (.sync
            (.connect b))))))
      (finally
        (.sync
         (.shutdownGracefully group))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (connect "localhost" 8080))


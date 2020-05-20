(ns netty.core
  (:import [io.netty.bootstrap ServerBootstrap]
           [io.netty.buffer ByteBuf Unpooled]
           [io.netty.channel ChannelFutureListener
            ChannelInboundHandlerAdapter
            ChannelInitializer]
           [io.netty.channel.nio NioEventLoopGroup]
           [io.netty.channel.socket.nio NioServerSocketChannel]
           [io.netty.util CharsetUtil]
           [java.net InetSocketAddress]))

(defn make-handler []
  (proxy [ChannelInboundHandlerAdapter] []
    (channelRead [ctx msg]
      (let [in (cast ByteBuf msg)]
        (println "Server received:" (.readableBytes in) "Bytes")
        ;(println "Server received:" (.toString in CharsetUtil/UTF_8))
        (.write ctx in)))
    (channelReadComplete [ctx]
      (println "read complete")
      (.addListener
       (.writeAndFlush ctx (Unpooled/EMPTY_BUFFER))
       ChannelFutureListener/CLOSE))
    (exceptionCaught [ctx cause]
      (.printStackTrace ctx)
      (.close ctx))))

(defn initializer []
  (proxy [ChannelInitializer] []
    (initChannel [ch]
      (.addLast
       (.pipeline ch)
       (make-handler)))))

(defn serve [port]
  (let [group   (NioEventLoopGroup.)]
    (try
      (let [b (ServerBootstrap.)]
        (.childHandler
         (.localAddress
          (.channel
           (.group
            b
            group)
           NioServerSocketChannel)
          (InetSocketAddress. port))
         (initializer))

        (.sync
         (.closeFuture
          (.channel (.sync (.bind b))))))
      (finally 
        (.sync
         (.shutdownGracefully group))))))

(defn -main [& args]
  (let [port 8080]
    (serve 8080)))

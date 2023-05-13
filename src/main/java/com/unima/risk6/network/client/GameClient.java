package com.unima.risk6.network.client;

import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.network.message.Message;
import com.unima.risk6.network.serialization.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameClient implements Runnable {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameClient.class);

  private final String url;

  private volatile Channel ch;

  public GameClient(String url) {
    this.url = System.getProperty("url", url);
  }

  public void sendMessage(Message message) {
    String json = Serializer.serialize(message);
    WebSocketFrame frame = new TextWebSocketFrame(json);
    ch.writeAndFlush(frame);
    LOGGER.debug("Sent Message: " + json);
  }

  public void leaveGame() {
    ch.writeAndFlush(new CloseWebSocketFrame());
    ch.close();
    LobbyConfiguration.stopGameClient();
  }

  public void run() {
    try {
      URI uri = new URI(url);
      final int port = 42069;

      EventLoopGroup group = new NioEventLoopGroup();
      try {
        final GameClientHandler handler = new GameClientHandler(
            WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true,
                new DefaultHttpHeaders()));

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192),
                    WebSocketClientCompressionHandler.INSTANCE, handler);
              }
            });

        ch = b.connect(uri.getHost(), port).sync().channel();
        handler.handshakeFuture().sync();

        while (true) {
          Thread.sleep(10000);
        }
      } finally {
        group.shutdownGracefully();
      }
    } catch (Exception e) {
      //TODO Logger
      System.out.println(e);
    }
  }

  public Channel getCh() {
    return ch;
  }
}
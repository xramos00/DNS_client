package models;
/*
 * Author - Patricia Ramosova
 * Link - https://github.com/xramos00/DNS_client
 * */
import exceptions.TimeoutException;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import tasks.DNSOverTLS;
import tasks.DNSTaskBase;

public class DoTClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private String resolverIP;
    private DNSTaskBase dnsTaskBase;

    public DoTClientInitializer(SslContext sslCtx, String resolverIP, DNSTaskBase dnsTaskBase) {
        this.sslCtx = sslCtx;
        this.resolverIP = resolverIP;
        this.dnsTaskBase = dnsTaskBase;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        ReadTimeoutHandler readTimeoutHandler = new ReadTimeoutHandler(2);
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler(){
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                if (cause instanceof ReadTimeoutException){
                    cause.printStackTrace();
                    ctx.close();
                    ((DNSOverTLS)dnsTaskBase).setExc(new TimeoutException());
                    ((DNSOverTLS)dnsTaskBase).setNotFinished(false);
                } else {
                    super.exceptionCaught(ctx, cause);
                }
            }
        };
        SslHandler sslHandler = sslCtx.newHandler(ch.alloc(), resolverIP, 853);
        sslHandler.setCloseNotifyFlushTimeoutMillis(2000);
        sslHandler.setHandshakeTimeoutMillis(2000);
        sslHandler.setCloseNotifyReadTimeoutMillis(2000);
        pipeline.addLast(readTimeoutHandler);
        pipeline.addLast(channelDuplexHandler);
        pipeline.addLast(sslHandler);

        // and then business logic.
        pipeline.addLast(new DoTClientHandler(dnsTaskBase));
    }
}

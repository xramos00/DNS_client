package tasks;
/*
 * Author - Patricia Ramosova
 * Link - https://github.com/xramos00/DNS_client
 * */
import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import exceptions.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import models.DoTClientInitializer;
import org.json.simple.parser.ParseException;
import tasks.runnables.RequestResultsUpdateRunnable;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Class representing protocol DNS over TLS
 */
@Getter
@Setter
public class DNSOverTLS extends DNSTaskBase{

    private SslContext sslCtx;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private Exception exc = null;

    private boolean notFinished = true;
    public DNSOverTLS(boolean recursion, boolean adFlag, boolean cdFlag, boolean doFlag, String domain, Q_COUNT[] types, TRANSPORT_PROTOCOL transport_protocol, APPLICATION_PROTOCOL application_protocol, String resolverIP, NetworkInterface netInterface) throws UnsupportedEncodingException, NotValidIPException, NotValidDomainNameException, UnknownHostException {
        super(recursion, adFlag, cdFlag, doFlag, domain, types, transport_protocol, application_protocol, resolverIP, netInterface, null);
    }

    @Override
    protected void sendData() throws TimeoutException, SSLException, InterruptedException {
        try {
            setStartTime(System.nanoTime());
            OpenSsl.ensureAvailability();

            sslCtx = SslContextBuilder.forClient()
                    .protocols("TLSv1.3")
                    .ciphers(Arrays.asList(
                            "TLS_AES_256_GCM_SHA384",
                            "TLS_AES_128_GCM_SHA256",
                            "TLS_CHACHA20_POLY1305_SHA256",
                            "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                            "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"))
                    .sslProvider(SslProvider.OPENSSL)
                    .sessionTimeout(2000)
                    .build();

            group = new NioEventLoopGroup();

            bootstrap = new Bootstrap()
                    .group(group)
                    .channelFactory(() -> {
                        if (Epoll.isAvailable()) {
                            return new EpollSocketChannel();
                        } else {
                            return new NioSocketChannel();
                        }
                    })
                    .handler(new DoTClientInitializer(sslCtx, resolver, this));

            channel = bootstrap.connect(resolver, 853).sync().channel();
            channel.config().setConnectTimeoutMillis(2000);

        /*System.out.println("--------------------------REQUEST--------------------------");
        System.out.println("Sending over TLS");
        System.out.println("-----------------------------------------------------------");*/
            channel.writeAndFlush(Unpooled.wrappedBuffer(getMessageAsBytes())).sync();
            while (notFinished) {
                System.out.print("\r");
            }
            if (exc != null){
                throw new TimeoutException();
            }
            setWasSend(true);
            setStopTime(System.nanoTime());
            setDuration(calculateDuration());
            setMessagesSent(1);
            if (!massTesting) {
                Platform.runLater(() -> controller.getSendButton().setText(controller.getButtonText()));
            }
        } catch (SSLException | InterruptedException | TimeoutException e) {
            Platform.runLater(()->{
                controller.getSendButton().setText(controller.getButtonText());
                progressBar.setProgress(0);
            });
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void updateProgressUI() {

    }

    @Override
    protected void updateResultUI() {
        Platform.runLater(new RequestResultsUpdateRunnable(this));
    }

    @Override
    public void stopExecution() {
        ChannelFuture future = channel.close();
        try {
            future.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

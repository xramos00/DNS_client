package models;
/*
 * Author - Patricia Ramosova
 * Link - https://github.com/xramos00/DNS_client
 * */

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tasks.DNSOverTLS;
import tasks.DNSTaskBase;

public class DoTClientHandler extends SimpleChannelInboundHandler<Object> {

    DNSTaskBase dnsTaskBase;

    public DoTClientHandler(DNSTaskBase dnsTaskBase){
        this.dnsTaskBase = dnsTaskBase;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close().sync();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        ByteBuf dataLengthBuf = byteBuf.readBytes(2);
        byte[] dataLengthByte = new byte[dataLengthBuf.readableBytes()];
        dataLengthBuf.readBytes(dataLengthByte);
        int length = ((dataLengthByte[0] & 0xFF) << 8) + (dataLengthByte[1] & 0xFF);
        ByteBuf dnsBuf = byteBuf.readBytes(length);
        byte[] pck = new byte[dnsBuf.readableBytes()];
        dnsBuf.readBytes(pck);
        dnsTaskBase.setReceiveReply(pck);
        System.out.println(pck.length);
        /*System.out.println("--------------------------RESPONSE--------------------------");
        System.out.println(new Message(pck));
        System.out.println("------------------------------------------------------------");*/
        ((DNSOverTLS)dnsTaskBase).setNotFinished(false);
    }
}

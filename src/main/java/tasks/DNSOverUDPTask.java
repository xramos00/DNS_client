package tasks;

import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import exceptions.*;
import javafx.application.Platform;
import models.Ip;
import tasks.runnables.ProgressUpdateRunnable;
import tasks.runnables.RequestResultsUpdateRunnable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

public class DNSOverUDPTask extends DNSTaskBase{

    public DNSOverUDPTask(boolean recursion, boolean dnssec, boolean rrRecords, String domain, Q_COUNT[] types, TRANSPORT_PROTOCOL transport_protocol, APPLICATION_PROTOCOL application_protocol, String resolverIP, NetworkInterface netInterface, boolean caFlag) throws UnsupportedEncodingException, NotValidIPException, NotValidDomainNameException, UnknownHostException {
        super(recursion, dnssec, caFlag, rrRecords, domain, types, transport_protocol, application_protocol, resolverIP, netInterface);
    }

    @Override
    protected void sendData() throws TimeoutException, MessageTooBigForUDPException, InterfaceDoesNotHaveIPAddressException {
        if (getSize() > MAX_UDP_SIZE)
            throw new MessageTooBigForUDPException();
        setMessagesSent(0);
        DatagramSocket datagramSocket;
        try {
            datagramSocket = new DatagramSocket(0, Ip.getIpAddressFromInterface(getInterfaceToSend(), getResolver()));
        } catch (Exception e) {
            throw new InterfaceDoesNotHaveIPAddressException();
        }
        boolean run = true;
        boolean exception = false;
        boolean timeout = false;
        while (run) {
            try {
                if (getMessagesSent() == DNSTaskBase.MAX_MESSAGES_SENT) {
                    throw new TimeoutException();
                }

                DatagramPacket responsePacket = new DatagramPacket(getRecieveReply(), getRecieveReply().length);
                DatagramPacket datagramPacket = new DatagramPacket(getMessageAsBytes(), getMessageAsBytes().length, getIp(), DNSTaskBase.DNS_PORT);
                // DatagramPacket datagramPacket = new Data
                datagramSocket.setSoTimeout(DNSTaskBase.TIME_OUT_MILLIS);
                setStartTime(System.nanoTime());

                datagramSocket.send(datagramPacket);
                setWasSend(true);
                datagramSocket.receive(responsePacket);

                setStopTime(System.nanoTime());
                datagramSocket.close();
                run = false;
            } catch (SocketTimeoutException | SocketException e) {
                LOGGER.warning("Time out for the: " + (getMessagesSent() + 1) + " message");
                timeout = true;
                if (getMessagesSent() == MAX_MESSAGES_SENT) {
                    datagramSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            setMessagesSent(getMessagesSent()+1);
            if (timeout)
            {
                timeout = false;
                setDuration(9999);
            }
            else
            {
                setDuration(calculateDuration());
            }
            updateProgressUI();
        }

        if (exception) {
            throw new TimeoutException();
        }
    }

    @Override
    protected void updateProgressUI()
    {
        Platform.runLater(new ProgressUpdateRunnable(this));
    }

    @Override
    protected void updateResultUI()
    {
        Platform.runLater(new RequestResultsUpdateRunnable(this));
    }


}

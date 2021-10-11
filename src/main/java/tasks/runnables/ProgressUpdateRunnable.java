package tasks.runnables;

import tasks.DNSTaskBase;

public class ProgressUpdateRunnable implements Runnable
{
    DNSTaskBase dnsUPDTask = null;

    public ProgressUpdateRunnable(DNSTaskBase dnsUDPTask)
    {
        this.dnsUPDTask = dnsUDPTask;
    }

    @Override
    public void run()
    {
        dnsUPDTask.setMessagesSentProperty(dnsUPDTask.getMessagesSent());
        dnsUPDTask.setRequestProperty(dnsUPDTask.getRequest());
        dnsUPDTask.setResponseProperty(dnsUPDTask.getResponse());
        dnsUPDTask.setDurationProperty(dnsUPDTask.getDuration());
        dnsUPDTask.getProgressBar().setProgress((float)dnsUPDTask.getMessagesSent()/(float)DNSTaskBase.MAX_MESSAGES_SENT);
        dnsUPDTask.setResponseSizeProperty(dnsUPDTask.getByteSizeResponse());
        dnsUPDTask.setQuerySizeProperty(dnsUPDTask.getByteSizeQuery());
        //dnsUPDTask.getProgressBar().setProgress(0.5F);
    }
}

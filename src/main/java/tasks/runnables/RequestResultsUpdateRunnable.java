package tasks.runnables;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import tasks.DNSOverTCPTask;
import tasks.DNSTaskBase;

import java.util.logging.Logger;

public class RequestResultsUpdateRunnable implements Runnable {

    DNSTaskBase dnsOverTCPTask = null;
    protected Logger LOGGER;

    public RequestResultsUpdateRunnable(DNSTaskBase dnsOverTCPTask)
    {
        this.dnsOverTCPTask = dnsOverTCPTask;
    }

    protected void expandAll(TreeItem<String> t) {
        try {
            if(t.getChildren().size() == 0)
            {
                t.setExpanded(true);
                return;
            }
            for(TreeItem<String> item: t.getChildren())
            {
                expandAll(item);
                item.setExpanded(true);
            }
        } catch (Exception e) {
            LOGGER.warning(e.toString());
        }
    }

    @Override
    public void run() {
        // update
        dnsOverTCPTask.setMessagesSentProperty(dnsOverTCPTask.getMessagesSent());
        dnsOverTCPTask.setRequestProperty(dnsOverTCPTask.getRequest());
        dnsOverTCPTask.setResponseProperty(dnsOverTCPTask.getResponse());
        dnsOverTCPTask.setDurationProperty(dnsOverTCPTask.getDuration());
        expandAll(dnsOverTCPTask.getRequestProperty());
        expandAll(dnsOverTCPTask.getResponseProperty());
        dnsOverTCPTask.getProgressBar().setProgress(1.0);
        dnsOverTCPTask.setQuerySizeProperty(dnsOverTCPTask.getByteSizeQuery());
        dnsOverTCPTask.setResponseSizeProperty(dnsOverTCPTask.getByteSizeResponse());
    }
}

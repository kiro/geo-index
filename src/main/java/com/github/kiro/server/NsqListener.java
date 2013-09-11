package com.github.kiro.server;

import com.github.kiro.Point;
import com.github.kiro.PointsIndex;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ly.bit.nsq.Message;
import ly.bit.nsq.exceptions.NSQException;
import ly.bit.nsq.lookupd.BasicLookupd;
import ly.bit.nsq.syncresponse.SyncResponseHandler;
import ly.bit.nsq.syncresponse.SyncResponseReader;

import static com.github.kiro.Point.point;

/**
 * NsqListener
 */
public class NsqListener {
    private static final String NSQ_TOPIC = "jstats.allingested";
    private static final String CHANNEL = "kiro-experiment";
    private final PointsIndex pointsIndex;
    private final String nsqAddress;
    private final int nsqPort;

    public NsqListener(PointsIndex pointsIndex, String nsqAddress, int nsqPort) {
        this.pointsIndex = pointsIndex;
        this.nsqAddress = nsqAddress;
        this.nsqPort = nsqPort;
    }

    public void listen() throws Exception {
        SyncResponseHandler handler = new SyncResponseHandler() {
            @Override
            public boolean handleMessage(Message msg) throws NSQException {
                String message = new String(msg.getBody());
                JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();

                if (jsonObject.get("eventType").getAsString().equals("point")) {
                    String driverId = jsonObject.get("driverId").getAsString();
                    double lat = Double.parseDouble(jsonObject.get("latitude").getAsString());
                    double lon = Double.parseDouble(jsonObject.get("longitude").getAsString());

                    Point point = point(driverId, lat, lon);
                    pointsIndex.update(point);
                }

                return true;
            }
        };

        SyncResponseReader reader = new SyncResponseReader(NSQ_TOPIC, CHANNEL + "#ephemeral", handler);
        reader.connectToNsqd(nsqAddress, nsqPort);
        //reader.addLookupd(new BasicLookupd(nsqAddress + ":" + nsqPort));
    }
}

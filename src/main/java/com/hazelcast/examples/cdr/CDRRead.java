package com.hazelcast.examples.cdr;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hazelcast.jet.pipeline.JournalInitialPosition.START_FROM_CURRENT;
import static java.util.concurrent.TimeUnit.MINUTES;

public class CDRRead implements Serializable {
    public static void main(String[] args) {
        Pipeline pipe = Pipeline.create();
        //read from map cdr journal
        pipe.readFrom(Sources.<String, CDR>mapJournal("cdr", START_FROM_CURRENT))
                .withIngestionTimestamps()
                .groupingKey(entry -> entry.getValue().getCallingNumber() + entry.getValue().getCalledNumber())
                .mapStateful(MINUTES.toMillis(5),
                        () -> new ArrayList<CDR>(),
                        (busyCDRs, id, entry) -> {
                            if (entry.getValue().getStatus().equals("BUSY")) {
                                busyCDRs.add(entry.getValue());
                                return null;
                            } else {
                                CDR finalCDR = new CDR();
                                copy(finalCDR, entry);
                                var route = busyCDRs.stream().map(busyCDR -> busyCDR.getDestinationNumber()).collect(Collectors.joining(","));
                                finalCDR.setRoute(route);
                                busyCDRs.clear();
                                return  finalCDR;
                            }
                        },
                        (busyCDRs, id, time) -> {
                            CDR ret = new CDR();
                            ret.setID("-1");
                            busyCDRs.clear();
                            return  ret;
                        }
                )
                .writeTo(Sinks.map("cdr-final",cdr -> cdr.getID(), cdr -> cdr));
//                .writeTo(Sinks.logger());
        JobConfig cfg = new JobConfig().setName("cdr-read");
        HazelcastInstance hz = Hazelcast.bootstrappedInstance();
//        HazelcastInstance hz = HazelcastClient.newHazelcastClient();
        hz.getJet().newJob(pipe, cfg);

    }

    private static void copy(CDR finalCDR, Map.Entry<String, CDR> entry) {
        finalCDR.setID(entry.getValue().getID());
        finalCDR.setCallingNumber(entry.getValue().getCallingNumber());
        finalCDR.setCalledNumber(entry.getValue().getCalledNumber());
        finalCDR.setDestinationNumber(entry.getValue().getDestinationNumber());
        finalCDR.addToRoute(entry.getValue().getDestinationNumber());
        finalCDR.setStatus(entry.getValue().getStatus());
        finalCDR.setTime(entry.getValue().getTime());
    }
}

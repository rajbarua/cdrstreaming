package com.hazelcast.examples.cdr;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.pipeline.Pipeline;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CDRWrite {
    public static void main(String[] args) {
        //create a pipeline
        Pipeline pipe = Pipeline.create();
        //create a cdr object and write to cdr map
        CDR cdr1 = new CDR("1","447863499470", "449301857280", "447205189201", "2020-10-10 10:10:00", "BUSY");
        CDR cdr2 = new CDR("2", "447863499470", "449301857280", "447205189202", "2020-10-10 10:15:00", "BUSY");
        CDR cdr3 = new CDR("3", "447863499470", "449301857280", "447205189203", "2020-10-10 10:20:00", "BUSY");
        CDR cdr4 = new CDR("4", "447863499470", "449301857280", "447205189204", "2020-10-10 10:25:00", "ANSWERED");
        //get the cdr map and push data into it
        HazelcastInstance hz = HazelcastClient.newHazelcastClient();
        pushCDR(cdr1, hz);
//        pushCDR(cdr2, hz);
//        pushCDR(cdr3, hz);
//        pushCDR(cdr4, hz);
        System.out.println("CDR written to map");
        hz.shutdown();
    }

    private static void pushCDR(CDR cdr, HazelcastInstance hz) {
        hz.getMap("cdr").put(cdr.getID(), cdr);
        //make thread sleep for 5 seconds
        try {
            Thread.sleep(SECONDS.toMillis(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
/*
./gradlew build
hz-cli submit --class com.hazelcast.examples.cdr.CDRRead  build/libs/hz-k8s-1.0-SNAPSHOT.jar

CREATE OR REPLACE MAPPING "cdr" (
  __key VARCHAR,
  "ID" VARCHAR EXTERNAL NAME "this.ID",
  "calledNumber" VARCHAR EXTERNAL NAME "this.calledNumber",
  "callingNumber" VARCHAR EXTERNAL NAME "this.callingNumber",
  "destinationNumber" VARCHAR EXTERNAL NAME "this.destinationNumber",
  "route" VARCHAR EXTERNAL NAME "this.route",
  "status" VARCHAR EXTERNAL NAME "this.status",
  "time" VARCHAR EXTERNAL NAME "this.time" )
TYPE IMap OPTIONS ( 'keyFormat' = 'java',
  'keyJavaClass' = 'java.lang.String',
  'valueFormat' = 'compact',
  'valueCompactTypeName' = 'com.hazelcast.examples.cdr.CDR' );

CREATE OR REPLACE MAPPING "cdr-final" (
  __key VARCHAR,
  "ID" VARCHAR EXTERNAL NAME "this.ID",
  "calledNumber" VARCHAR EXTERNAL NAME "this.calledNumber",
  "callingNumber" VARCHAR EXTERNAL NAME "this.callingNumber",
  "destinationNumber" VARCHAR EXTERNAL NAME "this.destinationNumber",
  "route" VARCHAR EXTERNAL NAME "this.route",
  "status" VARCHAR EXTERNAL NAME "this.status",
  "time" VARCHAR EXTERNAL NAME "this.time" )
TYPE IMap OPTIONS ( 'keyFormat' = 'java',
  'keyJavaClass' = 'java.lang.String',
  'valueFormat' = 'compact',
  'valueCompactTypeName' = 'com.hazelcast.examples.cdr.CDR' );

sink into cdr  values ('1', '1','447863499470', '449301857280', '447205189201', '', 'BUSY', '2020-10-10 10:10:00');
sink into cdr values ('2', '2','447863499470', '449301857280', '447205189202', '', 'BUSY', '2020-10-10 10:15:00');
sink into cdr values ('3', '3','447863499470', '449301857280', '447205189203', '', 'BUSY', '2020-10-10 10:20:00');
sink into cdr values ('4', '4','447863499470', '449301857280', '447205189204', '', 'ANSWERED', '2020-10-10 10:25:00');
 */

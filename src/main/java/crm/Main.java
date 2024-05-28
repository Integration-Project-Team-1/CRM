package crm;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

import static crm.Business.*;

public class Main {
    public static void main(String[] args) throws Exception {

        try {
            Heartbeat heartbeat = new Heartbeat();
            // The HeartbeatTask will start running automatically
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Create an ExecutorService to manage concurrent execution of the Consumer task
       ExecutorService executor = Executors.newCachedThreadPool();

        // Start the Consumer task
        executor.execute(() -> {
            try {
               Consumer consumer = new Consumer();
              consumer.startConsuming();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Salesforce salesforce = new Salesforce();
         //Thread for continuously checking CRUD for users and businesses in Salesforce
        executor.execute(() -> {

           salesforce.continuouslyCheckForNewUsers("Deelnemer__c");
        });

        executor.execute(()->{
            salesforce.continuouslyCheckForNewUpdatedUsers("gewijzigde_Deelnemer__c");
       });

        executor.execute(()->{
           salesforce.continuouslyCheckForDeletedUsers("delete_deelnemer__c");
       });

        executor.execute(()->{
            continuouslyCheckForNewBusinesses("Business__c");
        });

        executor.execute(()->{
            continuouslyCheckForNewUpdatedBusinesses("gewijzigde_Business__c");
        });

        executor.execute(()->{
            continuouslyCheckForDeletedBusinesses("delete_Business__c");
        });



        // Shutdown the executor when no longer needed
        executor.shutdown();
    }
    }




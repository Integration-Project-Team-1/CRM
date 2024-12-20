package crm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.api.ApiConfig;
import com.force.api.ForceApi;
import com.force.api.QueryResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static crm.Business.*;
import static crm.Rabbitmq.sendLog;
import static crm.Rabbitmq.sendToExchange;
import static crm.XML.jsonBusinessToXml;
import static crm.XML.jsonDeelnemerToXml;
import static crm.xmlValidation.validateXML;

public class Salesforce {

    private static List<String> createdDeelnemersUuidList = new ArrayList<>();
    private static List<JSONObject> updatedJsonDeelnemersList = new ArrayList<>();
    private static List<String> deletedUsersUuidList = new ArrayList<>();
    private String MASTERUUID_URL = System.getenv("MASTERUUID_URL");

    public static ForceApi api = connectToSalesforce();

    public static ForceApi connectToSalesforce() {
        String SALESFORCE_USERNAME = System.getenv("SALESFORCE_USERNAME");
        String SALESFORCE_PASSWORD = System.getenv("SALESFORCE_PASSWORD");
        String SALESFORCE_SECURITY_TOKEN = System.getenv("SALESFORCE_SECURITY_TOKEN");
        String LOGIN_URL = System.getenv("LOGIN_URL");
        String CONSUMER_KEY = System.getenv("CONSUMER_KEY");
        String CONSUMER_SECRET = System.getenv("CONSUMER_SECRET");

        // Combineer wachtwoord en beveiligingstoken
        String loginPassword = SALESFORCE_PASSWORD + SALESFORCE_SECURITY_TOKEN;

        // Configureer de API-configuratie
        ApiConfig config = new ApiConfig()
                .setClientId(CONSUMER_KEY)
                .setClientSecret(CONSUMER_SECRET)
                .setUsername(SALESFORCE_USERNAME)
                .setPassword(loginPassword)
                .setLoginEndpoint(LOGIN_URL);

        ForceApi api = new ForceApi(config);
        return api;

    }

    public void createDeelnemer(Participant participant) {

        Map<String, Object> deelnemerFields = new HashMap<>();
        deelnemerFields.put("Name", participant.getFirstname());
        deelnemerFields.put("familie_naam__c", participant.getLastname());
        deelnemerFields.put("Phone__c", participant.getPhone());
        deelnemerFields.put("Email__c", participant.getEmail());
        deelnemerFields.put("Bedrijf__c", participant.getBusiness());
        deelnemerFields.put("date_of_birth__c", participant.getDateOfBirth());
        deelnemerFields.put("Deelnemer_uuid__c", participant.getUuid());

        System.out.println(participant.getDateOfBirth());

        try{

            createdDeelnemersUuidList.add(participant.getUuid());

            // Maak de Deelnemer aan in Salesforce
            api.createSObject("Deelnemer__c", deelnemerFields);

        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }


    }

    public void createBusiness(Business business) {
        Map<String, Object> businessFields = new HashMap<>();
        businessFields.put("Name", business.getName());
        businessFields.put("VAT__c", business.getVat());
        businessFields.put("Email__c", business.getEmail());
        businessFields.put("Access_Code__c", business.getAccessCode());
        businessFields.put("Address__c", business.getAddress());
        businessFields.put("Bedrijf_uuid__c", business.getUuid());

        try {
            createdBusinessesUuidList.add(business.getUuid());
            // Maak het Business object aan in Salesforce
            api.createSObject("Business__c", businessFields);
        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }


    }

    public void createConsumption(Consumption consumption) {
        Map<String, Object> consumptionFields = new HashMap<>();
        consumptionFields.put("Timestamp__c", new Date());
        consumptionFields.put("Name", "food");
        consumptionFields.put("Products__c", consumption.getProducts());
        consumptionFields.put("Consumer_uuid__c", consumption.getUuid());

        // Maak het Consumption object aan in Salesforce
        api.createSObject("Consumption__c", consumptionFields);
    }

    public void updateDeelnemer(String uuid, Participant updatedParticipant) {

        // Retrieve the Deelnemer record JSON string based on UUID
        String deelnemerJson = getDeelnemer(uuid);

        if (deelnemerJson != null) {
            try {
                // Convert JSON string to Map
                ObjectMapper objectMapper = new ObjectMapper();

                JSONObject deelnemerJsonObject = new JSONObject(deelnemerJson);
                Map<String, Object> deelnemerRecord = objectMapper.readValue(deelnemerJson, new TypeReference<Map<String, Object>>() {
                });

                // Prepare fields to update
                Map<String, Object> updatedFields = new HashMap<>();
                updatedFields.put("Name", updatedParticipant.getFirstname());
                updatedFields.put("familie_naam__c", updatedParticipant.getLastname());
                updatedFields.put("Phone__c", updatedParticipant.getPhone());
                updatedFields.put("Email__c", updatedParticipant.getEmail());
                updatedFields.put("Bedrijf__c", updatedParticipant.getBusiness());
                updatedFields.put("date_of_birth__c", updatedParticipant.getDateOfBirth());

                // Get the record ID
                String id = (String) deelnemerRecord.get("Id");

                String gewijzigdeUuid = deelnemerJsonObject.optString("Deelnemer_uuid__c");


                // Update the Deelnemer in Salesforce using the retrieved ID and updated fields
                api.updateSObject("Deelnemer__c", id, updatedFields);
                deleteGewijzigdeDeelnemer(gewijzigdeUuid);

                System.out.println("Participant updated");
            } catch (Exception e) {
                e.printStackTrace();
                e.getMessage();
            }
        } else {
            System.out.println("No Deelnemer record found with UUID: " + uuid);
        }
    }

    public void deleteDeelnemer(String uuid) {
        // Retrieve Deelnemer by UUID
        String deelnemerJson = getDeelnemer(uuid);
        if (deelnemerJson != null) {
            try {
                // Convert JSON string to Map
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> deelnemerRecord = objectMapper.readValue(deelnemerJson, new TypeReference<Map<String, Object>>() {
                });
                String deelnemerId = (String) deelnemerRecord.get("Id");

                deletedUsersUuidList.add(uuid);
                // Delete the Deelnemer
                api.deleteSObject("Deelnemer__c", deelnemerId);
                System.out.println("Deelnemer deleted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                e.getMessage();
            }

        } else {
            System.out.println("Deelnemer not found.");
        }
    }


    public String getDeelnemer(String uuid) {
        // Query the newest Deelnemer__c record and order by CreatedDate in descending order
        String query = "SELECT Id, Name, familie_naam__c, Phone__c, Email__c, Bedrijf__c, date_of_birth__c, Deelnemer_uuid__c FROM Deelnemer__c WHERE Deelnemer_uuid__c = '" + uuid + "'";

        // Perform the query and get the first result
        QueryResult<Map> queryResult = api.query(query);
        ObjectMapper objectMapper = new ObjectMapper();
        if (queryResult.getTotalSize() > 0) {
            Map<String, Object> deelnemer = queryResult.getRecords().get(0);
            try {
                // Convert the map object to JSON string
                String jsonDeelnemer = objectMapper.writeValueAsString(deelnemer);
                return jsonDeelnemer;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No records found.");
        }
        return null;

    }

    // Method to query the newest Deelnemer__c record
    public String getNewestDeelnemerAsJson(String customObject) {

        // Query the newest Deelnemer__c record and order by CreatedDate in descending order
        String query = "SELECT Id, Name, familie_naam__c, Phone__c, Email__c, Bedrijf__c, date_of_birth__c, Deelnemer_uuid__c FROM " + customObject + " ORDER BY CreatedDate DESC LIMIT 1";

        // Perform the query and get the first result
        QueryResult<Map> queryResult = api.query(query);
        ObjectMapper objectMapper = new ObjectMapper();
        if (queryResult.getTotalSize() > 0) {
            Map<String, Object> deelnemer = queryResult.getRecords().get(0);
            // Check if the UUID is not null
            if (deelnemer.get("Deelnemer_uuid__c") != null) {
                try {
                    // Convert the map object to JSON string
                    String jsonDeelnemer = objectMapper.writeValueAsString(deelnemer);
                    return jsonDeelnemer;
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getMessage();
                }
            } else {
                System.out.println("Deelnemer UUID is null. Generating a new UUID...");
                try {
                    // Create a new UUID by making a POST request to the provided URL
                    URL url = new URL(MASTERUUID_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Construct the JSON payload
                    String payload = "{\"service\": \"" + "crm" + "\", \"id\": \"" + deelnemer.get("Id") + "\"}";
                    System.out.println(payload);

                    // Write the payload to the connection
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = payload.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Get the response code
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Get the response body
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }

                            // Parse the response JSON to get the UUID
                            JSONObject jsonResponse = new JSONObject(response.toString());
                            String newUUID = jsonResponse.getString("uuid");

                            // Update the deelnemer map with the new UUID
                            deelnemer.put("Deelnemer_uuid__c", newUUID);

                            String deelnemerId = deelnemer.get("Id").toString();
                            // Remove the Id field from the map
                            deelnemer.remove("Id");
                            // Convert the updated map object to JSON string
                            String jsonDeelnemer = objectMapper.writeValueAsString(deelnemer);
                            JSONObject jsonDeelnemerObject = new JSONObject(jsonDeelnemer);
                            updatedJsonDeelnemersList.add(jsonDeelnemerObject);

                            // Update the Salesforce object without the Id field
                            api.updateSObject(customObject, deelnemerId, deelnemer);


                            return jsonDeelnemer;
                        }
                    } else {
                        System.out.println("HTTP request failed with response code: " + responseCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    e.getMessage();
                }
            }
        } else {
            System.out.println("No records found.");
        }
        return null;
    }


    // Method to update a Business__c object
    public void updateBusiness(String uuid, Business updatedBusiness) {
        // Retrieve the Business record JSON string based on UUID
        String businessJson = getBusiness(uuid);

        if (businessJson != null) {
            try {
                // Convert JSON string to Map
                ObjectMapper objectMapper = new ObjectMapper();
                JSONObject businessJsonObject = new JSONObject(businessJson);
                Map<String, Object> businessRecord = objectMapper.readValue(businessJson, new TypeReference<Map<String, Object>>() {});

                // Prepare fields to update
                Map<String, Object> updatedFields = new HashMap<>();
                updatedFields.put("name", updatedBusiness.getName());
                updatedFields.put("VAT__c", updatedBusiness.getVat());
                updatedFields.put("email__c", updatedBusiness.getEmail());
                updatedFields.put("access_code__c", updatedBusiness.getAccessCode());
                updatedFields.put("address__c", updatedBusiness.getAddress());

                System.out.println(updatedFields.values());
                // Get the record ID
                String id = (String) businessRecord.get("Id");
                String gewijzigdeUuid = businessJsonObject.optString("Bedrijf_uuid__c");

                // Update the Business in Salesforce using the retrieved ID and updated fields
                api.updateSObject("Business__c", id, updatedFields);
                deleteGewijzigdeBusiness(gewijzigdeUuid);
                System.out.println("Business updated");
            } catch (Exception e) {
                e.printStackTrace();
                e.getMessage();
            }
        } else {
            System.out.println("No Business record found with UUID: " + uuid);
        }
    }


    // Method to delete a Business__c object
    public void deleteBusinessByUUID(String uuid) {
        // Retrieve the Business__c record by UUID
        String businessJson = getBusiness(uuid);

        // Check if a Business with the provided UUID exists
        if (businessJson != null) {
            try {
                // Convert JSON string to Map
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> businessRecord = objectMapper.readValue(businessJson, new TypeReference<Map<String, Object>>() {});

                // Get the Business__c record ID
                String businessId = (String) businessRecord.get("Id");

                deletedBusinessesUuidList.add(uuid);

                // Delete the Business__c object in Salesforce
                api.deleteSObject("Business__c", businessId);
                System.out.println("Business deleted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                e.getMessage();
            }
        } else {
            System.out.println("No Business found with UUID " + uuid);
        }
    }

    public String getBusiness(String uuid) {
        System.out.println("i am in getbusiness and api works");
        // SOQL query to retrieve Business__c record by uuid field
        String query = "SELECT Id, Name, VAT__c, Email__c, Access_Code__c, Address__c FROM Business__c WHERE Bedrijf_uuid__c = '" + uuid + "'";
        System.out.println("query executed");

        // Perform the query and get the first result
        QueryResult<Map> queryResult = api.query(query);
        ObjectMapper objectMapper = new ObjectMapper();
        if (queryResult.getTotalSize() > 0) {
            Map<String, Object> business = queryResult.getRecords().get(0);
            try {
                // Convert the map object to JSON string
                String jsonBusiness = objectMapper.writeValueAsString(business);
                return jsonBusiness;

            } catch (Exception e) {
                e.printStackTrace();
                e.getMessage();
            }
        } else {
            System.out.println("No records found.");
        }
        return null;
    }

    public void continuouslyCheckForNewUsers(String customObject) {
        while (true) {
            String method = "create";

            // Get the JSON representation of the current Deelnemer__c record
            String jsonDeelnemer = getNewestDeelnemerAsJson(customObject);

            System.out.println(jsonDeelnemer);

            // Check if jsonDeelnemer is null, if so, continue the loop
            if (jsonDeelnemer == null) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue; // Skip the rest of the loop and start from the beginning
            }


            // Print the JSON representation of the new Deelnemer__c record
            System.out.println("New " + customObject + " record:");
            System.out.println(jsonDeelnemer);
            String message = jsonDeelnemerToXml(jsonDeelnemer, method);
            String uuid = extractUUID(message);

                // Check if the message is not already in the list
                if (!createdDeelnemersUuidList.contains(uuid)) {
                    // Send the message to the exchange
                    sendToExchange(message);
                    // Add the message to the list
                    createdDeelnemersUuidList.add(uuid);
                }


            // Check if the list size exceeds the maximum allowed size (5)
            if (createdDeelnemersUuidList.size() > 5) {
                // Remove the oldest message from the list
                createdDeelnemersUuidList.remove(0);
            }

            // Wait for 15 seconds before checking again
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void continuouslyCheckForNewUpdatedUsers(String customObject) {
        while (true) {
            String method = "update";

            // Get the JSON representation of the current Deelnemer__c record
            String jsonDeelnemer = getNewestDeelnemerAsJson(customObject);


            System.out.println(jsonDeelnemer);

            // Check if jsonDeelnemer is null, if so, continue the loop
            if (jsonDeelnemer == null) {
                // Wait for 15 seconds before checking again
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue; // Skip the rest of the loop and start from the beginning
            }

            try {
                JSONObject newJson = new JSONObject(jsonDeelnemer);
                boolean isDuplicate = false;

                // Compare newJson with each existing JSON object in the list
                for (JSONObject oldJson : updatedJsonDeelnemersList ) {
                    String oldName = oldJson.optString("Name");
                    String newName = newJson.optString("Name");

                    String oldFamilieNaam = oldJson.optString("familie_naam__c");
                    String newFamilieNaam = newJson.optString("familie_naam__c");

                    String oldPhone = oldJson.optString("Phone__c");
                    String newPhone = newJson.optString("Phone__c");

                    String oldEmail = oldJson.optString("Email__c");
                    String newEmail = newJson.optString("Email__c");

                    String oldBedrijf = oldJson.optString("Bedrijf__c");
                    String newBedrijf = newJson.optString("Bedrijf__c");

                    String oldDateOfBirth = oldJson.optString("date_of_birth__c");
                    String newDateOfBirth = newJson.optString("date_of_birth__c");

                    if (oldName.equals(newName) &&
                            oldFamilieNaam.equals(newFamilieNaam) &&
                            oldPhone.equals(newPhone) &&
                            oldEmail.equals(newEmail) &&
                            oldBedrijf.equals(newBedrijf) &&
                            oldDateOfBirth.equals(newDateOfBirth)) {
                        isDuplicate = false;

                        System.out.println(oldName);
                        System.out.println(newName);
                    }
                }


                if (!isDuplicate) {
                    System.out.println("Changes detected in " + customObject + " record:");

                    // Print the JSON representation of the new Deelnemer__c record
                    System.out.println("New " + customObject + " record:");
                    System.out.println(jsonDeelnemer);
                    String message = jsonDeelnemerToXml(jsonDeelnemer, method);

                    // Add the new JSON to the list of existing JSON objects
                    updatedJsonDeelnemersList.add(newJson);

                    // Send the message to the exchange
                    sendToExchange(message);
                    String uuid = newJson.optString("Deelnemer_uuid__c");
                    deleteGewijzigdeDeelnemer(uuid);
                    updatedJsonDeelnemersList.remove(0);

                } else {
                    System.out.println("No changes detected in " + customObject + " record.");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Wait for 15 seconds before checking again
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void continuouslyCheckForDeletedUsers(String customObject) {
        while (true) {

            String method = "delete";

            // Get the JSON representation of the current Deelnemer__c record
            String jsonDeelnemer = getNewestDeelnemerAsJson(customObject);

            System.out.println(jsonDeelnemer);

            // Check if jsonDeelnemer is null, if so, continue the loop
            if (jsonDeelnemer == null) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue; // Skip the rest of the loop and start from the beginning
            }

                // Print the JSON representation of the new Deelnemer__c record
                System.out.println("New " + customObject + " record:");
                System.out.println(jsonDeelnemer);
                String message = jsonDeelnemerToXml(jsonDeelnemer, method);
                String uuid = extractUUID(message);


                // Check if the message is not already in the list
                if (!deletedUsersUuidList.contains(uuid)) {
                    // Send the message to the exchange
                    sendToExchange(message);
                    // Add the message to the list
                    deletedUsersUuidList.add(uuid);
                }


            // Check if the list size exceeds the maximum allowed size (5)
            if (deletedUsersUuidList.size() > 5) {
                // Remove the oldest message from the list
                deletedUsersUuidList.remove(0);
            }

            // Wait for 15 seconds before checking again
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static String extractUUID(String inputString) {
        String pattern = "uuid=\"([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})\"";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(inputString);

        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    public void deleteGewijzigdeDeelnemer(String uuid) {
        // Retrieve Deelnemer by UUID
        String deelnemerJson = getGewijzigdeDeelnemer(uuid);
        if (deelnemerJson != null) {
            try {
                // Convert JSON string to Map
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> deelnemerRecord = objectMapper.readValue(deelnemerJson, new TypeReference<Map<String, Object>>() {
                });
                String deelnemerId = (String) deelnemerRecord.get("Id");

                // Delete the Deelnemer
                api.deleteSObject("gewijzigde_Deelnemer__c", deelnemerId);
                System.out.println("Deelnemer deleted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                e.getMessage();
            }

        } else {
            System.out.println("Deelnemer not found.");
        }
    }

    public String getGewijzigdeDeelnemer(String uuid) {
        // Query the newest Deelnemer__c record and order by CreatedDate in descending order
        String query = "SELECT Id, Name, familie_naam__c, Phone__c, Email__c, Bedrijf__c, date_of_birth__c, Deelnemer_uuid__c FROM gewijzigde_Deelnemer__c WHERE Deelnemer_uuid__c = '" + uuid + "'";

        // Perform the query and get the first result
        QueryResult<Map> queryResult = api.query(query);
        ObjectMapper objectMapper = new ObjectMapper();
        if (queryResult.getTotalSize() > 0) {
            Map<String, Object> deelnemer = queryResult.getRecords().get(0);
            try {
                // Convert the map object to JSON string
                String jsonDeelnemer = objectMapper.writeValueAsString(deelnemer);
                return jsonDeelnemer;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No records found.");
        }
        return null;

    }


}




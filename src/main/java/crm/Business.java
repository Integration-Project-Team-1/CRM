package crm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.api.ApiConfig;
import com.force.api.ForceApi;
import com.force.api.QueryResult;
import jakarta.xml.bind.annotation.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static crm.Rabbitmq.sendBusinessToExchange;
import static crm.Rabbitmq.sendToExchange;
import static crm.Salesforce.*;
import static crm.XML.jsonBusinessToXml;
import static crm.XML.jsonDeelnemerToXml;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Business {
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "VAT")
    private String vat;

    @XmlElement(name = "email")
    private String email;

    @XmlElement(name = "access_code")
    private String accessCode;

    @XmlElement(name = "address")
    private String address;

    @XmlElement(name = "service")
    private String service;

    @XmlElement(name = "method")
    private String method;
    @XmlAttribute(name = "uuid")
    private String uuid;

    private static String MASTERUUID_URL = System.getenv("MASTERUUID_URL");

    public static List<String> createdBusinessesUuidList = new ArrayList<>();
    public static List<JSONObject> updatedJsonBusinessesList = new ArrayList<>();
    public static List<String> deletedBusinessesUuidList = new ArrayList<>();

    public Business() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    // Method to query the newest Business__c record
    public static String getNewestBusinessAsJson(String customObject) {


        // Query the newest Business__c record and order by CreatedDate in descending order
        String query= "SELECT Id, Name, VAT__c, Email__c, Access_Code__c, Address__c, Bedrijf_uuid__c FROM " + customObject + " ORDER BY CreatedDate DESC LIMIT 1";
        // Perform the query and get the first result
        QueryResult<Map> queryResult = api.query(query);
        ObjectMapper objectMapper = new ObjectMapper();
        if (queryResult.getTotalSize() > 0) {
            Map<String, Object> business = queryResult.getRecords().get(0);

            String accesscode = business.get("Access_Code__c").toString();
            String newAccesscode =  accesscode.replace(".0","");

            business.put("Access_Code__c",newAccesscode);

            // Check if the UUID is not null
            if (business.get("Bedrijf_uuid__c") != null) {
                try {

                    String businessId = business.get("Id").toString();

                    // Remove the Id field from the map
                    business.remove("Id");
                    // Convert the updated map object to JSON string
                    String jsonBusiness = objectMapper.writeValueAsString(business);
                    JSONObject jsonbusinessObject = new JSONObject(jsonBusiness);
                    updatedJsonBusinessesList.add(jsonbusinessObject);

                    // Update the Salesforce object without the Id field
                    api.updateSObject(customObject, businessId, business);
                    // Convert the map object to JSON string
                    return jsonBusiness;
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getMessage();
                }
            } else {
                System.out.println("business UUID is null. Generating a new UUID...");
                try {
                    // Create a new UUID by making a POST request to the provided URL
                    URL url = new URL(MASTERUUID_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Construct the JSON payload
                    String payload = "{\"service\": \"" + "crm" + "\", \"id\": \"" + business.get("Id") + "\"}";
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


                            // Update the business map with the new UUID
                            business.put("Bedrijf_uuid__c", newUUID);

                            String businessId = business.get("Id").toString();

                            // Remove the Id field from the map
                            business.remove("Id");
                            // Convert the updated map object to JSON string
                            String jsonBusiness = objectMapper.writeValueAsString(business);
                            JSONObject jsonbusinessObject = new JSONObject(jsonBusiness);
                            updatedJsonBusinessesList.add(jsonbusinessObject);

                            // Update the Salesforce object without the Id field
                            api.updateSObject(customObject, businessId, business);


                            return jsonBusiness;
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

    public static void continuouslyCheckForNewBusinesses(String customObject) {
        while (true) {
            String method = "create";

            // Get the JSON representation of the current Deelnemer__c record
            String jsonBusiness = getNewestBusinessAsJson(customObject);

            System.out.println(jsonBusiness);

            // Check if jsonDeelnemer is null, if so, continue the loop
            if (jsonBusiness == null) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue; // Skip the rest of the loop and start from the beginning
            }


            // Print the JSON representation of the new Deelnemer__c record
            System.out.println("New " + customObject + " record:");
            System.out.println(jsonBusiness);
            String message = jsonBusinessToXml(jsonBusiness, method);
            String uuid = extractUUID(message);

            // Check if the business is not already in the list
            if (!createdBusinessesUuidList.contains(uuid)) {
                // Send the message to the exchange
                sendBusinessToExchange(message);
                // Add the message to the list
                createdBusinessesUuidList.add(uuid);
            }


            // Check if the list size exceeds the maximum allowed size (5)
            if (createdBusinessesUuidList.size() > 5) {
                // Remove the oldest message from the list
                createdBusinessesUuidList.remove(0);
            }

            // Wait for 15 seconds before checking again
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void continuouslyCheckForNewUpdatedBusinesses(String customObject) {
        while (true) {
            String method = "update";

            // Get the JSON representation of the current Deelnemer__c record
            String jsonBusiness = getNewestBusinessAsJson(customObject);


            System.out.println(jsonBusiness);

            // Check if jsonDeelnemer is null, if so, continue the loop
            if (jsonBusiness == null) {
                // Wait for 15 seconds before checking again
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue; // Skip the rest of the loop and start from the beginning
            }

            try {
                JSONObject newJson = new JSONObject(jsonBusiness);
                boolean isDuplicate = false;

                for (JSONObject oldJson : updatedJsonBusinessesList) {
                    String oldName = oldJson.optString("Name");
                    String newName = newJson.optString("Name");

                    String oldVat = oldJson.optString("VAT__c");
                    String newVat = newJson.optString("VAT__c");

                    String oldEmail = oldJson.optString("Email__c");
                    String newEmail = newJson.optString("Email__c");

                    String oldAccessCode = oldJson.optString("Access_Code__c");
                    String newAccessCode = newJson.optString("Access_Code__c");

                    String oldAddress = oldJson.optString("Address__c");
                    String newAddress = newJson.optString("Address__c");

                    if (oldName.equals(newName) &&
                            oldVat.equals(newVat) &&
                            oldEmail.equals(newEmail) &&
                            oldAccessCode.equals(newAccessCode) &&
                            oldAddress.equals(newAddress)) {
                        isDuplicate = false;

                        System.out.println(oldName);
                        System.out.println(newName);
                    }
                }


                if (!isDuplicate) {
                    System.out.println("Changes detected in " + customObject + " record:");

                    // Print the JSON representation of the new Deelnemer__c record
                    System.out.println("New " + customObject + " record:");
                    System.out.println(jsonBusiness);
                    String message = jsonBusinessToXml(jsonBusiness, method);

                    // Add the new JSON to the list of existing JSON objects
                    updatedJsonBusinessesList.add(newJson);

                    // Send the message to the exchange
                    sendBusinessToExchange(message);
                    String uuid = newJson.optString("Bedrijf_uuid__c");
                    deleteGewijzigdeBusiness(uuid);
                    updatedJsonBusinessesList.remove(0);

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

    public static void deleteGewijzigdeBusiness(String uuid) {
        // Retrieve Deelnemer by UUID
        String businessJson = getGewijzigdeBusiness(uuid);
        if (businessJson != null) {
            try {
                // Convert JSON string to Map
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> businessRecord = objectMapper.readValue(businessJson, new TypeReference<Map<String, Object>>() {
                });
                String businessId = (String) businessRecord.get("Id");

                // Delete the Deelnemer
                api.deleteSObject("gewijzigde_Business__c", businessId);
                System.out.println("business deleted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                e.getMessage();
            }

        } else {
            System.out.println("business not found.");
        }
    }

    public static String getGewijzigdeBusiness(String uuid) {
        // Query the newest Deelnemer__c record and order by CreatedDate in descending order
        String query = "SELECT Id, Name, VAT__c, Email__c, Access_Code__c, Address__c FROM gewijzigde_Business__c WHERE Bedrijf_uuid__c = '" + uuid + "'";

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
            }
        } else {
            System.out.println("No records found.");
        }
        return null;

    }

    public static void continuouslyCheckForDeletedBusinesses(String customObject) {
        while (true) {

            String method = "delete";

            // Get the JSON representation of the current Deelnemer__c record
            String jsonBusiness = getNewestBusinessAsJson(customObject);

            System.out.println(jsonBusiness);

            // Check if jsonDeelnemer is null, if so, continue the loop
            if (jsonBusiness == null) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue; // Skip the rest of the loop and start from the beginning
            }

            // Print the JSON representation of the new Deelnemer__c record
            System.out.println("New " + customObject + " record:");
            System.out.println(jsonBusiness);
            String message = jsonBusinessToXml(jsonBusiness, method);
            String uuid = extractUUID(message);


            // Check if the message is not already in the list
            if (!deletedBusinessesUuidList.contains(uuid)) {
                // Send the message to the exchange
                sendBusinessToExchange(message);
                // Add the message to the list
                deletedBusinessesUuidList.add(uuid);
            }


            // Check if the list size exceeds the maximum allowed size (5)
            if (deletedBusinessesUuidList.size() > 5) {
                // Remove the oldest message from the list
                deletedBusinessesUuidList.remove(0);
            }

            // Wait for 15 seconds before checking again
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

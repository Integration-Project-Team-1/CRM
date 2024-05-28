package crm;

import org.json.JSONException;
import org.json.JSONObject;

public class XML {

    public static String jsonBusinessToXml(String jsonString, String method) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            String xmlData = String.format(
                    "<business xmlns=\"http://ehb.local\" uuid=\"%s\">%n" +
                            "    <service>crm</service>%n" +
                            "    <method>%s</method>%n" +
                            "    <name>%s</name>%n" +
                            "    <VAT>%s</VAT>%n" +
                            "    <email>%s</email>%n" +
                            "    <access_code>%s</access_code>%n" +
                            "    <address>%s</address>%n" +
                            "</business>%n",
                    jsonObject.optString("Bedrijf_uuid__c", ""),
                    method,
                    jsonObject.optString("Name", ""),
                    jsonObject.optString("VAT__c", ""),
                    jsonObject.optString("Email__c", ""),
                    jsonObject.optString("Access_Code__c", ""),
                    jsonObject.optString("Address__c", "")
            );

            return xmlData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static String jsonDeelnemerToXml(String jsonString, String method) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            String xmlData = String.format(
                    "<participant xmlns=\"http://ehb.local\" uuid=\"%s\">%n" +
                            "    <service>crm</service>%n" +
                            "    <method>%s</method>%n" +
                            "    <firstname>%s</firstname>%n" +
                            "    <lastname>%s</lastname>%n" +
                            "    <email>%s</email>%n" +
                            "    <phone>%s</phone>%n" +
                            "    <business>%s</business>%n" +
                            "    <date_of_birth>%s</date_of_birth>%n" +
                            "</participant>%n",
                    jsonObject.optString("Deelnemer_uuid__c", ""),
                    method,
                    jsonObject.optString("Name", ""),
                    jsonObject.optString("familie_naam__c", ""),
                    jsonObject.optString("Email__c", ""),
                    jsonObject.optString("Phone__c", ""),
                    jsonObject.optString("Bedrijf__c", ""),
                    jsonObject.optString("date_of_birth__c", "")
            );


            return xmlData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

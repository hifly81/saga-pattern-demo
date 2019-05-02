package com.redhat.demo.saga.elasticsearch;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EsClient {

    public static int addOrder(String json, String orderId) {

        if (System.getenv("ES_DISABLED") != null)
            return 1;

        HttpURLConnection conn = null;

        try {

            String urlES = System.getenv("es-host");
            URL url = new URL(urlES + "/orders/" + orderId);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            conn.disconnect();

            return HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            e.printStackTrace();
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        } finally {
            if (conn != null)
                conn.disconnect();
        }


    }

}

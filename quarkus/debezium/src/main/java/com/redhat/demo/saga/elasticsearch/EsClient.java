package com.redhat.demo.saga.elasticsearch;

/*import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;*/

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EsClient {

    public static int addOrder(String json, String orderId) {

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
            if(conn != null)
                conn.disconnect();
        }

    }

    /*public DocWriteResponse.Result addEntry(String json, String index) {
        IndexResponse response = null;
        try(RestHighLevelClient client =
                    new RestHighLevelClient(
                            RestClient.builder(
                                    new HttpHost(
                                            System.getenv("es-host"), Integer.valueOf(System.getenv("es-port")), "http")))) {

            System.out.println("Adding INDEX:" + response.getResult());

            IndexRequest request = new IndexRequest(json, index);

            request.source(json, XContentType.JSON);

            //TODO use indexAsync
            response = client.index(request);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("INDEX RESULT:" + response.getResult());
        return response.getResult();
    }*/


}

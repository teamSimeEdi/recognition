package si.RSOteam8;


import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;


import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("recognition")
@Log
public class RecognitionResource {


    @Inject
    private ConfigProperties cfg;

    @Counted(name = "getAllRecognitions-count")
    @GET
    public Response getAllRecognitions() throws IOException {

        //////standard edi stuff/////
        Logger.getLogger(RecognitionHealthCheck.class.getSimpleName()).info("just testing");
        List<Recognition> recognitions = new LinkedList<Recognition>();

        ///////////////////////


        //////od tle naprej je api call//////
        String credentialsToEncode = "acc_730d2fc218b8880" + ":" + "20d3bb6f1080dc31fae4f82c91d658c8";
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        String endpoint_url = "https://api.imagga.com/v2/tags";
        // "https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png"
        String image_url = cfg.getTest();

        String url = endpoint_url + "?image_url=" + image_url;
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", "Basic " + basicAuth);

        int responseCode = connection.getResponseCode();

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String jsonResponse = connectionInput.readLine();

        connectionInput.close();

        JSONObject obj = new JSONObject(jsonResponse);
        JSONObject obj2 = obj.getJSONObject("result");
        JSONArray arr = obj2.getJSONArray("tags");

        for (int i = 0; i < arr.length(); i++) {
            Recognition recognition = new Recognition();
            String confi = String.valueOf(arr.getJSONObject(i).getFloat("confidence"));
            recognition.setConfidence(confi);
            //System.out.println(arr.getJSONObject(i).getJSONObject("tag"));
            String tegu = arr.getJSONObject(i).getJSONObject("tag").getString("en");
            //System.out.println(tegu);
            recognition.setTag(tegu);
            recognitions.add(recognition);
        }

        //////////////tle se konca api call////////////

        return Response.ok(recognitions).build();
    }

    @GET
    @Path("{recognitionId}")
    public Response getRecognition(@PathParam("recognitionId") String recognitionId) {
        //Customer customer = Database.getCustomer(customerId);
        //return customer != null
        //       ? Response.ok(customer).build()
        //        : Response.status(Response.Status.NOT_FOUND).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewRecognition(Recognition recognition) {
        //Database.addCustomer(customer);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{recognitionId}")
    public Response deleteRecognition(@PathParam("recognitionId") String recognitionId) {
        //Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }
}

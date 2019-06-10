package se.kry.codetest;

import io.vertx.core.Future;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BackgroundPoller {

  public Future<List<String>> pollServices(Map<String, String> services, DBConnector connector) {
    final WebClient webClient = WebClient.create(Vertx.vertx());
    services.entrySet().stream().forEach(service -> {
      webClient.get(service.getKey())
          .send(ar -> {
            // Obtain response
            final HttpResponse<Buffer> result = ar.result();
            // Get response message
            final String statusMessage = result.statusMessage();
            // Update status of the service in the database
            connector.query("UPDATE service SET status = \"" + statusMessage
                + "\"").setHandler(F);
          });

    });
    //TODO
    return Future.failedFuture("TODO");
  }
}

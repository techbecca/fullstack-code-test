package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  private HashMap<String, String> services = new HashMap<>();
  //TODO use this
  private DBConnector connector;
  private BackgroundPoller poller = new BackgroundPoller();

  @Override
  public void start(Future<Void> startFuture) {
    connector = new DBConnector(vertx);
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    getServicesFromDB();
    vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices(services, connector));
    setRoutes(router);
    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8080, result -> {
          if (result.succeeded()) {
            System.out.println("KRY code test service started");
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private void getServicesFromDB() {
    connector.query("SELECT * FROM service;").setHandler(done -> {
          if (done.succeeded()) {
            for (JsonObject entries : done.result().getRows()) {
              services.put(entries.getString("url"), "UNKNOWN");
            }
          } else {
            done.cause().printStackTrace();
          }
        }
    );
  }

  private void setRoutes(Router router) {
    router.route("/*").handler(StaticHandler.create());
    final String path = "/service";
    final String contentType = "content-type";
    router.get(path).handler(req -> {
      List<JsonObject> jsonServices = services
          .entrySet()
          .stream()
          .map(service ->
              new JsonObject()
                  .put("contentType", service.getKey())
                  //.put("url", service.getValue().getUrl())
                  //.put("time_added", service.getValue().getFormattedDate())
                  .put("status", service.getValue()))
          .collect(Collectors.toList());
      req.response()
          .putHeader(contentType, "application/json")
          .end(new JsonArray(jsonServices).encode());
    });
    router.post(path).handler(this::addService);
    router.delete(path).handler(req -> {
      JsonObject jsonBody = req.getBodyAsJson();
      final String url = jsonBody.getString("url");
      connector.query("DELETE FROM service WHERE url = \"" + url + "\";").setHandler(done -> {
        if (done.succeeded()) {
          System.out.println("Service " + url + " was deleted");
        } else {
          done.cause().printStackTrace();
        }
      });
      req.response() //TODO: write erronous response if DB entry fails
          .putHeader(contentType, "text/plain")
          .end("OK");
    });
  }

  private void addService(RoutingContext req) {
    JsonObject jsonBody = req.getBodyAsJson();
    final String url = jsonBody.getString("url");
    final String name = jsonBody.getString("name");
    final Service service = new Service(name, url);
    services.put(url, "UNKNOWN");
    connector.query("INSERT OR IGNORE INTO service VALUES( \""
        + service.getUrl() + "\", \""
        + service.getName() + "\", "
        + service.getTimeStampMillis() + ");").setHandler(done -> {
          if (done.succeeded()) {
            System.out.println("Service " + url + " inserted or already exists");
          } else {
            done.cause().printStackTrace();
          }
        }
    );
    req.response() //TODO: write erronous response if DB entry fails.
        .putHeader("content-type", "text/plain")
        .end("OK");
  }
}

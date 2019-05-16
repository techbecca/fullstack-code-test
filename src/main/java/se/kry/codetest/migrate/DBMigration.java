package se.kry.codetest.migrate;

import io.vertx.core.Vertx;
import se.kry.codetest.DBConnector;

public class DBMigration {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DBConnector connector = new DBConnector(vertx);
    connector.query("CREATE TABLE IF NOT EXISTS service (url VARCHAR(128) NOT NULL UNIQUE)").setHandler(done -> {
      if (done.succeeded()) {
        System.out.println("completed db migrations");
      } else {
        done.cause().printStackTrace();
      }
      vertx.close(shutdown -> {
        System.exit(0);
      });
    });

    connector.query("INSERT OR IGNORE INTO service VALUES(\"https://www.kry.se\");").setHandler(done -> {
          if (done.succeeded()) {
            System.out.println("Init value inserted");
          } else {
            done.cause().printStackTrace();
          }
        }
    );

  }
}

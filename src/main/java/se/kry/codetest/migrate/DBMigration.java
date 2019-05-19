package se.kry.codetest.migrate;

import io.vertx.core.Vertx;
import se.kry.codetest.DBConnector;
import se.kry.codetest.Service;

public class DBMigration {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DBConnector connector = new DBConnector(vertx);
    connector.query("CREATE TABLE IF NOT EXISTS service (url VARCHAR(128) NOT NULL UNIQUE, "
        + "name VARCHAR(128) NOT NULL,"
        + "time_stamp INTEGER NOT NULL)").setHandler(done -> {
      if (done.succeeded()) {
        System.out.println("completed db migrations");
      } else {
        done.cause().printStackTrace();
      }
      vertx.close(shutdown -> {
        System.exit(0);
      });
    });

    Service kry = new Service("https://www.kry.se");
    connector.query("INSERT OR IGNORE INTO service VALUES( \""
        + kry.getUrl() + "\", \""
        + kry.getName() + "\", "
        + kry.getTimeStampMillis() + ");").setHandler(done -> {
          if (done.succeeded()) {
            System.out.println("Init value inserted");
          } else {
            done.cause().printStackTrace();
          }
        }
    );

  }
}

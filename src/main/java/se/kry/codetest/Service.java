package se.kry.codetest;

import io.vertx.core.json.JsonObject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class Service {

  private String url;
  private String name;
  private String status = "UNKNOWN";
  private long timeStampMillis;

  Service(String name, String url) {
    this.name = name;
    this.url = url;
    this.timeStampMillis = Instant.now().toEpochMilli();
  }

  public Service(String url) {
    this.url = url;
    this.name = "UNKNOWN_NAME";
    this.timeStampMillis = Instant.now().toEpochMilli();

  }

  public String getUrl() {
    return url;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getTimeStampMillis() {
    return timeStampMillis;
  }

  public String getFormattedDate() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStampMillis), ZoneId.systemDefault()).format(dateTimeFormatter);
  }

  public JsonObject toJson() {
    return this.toJson();
  }

}

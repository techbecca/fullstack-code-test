package se.kry.codetest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Service {

  private String url;
  private String name;
  private String status = "UNKNOWN";
  private LocalDate localDate;

  public Service(String name, String url) {
    this.name = name;
    this.url = url;
    this.localDate = LocalDate.now();
  }

  public Service(String url) {
    this.url = url;
    this.name = "UNKNOWN_NAME";
    this.localDate = LocalDate.now();
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

  public LocalDate getLocalDate() {
    return localDate;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getFormattedDate() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
    return localDate.format(dateTimeFormatter);
  }


}

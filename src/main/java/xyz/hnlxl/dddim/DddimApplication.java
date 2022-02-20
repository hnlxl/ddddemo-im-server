package xyz.hnlxl.dddim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/** The application launcher */
@SpringBootApplication
@EnableAsync
public class DddimApplication {

  public static void main(String[] args) {
    SpringApplication.run(DddimApplication.class, args);
  }

}

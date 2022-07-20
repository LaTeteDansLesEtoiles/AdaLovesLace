package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import com.google.gson.Gson;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.DiagramDTO;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ShareButton extends ImageButton {

  public static final String SHARE_BUTTON_NAME         = "         Share         ";

  private static final Logger logger                  = LoggerFactory.getLogger(ShareButton.class);

  public ShareButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onShareAction(app));
    buildButtonImage("share.png");
  }

  public static void onShareAction(App app) {
    logger.info("Share file");

    DiagramDTO diagram;

    try {
    diagram = new ImageUtil(app).getDiagram();
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }

    Gson gson = new Gson();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = postRequest(diagram, gson);

    handleResponse(client, request);
  }

  private static void handleResponse(HttpClient client, HttpRequest request) {
    CompletableFuture<HttpResponse<String>> completableFuture =
      client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    completableFuture
      .thenApplyAsync(HttpResponse::headers);
    completableFuture.join();

    try {
      logger.info("Response status code: {}", completableFuture.get().statusCode());
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Error getting response status code!", e);
      throw new RuntimeException("Error getting response status code!", e);
    }
  }

  private static HttpRequest postRequest(DiagramDTO diagram, Gson gson) {
    return HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:18081/api/upload-diagram"))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(diagram)))
      .build();
  }

}

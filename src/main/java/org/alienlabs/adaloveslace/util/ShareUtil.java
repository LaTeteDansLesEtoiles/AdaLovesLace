package org.alienlabs.adaloveslace.util;

import com.google.gson.Gson;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.DiagramDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.alienlabs.adaloveslace.App.ADA_LOVES_LACE_WEB;
import static org.alienlabs.adaloveslace.App.ADA_LOVES_LACE_WEB_SHARE_ENDPOINT;

public class ShareUtil {

  private static final Logger logger = LoggerFactory.getLogger(ShareUtil.class);

  public ShareUtil(App app, String filename, String username, String clientId, String clientSecret) {
    logger.info("Sharing in progress");
    DiagramDTO diagram;

    try {
      diagram = new ImageUtil(app).getDiagram(filename, username, clientId, clientSecret);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }

    Gson gson = new Gson();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = postRequest(diagram, gson);

    handleResponse(client, request);
  }

  private void handleResponse(HttpClient client, HttpRequest request) {
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

  private HttpRequest postRequest(DiagramDTO diagram, Gson gson) {
    return HttpRequest.newBuilder()
      .uri(URI.create(ADA_LOVES_LACE_WEB + ADA_LOVES_LACE_WEB_SHARE_ENDPOINT))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(diagram)))
      .build();
  }

}

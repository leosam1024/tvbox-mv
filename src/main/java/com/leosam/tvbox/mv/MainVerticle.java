package com.leosam.tvbox.mv;

import com.leosam.tvbox.mv.data.MvResult;
import com.leosam.tvbox.mv.data.VodResult;
import com.leosam.tvbox.mv.service.MvService;
import com.leosam.tvbox.mv.utils.NumberUtils;
import com.leosam.tvbox.mv.utils.StringUtils;
import com.leosam.tvbox.mv.utils.VertxUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class MainVerticle extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  private static MvService mvService;
  private static int port = 0;

  static {
    CompletableFuture.runAsync(MainVerticle::initIndex)
            .thenAcceptAsync((a) -> {
              if (port > 0) {
                logger.info("索引完成，可以请求数据了, HTTP server port " + port);
              } else {
                logger.info("索引完成，可以请求数据了");
              }
            });
  }

  private static void initIndex() {
    try {
      MvService mvService = new MvService();
      mvService.initIndex();
      MainVerticle.mvService = mvService;
    } catch (Exception e) {
      logger.error("初始化索引失败", e);
    }
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer httpServer = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.route("/mv/search").handler(this::searchMv);
    router.route("/mv/vod").handler(this::searchMvVod);
    router.route().handler(req -> req.response().putHeader("content-type", "text/plain").end("Hello from Vert.x!"));
    httpServer.requestHandler(router);

    httpServer.listen(7777, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        logger.info("HTTP server started on port 7777");
      } else {
        startPromise.fail(http.cause());
      }
    });
    port = httpServer.actualPort();
  }

  private void searchMvVod(RoutingContext req) {
    // 获取参数
    String wd = VertxUtils.queryParam(req, "wd");
    String ac = VertxUtils.queryParam(req, "ac");
    String ids = VertxUtils.queryParam(req, "ids");
    String maxCount = VertxUtils.queryParam(req, "maxCount");
    int max = Math.min(Math.max(NumberUtils.toInt(maxCount, 100), 10), 1000);
    String query = StringUtils.isNotEmpty(wd) ? wd : ids;

    // 处理结果
    VodResult vodResult = null;
    try {
      if (mvService != null) {
        vodResult = mvService.searchVod(query, max);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // 返回数据
    String jsonString = Json.encode(vodResult);
    req.response()
            .putHeader("content-type", "application/json")
            .end(jsonString);
  }

  private void searchMv(RoutingContext req) {
    // 获取参数
    String query = VertxUtils.queryParam(req, "query");
    String maxCount = VertxUtils.queryParam(req, "maxCount");
    int max = Math.min(Math.max(NumberUtils.toInt(maxCount, 100), 10), 1000);

    // 处理结果
    MvResult search = null;
    try {
      if (mvService != null) {
        search = mvService.search(query, max);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // 返回数据
    String jsonString = Json.encode(search);
    req.response()
            .putHeader("content-type", "application/json")
            .end(jsonString);
  }


  public static void main(String[] args) throws Exception {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}

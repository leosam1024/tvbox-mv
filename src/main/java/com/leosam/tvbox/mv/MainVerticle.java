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
import io.vertx.ext.web.handler.StaticHandler;
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
    router.route("/vod").handler(this::searchVod);
    router.route("/vod/:index").handler(this::searchVod);
    router.route("/*").handler(StaticHandler.create("static"));
    router.route().handler(this::home);
    httpServer.requestHandler(router);

    httpServer.listen(7777, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        logger.info("HTTP server started on port 7777");
      } else {
        startPromise.fail(http.cause());
        logger.info("HTTP server fail, on port 7777, 启动失败，更换端口重试", http.cause());
      }
    });
    port = httpServer.actualPort();
  }

  private void searchMvVod(RoutingContext req) {
    searchVod(req, MvService.MV_FILE_NAME);
  }

  private void searchVod(RoutingContext req) {
    searchVod(req, StringUtils.EMPTY);
  }

  /**
   * 参考 <a href="https://github.com/FongMi/TV/blob/release/app/src/main/java/com/fongmi/android/tv/model/SiteViewModel.java#L32">...</a>
   * @param req
   */
  private void searchVod(RoutingContext req, String index) {
    // 获取参数
    String wd = VertxUtils.queryParam(req, "wd");
    String ids = VertxUtils.queryParam(req, "ids");
    String query = StringUtils.trimToEmpty(StringUtils.defaultIfEmpty(wd, ids));
    String ac = VertxUtils.queryParam(req, "ac");
    String type = VertxUtils.queryParam(req, "t");
    String maxCount = VertxUtils.queryParam(req, "maxCount");
    int max = Math.min(Math.max(NumberUtils.toInt(maxCount, 200), 10), 1000);
    String pg = VertxUtils.queryParam(req, "pg");
    int page = NumberUtils.toInt(pg, 0);
    if(StringUtils.isEmpty(index)){
      String pathIndex = req.pathParam("index");
      String queryIndex = VertxUtils.queryParam(req, "index");
      index = StringUtils.trimToEmpty(StringUtils.defaultIfEmpty(pathIndex, queryIndex));
    }

    // 处理结果
    VodResult vodResult = null;
    try {
      // 搜索
      if (mvService != null && StringUtils.isNotEmpty(query) && page <= 0) {
        vodResult = mvService.searchVod(index, query, max);
      }

      // 首页
      if (mvService != null && StringUtils.isEmpty(query)) {
        vodResult = mvService.searchVodHome(index, type, page);
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
    String wd = VertxUtils.queryParam(req, "wd");
    query = StringUtils.isNotEmpty(query) ? query : wd;
    String maxCount = VertxUtils.queryParam(req, "maxCount");
    int max = Math.min(Math.max(NumberUtils.toInt(maxCount, 200), 10), 1000);

    // 处理结果
    MvResult search = null;
    try {
      if (mvService != null) {
        search = mvService.search(null, query, max);
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

  private void home(RoutingContext req) {
    req.response()
            .putHeader("content-type", "text/plain;charset=utf-8")
            .end("MV搜索服务\n源码网址：https://github.com/leosam1024/tvbox-mv");
  }


  public static void main(String[] args) throws Exception {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}

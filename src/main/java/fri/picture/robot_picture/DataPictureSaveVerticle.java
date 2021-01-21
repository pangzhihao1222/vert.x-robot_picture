package fri.picture.robot_picture;

import fri.picture.robot_picture.utils.Base64Util;
import fri.picture.robot_picture.utils.DateUtil;
import io.netty.util.internal.StringUtil;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.UUID;




public class DataPictureSaveVerticle extends AbstractVerticle {


  private static final Logger logger = LoggerFactory.getLogger(DataPictureSaveVerticle.class);


  //第一步 声明router
  Router router;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    //第二步 初始化Router
    router = Router.router(vertx);

    //获取body参数，得先添加这句
    router.route().handler(BodyHandler.create());

    /**
     * 引入配置依赖
     */
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(ar -> {
      if (ar.failed()) {

      } else {
        JsonObject config = ar.result();
        String imgHeader = config.getString("img_header");
        System.out.println(imgHeader);
        /**
         * json 格式
         * application/json
         * http://localhost:8888/test/json
         */
        router.post("/picture/save").handler(req ->
        {
          //vert.x获取json参数就这一句req.getBodyAsJson()
          String img = req.getBodyAsString();
          if (!StringUtil.isNullOrEmpty(img)) {
            logger.info("图片不为空:{}",img);

            String imgPath = imgHeader + DateUtil.getNowDate_EN() + "/";
            String imgName = DateUtil.getNowTime_CN_HHmmss().replaceAll("[^x00-xff]*", "") +
              UUID.randomUUID().toString().replace("-", "") + ".jpg";
            if(Base64Util.uploadImg(img,imgPath,imgName)){
              logger.info("图片转换成功");
              String url = "/" + DateUtil.getNowDate_EN() + "/" + imgName;
              req.response()
                .putHeader("content-type", "text/plain")
                .putHeader("charset", "utf-8")
                .end(url);
            }
            logger.error("图片存储失败"+img);
          } else {
            logger.error("图片为空:"+img);
            req.response()
              .putHeader("content-type", "application/json")
              .putHeader("charset", "utf-8")
              .end();
          }
        });
        vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
          if (http.succeeded()) {
            startPromise.complete();
            System.out.println("HTTP server started on port 8888");
          } else {
            startPromise.fail(http.cause());
          }
        });
      }
  });

 }
}

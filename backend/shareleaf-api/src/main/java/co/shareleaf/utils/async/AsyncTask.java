package co.shareleaf.utils.async;

import co.shareleaf.RootConfiguration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Biz Melesse created on 12/24/22
 */
@Slf4j
public class AsyncTask {

  public static void submit(Runnable task, Runnable callback) {
    try {
      CompletableFuture
          .runAsync(task, RootConfiguration.executor)
          .get();
      if (callback != null) {
        callback.run();
      }
    } catch (InterruptedException | ExecutionException e) {
      log.error(e.getLocalizedMessage());
    }
  }

}

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        WildberriesParser parser = new WildberriesParser();
        List<CompletableFuture<Void>> tasks = getTasks(service, parser);

        FileHandler fh = new FileHandler("/Users/pvolok/product.log");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        CompletableFuture<Void> all = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()]));
        all.get();
        service.shutdown();

    }

    private static List<CompletableFuture<Void>> getTasks(ExecutorService service, WildberriesParser parser) {
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for (int i = 3910000; i < 3920000; i++) {
            int finalI = i;
            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                try {
                    WildberriesProduct product = parser.download(finalI);
                    logger.info(product.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, service);
            tasks.add(task);
        }
        return tasks;
    }
}

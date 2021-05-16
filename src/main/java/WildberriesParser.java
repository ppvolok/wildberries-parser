import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class WildberriesParser {

    private static final String BASE_URL = "https://www.wildberries.ru";

    public WildberriesProduct download(long id) throws IOException {
        String url = BASE_URL + "/catalog/" + id + "/detail.aspx";
        try {
            Document doc = Jsoup.connect(url).get();


            WildberriesProduct product = new WildberriesProduct(id, 200);
            product.setName(doc.select("div.brand-and-name.j-product-title > span.name").text());
            product.setBrand(doc.select("div.brand-and-name.j-product-title > span.brand").text());

            String price = doc
                    .select("div.order-block > div > div > div > span")
                    .text()
                    .replace(" ", "")
                    .replace("₽", "");

            if ("".equals(price)) {
                product.setPrice(null);
            } else {
                product.setPrice(Double.parseDouble(price.replaceAll("[^\\d.]", "")));
            }

            product.setDescription(doc.select("#container > div.product-content-v1 > div:nth-child(6) > div.card-left2 > div > div.card-description.j-collapsable-description.i-collapsable-v1 > div:nth-child(2) > div.j-description.collapsable-content.description-text > p").text());

            return product;
        } catch (HttpStatusException e) {
            WildberriesProduct product = new WildberriesProduct(id, e.getStatusCode());
            return product;
        }
    }
}

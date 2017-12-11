package strategies;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CaffeParser extends WebsiteParser {


    @Override
    public String getURL() {
        Document d = this.document;

        Elements e = d.select("meta[property='og:url']");

        if (e != null && e.first() != null) {
            return e.first().attr("content");
        }
        return null;
    }

    @Override
    public String getBody() {
        Elements bodyContainer = this.document.select(".testo.articoloInterno");

        if (bodyContainer == null || bodyContainer.first() == null) {
            return "";
        }
        return bodyContainer.text();
    }

    @Override
    public String getDate() {
        Elements dateContainer = this.document.select(".data");
        if (dateContainer == null || dateContainer.first() == null) {
            return "";
        }
        return dateContainer.first().text();
    }

    @Override
    public String getTitle() {
        Document d = this.getDocument();
        Elements titleElement = d.select("#titolo");

        if (titleElement == null || titleElement.first() == null) {
            return "";
        }

        return titleElement.first().text();
    }
}

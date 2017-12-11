package strategies;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class TIOParser extends WebsiteParser {
    public TIOParser() {

    }


    @Override
    public String getURL() {
        Document d = this.getDocument();

        Elements e = d.select("meta[property='og:url']");

        if (e != null && e.first() != null) {
            return e.first().attr("content");
        }
        return null;
    }

    @Override
    public String getDate() {
        String strDate = "";

        Elements headings = this.getDocument().select(".list-item-heading");
        if (headings == null || headings.first() == null) {
            return strDate;
        }

        Elements dateHeading = headings.first().select(".lh-r");
        if (dateHeading == null || dateHeading.first() == null) {
            return strDate;
        }

        strDate = this.getDocument().select(".list-item-heading").first().select(".lh-r").first().text().split(" - ")[0];

        return strDate;
    }

    @Override
    public String getTitle() {
        return this.getDocument().title().replace("Ticinonline - ", "");
    }

    @Override
    public String getBody() {
        Document d = this.getDocument();
        Elements sections = d.select(".section");
        if (sections == null || sections.size() < 2) {
            return "";
        }

        Element mainBody = sections.get(1);
        if (mainBody == null || mainBody.text() == null) {
            return "";
        }

        return mainBody.text();
    }
}

package strategies;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Date;

public abstract class WebsiteParser {
    protected Document document;

    protected Document getDocument() {
        return this.document;
    }

    protected void setDocument(String s) {
        this.document = Jsoup.parse(s);
    }

    public void setContent(String s) {
        this.setDocument(s);
    }

    public boolean isValidArticle() {
        Boolean url = !(getURL() == null);
        Boolean title = !getTitle().isEmpty();
        Boolean content = !getBody().isEmpty();

        return url && title && content;
    };
    abstract public String getURL();
    abstract public String getBody();
    abstract public String getDate();
    abstract public String getTitle();
}

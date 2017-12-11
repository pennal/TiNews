package strategies;

import org.jsoup.select.Elements;

public class TicinonewsParser extends WebsiteParser {

    @Override
    public String getURL() {

        Elements urlElement = this.document.select(".sp.ico_google");
        if (urlElement == null || urlElement.first() == null) {
            return "";
        }

        return urlElement.first().attr("onclick").split(",")[1].replace(")", "").replaceAll("'", "");
    }

    @Override
    public String getBody() {
        Elements mainBody = this.document.select(".wl");
        if (mainBody == null || mainBody.first() == null) {
            return "";
        }

        String content = mainBody.select("h3").text();

        return content;
    }

    @Override
    public String getDate() {
        String b = this.getBody();

        int i = b.lastIndexOf("|");

        if (i < 0) {
            return "";
        }

        String dateContainer = b.substring(i, b.length());
        String[] els = dateContainer.split(" ");


        return els[1] + " " + els[2] + " " + els[3];
    }

    @Override
    public String getTitle() {
        return this.document.title().split(" - ")[0];
    }
}

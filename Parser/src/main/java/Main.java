import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Article;
import strategies.CaffeParser;
import strategies.TIOParser;
import strategies.TicinonewsParser;
import strategies.WebsiteParser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        // Check the required arguments have been supplied
        if (args.length < 2) {
            throw new IllegalArgumentException("Missing arguments");
        }

        // Get the paths
        String currentPath = args[0];
        String jsonPath = args[1];


        // Check they are not empty
        if (currentPath.equals("")) {
            throw new IllegalArgumentException("Missing path to dump location");
        }

        if (jsonPath.equals("")) {
            throw new IllegalArgumentException("Missing path to json file");
        }


        List<Article> articleList = new ArrayList<>();

        try {
            WebsiteParser p = null;


            if (currentPath.contains("tio")) {
                p = new TIOParser();
            } else if (currentPath.contains("caffe")) {
                p = new CaffeParser();
            } else if (currentPath.contains("ticinonews")) {
                p = new TicinonewsParser();
            } else {
                System.exit(1);
            }

            List<Path> fileList = Files.walk(Paths.get(currentPath))
                    .filter((el) -> el.getFileName().toString().contains(".html")).collect(Collectors.toList());

            for (Path file: fileList) {
                try {
                    List<String> content = Files.readAllLines(file);

                    StringBuilder rawHtml = new StringBuilder();
                    for (String s : content) {
                        rawHtml.append(s).append("\n");
                    }

                    p.setContent(rawHtml.toString());

                    if (p.isValidArticle()) {
                        Article t = new Article();

                        t.setContent(p.getBody());
                        t.setDate(p.getDate());
                        t.setTitle(p.getTitle());
                        t.setUrl(p.getURL());
                        t.setId(p.getBody().hashCode());

                        articleList.add(t);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Found " + articleList.size() + " article(s)");


        Gson gson = new Gson();
        Type type = new TypeToken<List<Article>>() {}.getType();
        String json = gson.toJson(articleList, type);


        try {
            Files.write(Paths.get(jsonPath), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished Parsing!");
    }
}

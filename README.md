# TiNews

This is the repo that contains all the work done for the course project in IR. Be sure to follow the instructions very carefully. For an insight in the design decisions please take a look at the report.

## Before you begin
Before you begin, make sure you have the following tools installed:

 * Java (>= 8)
 * wget (any version will do)
 * Maven (Usually bundled with the Java JDK)
 * Caddy (or any other webserver will do)

Start by downloading the repo, either by a git clone or a simple download.

In order to contain the project in one location, we are going to create a folder to hold all the info. Start by creating the folder:

```bash
# Start by navigating to THIS REPO using the terminal 

# Create a dir for the project
mkdir IRProject

# Any further command will assume you are here
cd IRProject 

# Export the path for good measure, so we can use it later
export IR_PROJECT_ROOT="$(pwd)"
```

## WARNING
All the paths assume you are working from inside of this repo. If you choose to change this, good luck!

## Setup
This part will get you setup from top to bottom 
### Part 1: Nutch
This part will guide you in getting nutch setup for crawling the websites. 

#### Download & partial setup
 	
```bash
# Download Nutch
wget http://www-eu.apache.org/dist/nutch/1.13/apache-nutch-1.13-bin.tar.gz

# Unpack it
tar xvfz apache-nutch-1.13-bin.tar.gz

# Get into the main folder
cd apache-nutch-1.13/

# Create the needed folders
# urls/ will hold the urls to crawl, crawlingDir/ will hold the crawled data
mkdir -p urls/ crawlingDir/ 	

# By default, nutch requires you to tell it the websites you want to crawl. 
# Let us assume we wish to crawl `http://www.example.com/`. Then

echo "http://www.example.com/" > urls/seed.txt 
```
At this point, you need to set the correct regexes for how Nutch will follow the URLs. As I explained in the report, I decided to keep Nutch from going outside of my current website.

Open up the file `conf/crawl-urlfilter.txt` and replace the last line, the one that says

```bash
+.
```

with the following file

```bash
# Allow the ones on the same domain
+(example\.com)

# Refuse any other url
-.
```

Repeat the same process for the file `conf/regex-urlfilter.txt`.

At this point, you are ready to crawl. Follow the instrcutions in the next section

#### Crawling

```bash
# Ensure that JAVA_HOME is set! to check, run
echo $JAVA_HOME

# If the output contains a path, you are good to go
# If the output is empty, set it by finding where you installed java. On my case, it was at
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-9.0.1.jdk/Contents/Home
# CHECK TO MAKE SURE THIS PATH EXISTS BEFORE YOU SET IT!

# In my case, I found that taking 5 as number of rounds yielded good results. 
# If you wish you can change this number, but I would not recommend you go under this value.
# Therefore to start crawling
./bin/crawl urls/ crawlingDir 5
```
The crawling process will take several hours, and in the end you will find your segments in the `crawlingDir` directory. 


#### Conversion
The tool I wrote takes the content of the segments, dumped as HTML and parsed them. Therefore, before we continue, we need to convert the data from Nutch. 

```bash
# Create a directory to hold the converted data
mkdir htmlDump

# Convert the content
./bin/nutch dump -segment crawlingDir/segments -outputDir htmlDump
```

Now you will have a folder called `htmlDump` that contains a lot of subfolders, each with its own document. In the next section, we use the tool I created to convert the content.

### Part 2: Utility Conversion
Now we are going to create a JSON file containing only the valid articles, all bundled in one nice JSON. To do so, we use the utility that I wrote.

```bash
# Navigate back to the parser folder
cd $IR_PROJECT_ROOT/TiNews/Parser
 
# Execute the conversion process
mvn exec:java -D"exec.mainClass"="Main" -Dexec.args="$IR_PROJECT_ROOT/apache-nutch-1.13/htmlDump $IR_PROJECT_ROOT/dump.json"
```

Once this process is done, we need to seed the data into Solr, and get it ready to run. This is shown in the next section

### Part 3: Solr
```bash
# Start by getting back to the root of the project
cd $IR_PROJECT_ROOT

# Download Solr
wget http://mirror.easyname.ch/apache/lucene/solr/7.1.0/solr-7.1.0.tgz

# Extract it
tar xvfz solr-7.1.0.tgz

# Get into the main folder
cd solr-7.1.0/
```

Before we continue, we need to make sure that Solr allows cross origin requests otherwise our client won't work. Edit the file `server/solr-webapp/webapp/WEB-INF/web.xml` and add the following XML before the existing filter section:

```xml
<filter>
    <filter-name>cross-origin</filter-name>
    <filter-class>org.eclipse.jetty.servlets.CrossOriginFilter</filter-class>
    <init-param>
         <param-name>allowedOrigins</param-name>
         <param-value>http://localhost*</param-value>
    </init-param>
     <init-param>
         <param-name>allowedMethods</param-name>
         <param-value>GET,POST,DELETE,PUT,HEAD,OPTIONS</param-value>
     </init-param>
     <init-param>
         <param-name>allowedHeaders</param-name>
         <param-value>origin, content-type, cache-control, accept, options, authorization, x-requested-with</param-value>
     </init-param>
    <init-param>
        <param-name>supportsCredentials</param-name>
        <param-value>true</param-value>
    </init-param>
    <init-param>
      <param-name>chainPreflight</param-name>
      <param-value>false</param-value>
    </init-param>
</filter>

<filter-mapping>
  <filter-name>cross-origin</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>
```

Now, we can get solr working and ingesting data

```bash
# Start it up
./bin/solr start

# Create a new core
./bin/solr create_core -c nutch -d _default

# Seed the data from the JSON
./bin/post -c nutch $IR_PROJECT_ROOT/dump.json
```

At this point, you are good to go.

## Running the Client
In order to run the client, any webserver will do. In my case I decided to use Caddy. If you decide to use anything other than Caddy, you may need to adapt the instructions to work with your chosen server. 

```bash
# Get into the frontend folder
cd $IR_PROJECT_ROOT/TiNews/Frontend

# Run the server!
caddy
```

Open `http://localhost:2015` and you should be good to go. Good luck!


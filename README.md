### **📌 Document Similarity Using Hadoop MapReduce**

**Assignment #1: Document Similarity using MapReduce**  
---

## **📖 Objective**
The goal of this assignment is to compute the **Jaccard Similarity** between pairs of documents using **Hadoop MapReduce**.  

A MapReduce job is implemented to:  
1. Extract words from multiple text documents.  
2. Identify which words appear in multiple documents.  
3. Compute the **Jaccard Similarity** between document pairs.  
4. Output document pairs with their similarity score.  

---

## **📖 Problem Statement**
Given a set of documents, we need to determine the **similarity between each pair of documents** based on the words they contain.  

The **Jaccard Similarity** is calculated using the formula:  

\[
Jaccard Similarity (A, B) = \frac{|A ∩ B|}{|A ∪ B|}
\]

Where:  
- **\( |A ∩ B| \)** is the number of words **common** to both documents.  
- **\( |A ∪ B| \)** is the **total number of unique words** across both documents.  

---

## **📂 Example Input**
We consider **three sample text documents** stored in a directory.

### **doc1.txt**
```
doc1 hadoop is a distributed system
```

### **doc2.txt**
```
doc2 hadoop is used for big data processing
```

### **doc3.txt**
```
doc3 big data is important for analysis
```

---

## **📈 Expected Output**
The output should display the Jaccard Similarity between **each document pair**, formatted as:

```
(doc1, doc2) Similarity: 0.20
(doc1, doc3) Similarity: 0.16
(doc2, doc3) Similarity: 0.25
```

---

## **🛠️ Approach and Implementation**
### **📚 Mapper (`DocumentSimilarityMapper.java`)**
- **Reads each document**, extracts **unique words**, and emits `(document, word)` pairs.
- **Removes duplicate words** per document using a `HashSet`.
- **Emits key-value pairs** where the key is the document name and the value is a word.

### **📚 Reducer (`DocumentSimilarityReducer.java`)**
- **Collects all words for each document** into a HashMap.
- **Computes Jaccard Similarity** for all document pairs:
  - **Intersection**: Words common in both documents.
  - **Union**: All unique words across both documents.
  - **Jaccard Similarity** = `|A ∩ B| / |A ∪ B|`
- **Emits the document pair and similarity score**.

### **📚 Driver (`DocumentSimilarityDriver.java`)**
- **Sets up and runs the MapReduce job**.
- **Configures Mapper and Reducer classes**.
- **Handles file input/output in Hadoop Distributed File System (HDFS)**.

---

## **⚙️ Environment Setup**
### **Step 1: Start Hadoop in Docker**
```sh
docker-compose up -d
docker exec -it namenode /bin/bash
```

### **Step 2: Build the Project**
```sh
mvn clean package
```
Copy the **JAR file** to the Namenode container:
```sh
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar namenode:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

---

## **📂 Uploading Data to HDFS**
### **Step 3: Copy Input Files to Namenode**
```sh
docker cp input/doc1.txt namenode:/opt/hadoop-3.2.1/share/hadoop/input/doc1.txt
docker cp input/doc2.txt namenode:/opt/hadoop-3.2.1/share/hadoop/input/doc2.txt
docker cp input/doc3.txt namenode:/opt/hadoop-3.2.1/share/hadoop/input/doc3.txt
```

### **Step 4: Move Data to HDFS**
```sh
docker exec -it namenode /bin/bash -c "
hdfs dfs -mkdir -p /input &&
hdfs dfs -put /opt/hadoop-3.2.1/share/hadoop/input/* /input/ &&
hdfs dfs -ls /input"
```

---

## **🚀 Running the MapReduce Job**
### **Step 5: Execute MapReduce**
```sh
docker exec -it namenode /bin/bash -c "
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.mapreduce.DocumentSimilarityDriver /input /output"
```

### **Step 6: Handle Output Directory Errors (If Needed)**
If `/output` directory already exists, delete it:
```sh
docker exec -it namenode /bin/bash -c "hdfs dfs -rm -r /output"
```
Then re-run the job.

---

## **📊 Retrieving and Storing Output**
### **Step 7: Check Output on Namenode**
```sh
docker exec -it namenode /bin/bash -c "hdfs dfs -cat /output/part-r-00000"
```

### **Step 8: Copy Output to Local Machine**
```sh
docker exec -it namenode /bin/bash -c "
hdfs dfs -get /output /opt/hadoop-3.2.1/share/hadoop/output"
```

Then transfer it from Namenode to **local project directory (`DocumentSimilarity/output/`)**:
```sh
docker cp namenode:/opt/hadoop-3.2.1/share/hadoop/output/ output/
```

---

## **🛠️ Challenges Faced & How I Overcame Them**
### **1️⃣ Issue: Output showed words instead of similarity scores**
- **Solution:** Reimplemented Reducer to compute Jaccard Similarity properly.

### **2️⃣ Issue: Output directory error in HDFS**
- **Solution:** Used `hdfs dfs -rm -r /output` before running the job.

### **3️⃣ Issue: Input files missing inside Namenode**
- **Solution:** Created `/opt/hadoop-3.2.1/share/hadoop/input/` manually before copying files.

---



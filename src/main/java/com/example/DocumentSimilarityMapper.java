package com.example;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.*;

public class DocumentSimilarityMapper extends Mapper<LongWritable, Text, Text, Text> {

    private Text documentId = new Text();
    private Text words = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;

        // Extract document ID (first word) and document content (rest of the line)
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2) return; // Skip invalid lines

        String docName = parts[0].trim();  // Document ID
        String docContent = parts[1].trim();  // Document Content

        documentId.set(docName);
        Set<String> uniqueWords = new HashSet<>();

        // Tokenize words and clean them
        StringTokenizer tokenizer = new StringTokenizer(docContent);
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (!word.isEmpty()) {
                uniqueWords.add(word);
            }
        }

        // Emit (DocumentID, word list)
        if (!uniqueWords.isEmpty()) {
            words.set(String.join(",", uniqueWords));
            context.write(documentId, words);
            System.out.println("Mapper Output: " + documentId.toString() + " -> " + words.toString());
        }
    }
}

package com.example;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, DoubleWritable> {

    private static List<String> documentNames = new ArrayList<>();
    private static List<Set<String>> documentWordSets = new ArrayList<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            documentNames.add(key.toString()); // Store actual document ID
            Set<String> wordSet = new HashSet<>(Arrays.asList(value.toString().split(",")));
            documentWordSets.add(wordSet);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Compare each document with every other document
        for (int i = 0; i < documentWordSets.size(); i++) {
            for (int j = i + 1; j < documentWordSets.size(); j++) {
                Set<String> doc1 = documentWordSets.get(i);
                Set<String> doc2 = documentWordSets.get(j);

                Set<String> intersection = new HashSet<>(doc1);
                intersection.retainAll(doc2);

                Set<String> union = new HashSet<>(doc1);
                union.addAll(doc2);

                double similarity = (double) intersection.size() / union.size();
                String docPair = documentNames.get(i) + " & " + documentNames.get(j);

                if (similarity > 0.0) { // Ensure non-zero results
                    context.write(new Text(docPair), new DoubleWritable(similarity));
                    System.out.println("Reducer Output: " + docPair + " -> " + similarity);
                }
            }
        }
    }
}

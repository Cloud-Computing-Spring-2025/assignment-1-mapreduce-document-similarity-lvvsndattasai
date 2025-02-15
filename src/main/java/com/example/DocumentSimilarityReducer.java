package com.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    private HashMap<String, HashSet<String>> docWordMap = new HashMap<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        HashSet<String> wordSet = new HashSet<>();
        for (Text word : values) {
            wordSet.add(word.toString());
        }
        docWordMap.put(key.toString(), wordSet);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        for (Map.Entry<String, HashSet<String>> entryA : docWordMap.entrySet()) {
            for (Map.Entry<String, HashSet<String>> entryB : docWordMap.entrySet()) {
                if (!entryA.getKey().equals(entryB.getKey())) {
                    double similarity = computeJaccardSimilarity(entryA.getValue(), entryB.getValue());
                    String pair = "(" + entryA.getKey() + ", " + entryB.getKey() + ")";
                    context.write(new Text(pair), new Text("Similarity: " + String.format("%.2f", similarity)));
                }
            }
        }
    }

    private double computeJaccardSimilarity(HashSet<String> setA, HashSet<String> setB) {
        HashSet<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);

        HashSet<String> union = new HashSet<>(setA);
        union.addAll(setB);

        return (double) intersection.size() / union.size();
    }
}

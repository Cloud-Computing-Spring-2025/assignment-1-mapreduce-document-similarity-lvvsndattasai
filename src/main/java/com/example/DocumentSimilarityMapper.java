package com.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {
    private Text word = new Text();
    private Text document = new Text();

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] parts = value.toString().split("\t", 2);
        if (parts.length < 2) return;

        String docName = parts[0]; // Extract document name
        String content = parts[1];

        HashSet<String> uniqueWords = new HashSet<>();
        StringTokenizer tokenizer = new StringTokenizer(content);
        while (tokenizer.hasMoreTokens()) {
            uniqueWords.add(tokenizer.nextToken().toLowerCase());
        }

        for (String token : uniqueWords) {
            word.set(token);
            document.set(docName);
            context.write(document, word);
        }
    }
}

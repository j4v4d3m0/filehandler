package com.github.javademo.filehandler.transform;

import static java.util.Collections.reverseOrder;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Stream;
import com.github.javademo.filehandler.exception.FilehandlerRuntimeException;

public class WordpairCounterFileCreator implements Creator {

  private int wordpairNumberLimit;

  public WordpairCounterFileCreator withWordpairNumberLimit(int wordpairNumberLimit) {
    this.wordpairNumberLimit = wordpairNumberLimit;
    return this;
  }

  @Override
  public Object create(File file) {
    try (Scanner scanner = new Scanner(file).useDelimiter("\\s")) {
      return createContent(scanner).toString();
    } catch (FileNotFoundException e) {
      throw new FilehandlerRuntimeException(e);
    }
  }

  private StringBuilder createContent(Scanner scanner) {
    StringBuilder content = new StringBuilder();
    filteredSortedStream(wordpairEntryStream(scanner)).forEach(v -> addLine(content, v));
    return content;
  }

  private Stream<Entry<String, Integer>> wordpairEntryStream(Scanner scanner) {
    return fillWordPairMap(scanner).entrySet().stream();
  }

  private Map<String, Integer> fillWordPairMap(Scanner scanner) {
    Map<String, Integer> wordpairs = new HashMap<>();
    String prevWord = null;
    while (scanner.hasNext()) {
      prevWord = putNextWordpairToMap(wordpairs, scanner, prevWord);
    }
    return wordpairs;
  }

  private String putNextWordpairToMap(
      Map<String, Integer> words, Scanner scanner, String prevWord) {
    String word = scanner.next().toLowerCase();
    if (prevWord != null) {
      String wordpair = prevWord + " " + word;
      Integer num = words.getOrDefault(wordpair, 0);
      words.put(wordpair, ++num);
    }
    return word;
  }

  private Stream<Map.Entry<String, Integer>> filteredSortedStream(
      Stream<Map.Entry<String, Integer>> stream) {
    return filteredStream(stream).sorted(reverseOrder(Map.Entry.comparingByValue()));
  }

  private Stream<Entry<String, Integer>> filteredStream(Stream<Map.Entry<String, Integer>> stream) {
    return stream.filter(i -> i.getValue() > wordpairNumberLimit);
  }

  private void addLine(StringBuilder out, Entry<String, Integer> v) {
    out.append(v.getKey()).append(" : ").append(v.getValue()).append("\n");
  }
}

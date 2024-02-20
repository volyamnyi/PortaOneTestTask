import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Task {
    public static void main(String[] args) {
        Instant start = Instant.now();

        List<Double> numbers = fromFileToDoubleList("10m.txt");

        List<Integer> largestIncreasingSequence = findLargestIncreasingSequence(numbers);

        List<Integer> largestDecreasingSequence = findLargestDecreasingSequence(numbers);

        numbers.sort((a, b) -> Double.compare(a.compareTo(b), 0));

        printData(numbers, largestIncreasingSequence, largestDecreasingSequence);

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toSeconds();

        System.out.println("Time Elapsed: " + timeElapsed + " seconds");
    }

    static double getMaxValueOfSorted(List<Double> numbers) {
        return numbers.get(numbers.size() - 1);
    }

    static double getMinValueOfSorted(List<Double> numbers) {
        return numbers.get(0);
    }

    static double getAverageValue(List<Double> numbers) {
        return numbers.stream().reduce(0.0, Double::sum) / numbers.size();
    }

    static double getMedian(List<Double> numbers) {
        int length = numbers.size();

        return length % 2 == 1 ? numbers.get(length / 2) :
                getAverageValue(List.of(numbers.get(length / 2), numbers.get((length / 2) - 1)));
    }

    public static List<Integer> findLargestIncreasingSequence(List<Double> numbers) {
        return findLargestSequence(numbers, true);
    }

    public static List<Integer> findLargestDecreasingSequence(List<Double> numbers) {
        return findLargestSequence(numbers, false);
    }

    public static List<Integer> findLargestSequence(List<Double> numbers, boolean order) {
        int numbersSize = numbers.size();
        int[] predecessors = new int[numbersSize];
        int[] indices = new int[numbersSize + 1];
        int longestIncreasingSubsequenceLength = 0;

        for (int i = 0; i < numbersSize; i++) {
            // Binary search for the largest positive integer j ≤ L
            // such that numbers[indices[j]] < numbers[i]
            int left = 1;
            int right = longestIncreasingSubsequenceLength;

            while (left <= right) {
                int mid = (left + right) / 2;
                //Depends on order value choosing the order
                if (order) {
                    if (numbers.get(indices[mid]) < numbers.get(i)) {
                        left = mid + 1;
                    } else {
                        right = mid - 1;
                    }
                } else {
                    if (numbers.get(indices[mid]) >= numbers.get(i)) {
                        left = mid + 1;
                    } else {
                        right = mid - 1;
                    }
                }
            }

            // After searching, left is 1 more than
            // length of the longest prefix numbers[i]
            int longestPrefixLength = left;

            // The predecessor numbers[i] is the last index
            // of a subsequence of length newL-1
            // The predecessor of numbers[i] is the last index of
            // the subsequence of length longestPrefixLength-1
            predecessors[i] = indices[longestPrefixLength - 1];
            indices[longestPrefixLength] = i;

            if (longestPrefixLength > longestIncreasingSubsequenceLength) {
                // If we find a subsequence that is longer
                // than any of the ones we found, we will update longestIncreasingSubsequenceLength
                longestIncreasingSubsequenceLength = longestPrefixLength;
            }
        }

        // Reconstruct the longest growing subsequence
        List<Integer> result = new ArrayList<>();

        int index = indices[longestIncreasingSubsequenceLength];

        while (index != 0) {
            result.add(0, (int) Math.round(numbers.get(index)));
            index = predecessors[index];
        }

        // Add the first element from the list to the beginning if it is larger than the current element
        // at the beginning depends on order value
        if (order) {
            if (result.get(0) > (int) Math.round(numbers.get(0))) {
                result.add(0, (int) Math.round(numbers.get(0)));
            }
        } else {
            if (result.get(0) < (int) Math.round(numbers.get(0))) {
                result.add(0, (int) Math.round(numbers.get(0)));
            }
        }

        return result;
    }

    static void printData(List<Double> numbers,
                          List<Integer> largestIncreasingSequence,
                          List<Integer> largestDecreasingSequence) {

        System.out.println("The Max Value: " + (int) getMaxValueOfSorted(numbers));
        System.out.println("The Min Value: " + (int) getMinValueOfSorted(numbers));
        System.out.println("The Average Value: " + getAverageValue(numbers));
        System.out.println("The Median Value: " + (int) getMedian(numbers));
        System.out.println("The Largest Increasing Sequence " + largestIncreasingSequence);
        System.out.println("The Largest Decreasing Sequence " + largestDecreasingSequence);
    }

    static List<Double> fromFileToDoubleList(String path) {
        try (Stream<String> lineStream = Files.lines(Paths.get(path))) {

            return lineStream.map(line -> Double
                            .parseDouble(line.trim()))
                    .collect(Collectors.toList());

        } catch (IOException ignored) {
        }

        return new ArrayList<>();
    }

}

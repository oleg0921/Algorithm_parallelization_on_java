import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays;

class WaveProcessor {
    private static void calculateSum(int[] arr, int start, int end, int[] result) {
        while (start < end) {
            int pairSum = arr[start] + arr[end];
            result[start] = pairSum;
            start++;
            end--;
        }
    }

    private static void processWave(int[] arr, int[] result, int waveNumber) {
        int length = arr.length;
        while (length > 1) {
            int numThreads = length / 2;
            Thread[] threads = new Thread[numThreads];

            for (int i = 0; i < numThreads; i++) {
                final int index = i;
                int[] finalArr = arr;
                int finalLength = length;
                threads[i] = new Thread(() -> calculateSum(finalArr, index, finalLength - index - 1, result));
                threads[i].start();
            }

            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Хвиля " + waveNumber + ":");
            System.out.print("пари - ");
            for (int i = 0; i < numThreads; i++) {
                System.out.print(arr[i] + " + " + arr[length - i - 1]);
                if (i < numThreads - 1) {
                    System.out.print("; ");
                }
            }
            System.out.println(";");
            System.out.println("Результат - " + Arrays.toString(arr));
            System.out.println("----------------------------------------");

            if (length % 2 != 0) {
                result[numThreads] = arr[numThreads]; // залишити середній елемент незмінним
                numThreads++;
            }

            arr = Arrays.copyOf(result, numThreads);
            length = arr.length;
            waveNumber++;
        }

        System.out.println("Тривалість хвиль: " + waveNumber);
        System.out.println("Сума елементів масиву: " + arr[0]);
    }

    public static void main(String[] args) {
        int[] inputArray = {1, 2, 3, 4, 5, 6};
        int wave = 1;

        processWave(inputArray, new int[inputArray.length / 2 + 1], wave);
    }
}

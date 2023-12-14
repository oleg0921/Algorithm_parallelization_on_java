import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main (String[] args) {

        int[] array = {1, 2, 3, 4, 5, 6};

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);

        int[] currentArray = array;

        while (currentArray.length > 1) {
            SumSync sumSync = new SumSync(currentArray.length / 2);

            for (int i = 0; i < availableProcessors; i++) {
                executorService.execute(new SumCalculator(currentArray, i, sumSync));
            }

            try {
                sumSync.awaitCompletion();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            currentArray = sumSync.getResultArray();
        }

        executorService.shutdown();

        int result = currentArray[0];
        System.out.println("Сума елементів масиву: " + result);

    }
}


class SumSync {
    private final int[] resultArray;
    private int count;

    public SumSync(int size) {
        this.resultArray = new int[size];
    }

    public synchronized void addResult(int result) {
        resultArray[count++] = result;
    }

    public int[] getResultArray() {
        return resultArray;
    }

    public synchronized void awaitCompletion() throws InterruptedException {
        while (count < resultArray.length) {
            wait();
        }
    }

    public synchronized void signalCompletion() {
        notifyAll();
    }
}

class SumCalculator implements Runnable {
    private final int[] array;
    private int index;
    private final SumSync sumSync;

    public SumCalculator(int[] array, int index, SumSync sumSync) {
        this.array = array;
        this.index = index;
        this.sumSync = sumSync;
    }

    @Override
    public void run() {
        int length = array.length;
        int symmetricIndex = length - index - 1;

        while (index < symmetricIndex) {
            int sum = array[index] + array[symmetricIndex];

            // Збереження результату
            sumSync.addResult(sum);

            index += length;
            symmetricIndex -= length;
        }

        // Сигнал про завершення роботи потока
        sumSync.signalCompletion();
    }
}
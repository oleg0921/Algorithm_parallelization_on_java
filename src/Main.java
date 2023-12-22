import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Main {
    public static void main (String[] args) {

        int[] array = {1, 2, 3, 4, 5, 6};
        calculateParallelArraySum(array);
    }


    public static void calculateParallelArraySum(int[] array) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while (array.length > 1) {
            int newIterationArrayLength = (array.length % 2 == 0) ? array.length / 2 : (array.length / 2) + 1;


            int[] results = new int[newIterationArrayLength];

            CountDownLatch latch = new CountDownLatch(newIterationArrayLength);
            for (int i = 0; i < newIterationArrayLength; i++) {

                int[] finalArray = array;
                int finalI = i;

                executorService.execute(() -> {
                   new SymSync(finalArray, newIterationArrayLength, finalI, results, latch).run();
                    latch.countDown();

                });
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("проміжний результат обрахунку : " + Arrays.toString(results));
            array = results;
        }

        executorService.shutdown();
        System.out.println("фінальний результат : " + array[0]);
    }

}

class SymSync implements Runnable{

    private  int[] actualArray;

    private int newIterationArrayLength;

    private int index;

    private  int[] results;


    private CountDownLatch latch;



    SymSync (int[] actualArray, int newIterationArrayLength, int index, int[] results, CountDownLatch latch) {
        this.actualArray = actualArray;
        this.newIterationArrayLength = newIterationArrayLength;
        this.index = index;
        this.results = results;
        this.latch = latch;

    }


    @Override
    public void run () {

         System.out.println(Thread.currentThread().getName() + " started work");
        final int index2 = actualArray.length - 1 - index;

        int value = actualArray[index] + ((index2 >= newIterationArrayLength) ? actualArray[index2] : 0);
        int finalIndex = index;
        results[finalIndex] = value;
        System.out.println(finalIndex  + "- finalIndex  " + Arrays.toString(results));

    }
}
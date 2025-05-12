package MonteCarloPI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonteCarloPi {

    static final long NUM_POINTS = 50_000_000L;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    static Lock lock = new ReentrantLock();
    static long totalInsideCircle = 0;
    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        // Without Threads
        System.out.println("Single threaded calculation started: ");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single threads): " + (endTime - startTime) / 1_000_000 + " ms");

        // With Threads
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n",NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (Multi-threaded): " + piWithThreads);
        System.out.println("Time taken (Multi-threaded): " + (endTime - startTime) / 1_000_000 + " ms");

        // TODO: After completing the implementation, reflect on the questions in the description of this task in the README file
        //       and include your answers in your report file.

        long singleTime = (endTime - startTime) / 1_000_000;

        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n",NUM_THREADS);
        startTime = System.nanoTime();
        endTime = System.nanoTime();
        long multiTime = (endTime - startTime) / 1_000_000;

        exportToCSV(piWithoutThreads, singleTime, piWithThreads, multiTime, "monteCarloResults.csv");

    }

    // Monte Carlo Pi Approximation without threads
    public static double estimatePiWithoutThreads(long numPoints)
    {
        long InsideCirclePoints = 0;
        for(long i = 0 ; i < numPoints ; i++){
            double x = Math.random();
            double y = Math.random();
            if(Math.pow(x , 2.0)+Math.pow(y , 2.0) <= 1.0){
                InsideCirclePoints++;
            }
        }

        // TODO: Implement this method to calculate Pi using a single thread
        return (InsideCirclePoints * 4.0) /numPoints;
    }


    // Monte Carlo Pi Approximation with threads
    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException, ExecutionException
    {


        // TODO: Implement this method to calculate Pi using multiple threads

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        Long pointsPerThread = numPoints /numThreads;


        for (int i = 0; i < numThreads; i++) {
            executor.execute(new MonteCarloTask(pointsPerThread));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        return (totalInsideCircle * 4.0) / numPoints;

        // HINT: You may need to create a variable to *safely* keep track of points that fall inside the circle
        // HINT: Each thread should generate and process a subset of the total points

        // TODO: After submitting all tasks, shut down the executor to prevent new tasks
        // TODO: wait for the executor to be fully terminated
        // TODO: Calculate and return the final estimation of Pi
    }
    static class MonteCarloTask implements Runnable {
        private final long numPoints;

        public MonteCarloTask(long numPoints) {
            this.numPoints = numPoints;
        }

        @Override
        public void run() {
            long insidePoints = 0;
            for (long i = 0; i < numPoints; i++) {
                double x = Math.random();
                double y = Math.random();
                if (x * x + y * y <= 1.0) {
                    insidePoints++;
                }
            }

            lock.lock();
            try {
                totalInsideCircle += insidePoints;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void exportToCSV(double piSingleThread , long timeSingleThread , double piMultiThreads , long timeMultiThreads , String fileName){
        boolean fileExists = new java.io.File(fileName).exists();

        try(FileWriter writer = new FileWriter(fileName, true)){
            if(!fileExists) {
                writer.append("Method,Estimated Pi,Time (ms)\n");
            }
            writer.append("Single Thread,").append(String.valueOf(piSingleThread)).append(",").append(String.valueOf(timeSingleThread)).append("\n");
            writer.append("Multi Thread,").append(String.valueOf(piMultiThreads)).append(",").append(String.valueOf(timeMultiThreads)).append("\n");
            System.out.println("Results exported to " +fileName);
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());

        }
    }
}
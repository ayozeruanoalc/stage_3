package com.guanchedata;

import com.guanchedata.measurement_metrics.IngestionRate;
import com.guanchedata.measurement_metrics.RecoveryTime;
import com.guanchedata.measurement_metrics.IndexingThroughput;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        String mode = System.getenv("BENCHMARK_MODE");

        if (mode == null || mode.isBlank()) {
            mode = "recovery";
            System.out.println(">>> No BENCHMARK_MODE found. Defaulting to: " + mode);
        }

        System.out.println("==========================================");
        System.out.println("   STARTING BENCHMARK: " + mode.toUpperCase());
        System.out.println("==========================================");

        switch (mode.toLowerCase().trim()) {
            case "ingestion":
                IngestionRate.main(args);
                break;
            case "indexing":
                IndexingThroughput.main(args);
                break;
            case "recovery":
                RecoveryTime.main(args);
                break;
            default:
                System.err.println("!!! Unknown mode: " + mode);
                System.err.println("Available modes: ingestion, indexing, recovery");
                System.exit(1);
        }
    }
}
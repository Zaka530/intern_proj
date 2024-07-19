package uz.kamron.springcourse.intern_proj.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final ScheduledExecutorService scheduler;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition limitReset = lock.newCondition();
    private int requestCount = 0;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.scheduler = Executors.newScheduledThreadPool(1);
        startLimitResetTask();
    }

    private void startLimitResetTask() {
        long period = timeUnit.toMillis(1);
        scheduler.scheduleAtFixedRate(() -> {
            lock.lock();
            try {
                requestCount = 0;
                limitReset.signalAll();
            } finally {
                lock.unlock();
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    public void createDocument(Document document, String signature) throws InterruptedException, IOException {
        lock.lock();
        try {
            while (requestCount >= requestLimit) {
                limitReset.await();
            }
            requestCount++;
        } finally {
            lock.unlock();
        }

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(document);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                .header("Content-Type", "application/json")
                .header("Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response: " + response.body());
    }

    @Data
    @NoArgsConstructor
    public static class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type = "LP_INTRODUCE_GOODS";
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private Product[] products;
        private String reg_date;
        private String reg_number;

        @Data
        @NoArgsConstructor
        public static class Description {
            private String participantInn;
        }

        @Data
        @NoArgsConstructor
        public static class Product {
            private String certificate_document;
            private String certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private String production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;
        }
    }

    public static void main(String[] args) {
        CrptApi api = new CrptApi(TimeUnit.SECONDS, 5);

        Document document = new Document();
        Document.Description description = new Document.Description();
        description.setParticipantInn("1234567890");
        document.setDescription(description);
        document.setDoc_id("1234");
        document.setDoc_status("status");
        document.setImportRequest(true);
        document.setOwner_inn("ownerInn");
        document.setParticipant_inn("participantInn");
        document.setProducer_inn("producerInn");
        document.setProduction_date("2023-01-01");
        document.setProduction_type("type");
        document.setProducts(new Document.Product[]{new Document.Product()});
        document.setReg_date("2023-01-01");
        document.setReg_number("reg123");

        try {
            api.createDocument(document, "your_signature_here");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}

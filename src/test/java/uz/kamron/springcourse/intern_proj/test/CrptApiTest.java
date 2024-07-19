package uz.kamron.springcourse.intern_proj.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.kamron.springcourse.intern_proj.api.CrptApi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CrptApiTest {
    private CrptApi crptApi;

    @BeforeEach
    public void setUp() {
        crptApi = new CrptApi(TimeUnit.SECONDS, 5);
    }

    @Test
    public void testCreateDocument() {
        CrptApi.Document document = new CrptApi.Document();
        CrptApi.Document.Description description = new CrptApi.Document.Description();
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
        document.setProducts(new CrptApi.Document.Product[]{new CrptApi.Document.Product()});
        document.setReg_date("2023-01-01");
        document.setReg_number("reg123");

        assertDoesNotThrow(() -> crptApi.createDocument(document, "your_signature_here"));
    }
}

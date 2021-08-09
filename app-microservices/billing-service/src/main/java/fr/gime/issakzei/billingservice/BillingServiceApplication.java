package fr.gime.issakzei.billingservice;

import fr.gime.issakzei.billingservice.dto.InvoiceRequestDTO;
import fr.gime.issakzei.billingservice.services.InvoiceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(BillingServiceApplication.class, args);
    }
@Bean
    CommandLineRunner  commandLineRunner(InvoiceService invoiceService){
        return args -> {
            invoiceService.save(new InvoiceRequestDTO(BigDecimal.valueOf(2340000), "C1"));
            invoiceService.save(new InvoiceRequestDTO(BigDecimal.valueOf(4320000), "C2"));
            invoiceService.save(new InvoiceRequestDTO(BigDecimal.valueOf(2420000), "C3"));
            invoiceService.save(new InvoiceRequestDTO(BigDecimal.valueOf(890000), "C4"));
        };
}
}

package fr.gime.issakzei.billingservice.web;

import fr.gime.issakzei.billingservice.dto.InvoiceRequestDTO;
import fr.gime.issakzei.billingservice.dto.InvoiceResponseDTO;
import fr.gime.issakzei.billingservice.services.InvoiceService;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class InvoiceRestController {
    private InvoiceService invoiceService;

    public InvoiceRestController(InvoiceService invoiceService){
        this.invoiceService= invoiceService;
    }

    @PostMapping(path = "/invoices")
    public InvoiceResponseDTO save(@RequestBody InvoiceRequestDTO invoiceRequestDTO){
        return  invoiceService.save(invoiceRequestDTO);
    }

    @GetMapping(path = "/invoices/{id}")
    public InvoiceResponseDTO getInvoice(@PathVariable (name= "id") String invoiceId){
        return  invoiceService.getInvoice(invoiceId);
    }

    @GetMapping(path = "/invoicesByCustomer/{customerId}")
    public List<InvoiceResponseDTO> getInvoiceByCustomer(@PathVariable  String customerId){
        return  invoiceService.invoiceByCustomerId(customerId);
    }

    @GetMapping(path = "/invoices")
    public List<InvoiceResponseDTO> getAllInvoices(){
        return  invoiceService.allInvoices();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e){
       return new ResponseEntity<>( e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

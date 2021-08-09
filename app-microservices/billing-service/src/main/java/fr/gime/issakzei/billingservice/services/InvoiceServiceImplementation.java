package fr.gime.issakzei.billingservice.services;

import fr.gime.issakzei.billingservice.dto.InvoiceRequestDTO;
import fr.gime.issakzei.billingservice.dto.InvoiceResponseDTO;
import fr.gime.issakzei.billingservice.entities.Customer;
import fr.gime.issakzei.billingservice.entities.Invoice;
import fr.gime.issakzei.billingservice.exceptions.CustomerException;
import fr.gime.issakzei.billingservice.mappers.InvoiceMapper;
import fr.gime.issakzei.billingservice.openfeign.CustomerRestClient;
import fr.gime.issakzei.billingservice.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImplementation implements  InvoiceService{
    @Autowired
    private InvoiceRepository invoiceRepository;

    private InvoiceMapper invoiceMapper;
    private CustomerRestClient customerRestClient;

    public InvoiceServiceImplementation(InvoiceRepository invoiceRepository,
                                        InvoiceMapper invoiceMapper,
                                        CustomerRestClient customerRestClient){
        this.invoiceRepository= invoiceRepository;
        this.invoiceMapper= invoiceMapper;
        this.customerRestClient = customerRestClient;
    }
    @Override
    public InvoiceResponseDTO save(InvoiceRequestDTO invoiceRequestDTO) {
        Customer customer=null;
        try{
           customer= customerRestClient.getCustomer(invoiceRequestDTO.getCustomerId());

        }
        catch (Exception e){
            throw  new CustomerException("Customer not Found");

        }
        Invoice invoice= invoiceMapper.fromInvoiceRequestDTOToInvoice(invoiceRequestDTO);
        invoice.setId(UUID.randomUUID().toString());
        invoice.setDate(new Date());
        /* Verification de l'integrité refe  Invoice/Customer */

        Invoice saveInvoice= invoiceRepository.save(invoice);
        saveInvoice.setCustomer(customer);
        return invoiceMapper
                .fromInvoiceToInvoiceRequestDTO(saveInvoice);
    }

    @Override
    public InvoiceResponseDTO getInvoice(String invoiceId) {
       Invoice invoice= invoiceRepository.findById(invoiceId).get();
       Customer customer= customerRestClient.getCustomer(invoice.getCustomerId());
       invoice.setCustomer(customer);
        return invoiceMapper.fromInvoiceToInvoiceRequestDTO(invoice);
    }

    @Override
    public List<InvoiceResponseDTO> invoiceByCustomerId(String customerId) {
        List<Invoice> invoices = invoiceRepository.
                findByCustomerId(customerId);

        for(Invoice invoice:invoices ){
            //Recuperation du customer à partir d l'api
            Customer customer= customerRestClient.getCustomer(invoice.getCustomerId());
            //Ajout de ce customer à la liste
            invoice.setCustomer(customer);

        }
        return invoices.stream().
                map(invoice->invoiceMapper.fromInvoiceToInvoiceRequestDTO(invoice))
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceResponseDTO> allInvoices() {
        List<Invoice>  allInvoices= invoiceRepository.findAll();

        for(Invoice invoice:allInvoices  ){
            //Recuperation du customer à partir d l'api
            Customer customer= customerRestClient.getCustomer(invoice.getCustomerId());
            //Ajout de ce customer à la liste
            invoice.setCustomer(customer);

        }

               return allInvoices.stream().map(invoices->invoiceMapper.
                fromInvoiceToInvoiceRequestDTO(invoices)).collect(Collectors.toList());


    }
}

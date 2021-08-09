package fr.gime.issakzei.billingservice.services;

import fr.gime.issakzei.billingservice.dto.InvoiceRequestDTO;
import fr.gime.issakzei.billingservice.dto.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {

    InvoiceResponseDTO save(InvoiceRequestDTO invoiceRequestDTO);

    InvoiceResponseDTO getInvoice(String invoiceId);

    List<InvoiceResponseDTO> invoiceByCustomerId(String customerId);

    //Liste de tous les les invoices
    List<InvoiceResponseDTO> allInvoices();

}

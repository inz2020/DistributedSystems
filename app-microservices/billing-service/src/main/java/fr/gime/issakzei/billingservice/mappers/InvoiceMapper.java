package fr.gime.issakzei.billingservice.mappers;

import fr.gime.issakzei.billingservice.dto.InvoiceRequestDTO;
import fr.gime.issakzei.billingservice.dto.InvoiceResponseDTO;
import fr.gime.issakzei.billingservice.entities.Invoice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    //methode retournant un objet Invoice
    Invoice fromInvoiceRequestDTOToInvoice(InvoiceRequestDTO invoiceRequestDTO);

    //methode retournant un objett InvoiceResponseDTO
    InvoiceResponseDTO fromInvoiceToInvoiceRequestDTO(Invoice invoice);
}

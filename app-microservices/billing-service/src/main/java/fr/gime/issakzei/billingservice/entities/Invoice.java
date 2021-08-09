package fr.gime.issakzei.billingservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Invoice {
     @Id
    private String id;

    private Date date;
    private BigDecimal amount;

    //Clé etrangère
    private String customerId;
    //Les attributs de la classe Customer ne sont pas persistents.
@Transient
    private  Customer customer;

}

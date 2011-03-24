package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("LendTransactionSystemComment")
@Audited
public class LendTransactionSystemComment extends LendTransactionComment implements SystemComment {

}

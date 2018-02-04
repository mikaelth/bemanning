package se.uu.ebc.bemanning.entity;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.EntityListeners;
import javax.persistence.Column;
import javax.persistence.TemporalType;
import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedBy
    @Column(name = "created_by")
    protected String createdBy;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", updatable = false)
    protected Date creationDate;

    @LastModifiedBy
    @Column(name = "modified_by")
    protected String lastModifiedBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modification_date")
    protected Date lastModifiedDate;


	/* Setters and getters */

    public String getLastModifiedBy()
    {
    	return this.lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy)
    {
    	this.lastModifiedBy = lastModifiedBy;
    }


	
}

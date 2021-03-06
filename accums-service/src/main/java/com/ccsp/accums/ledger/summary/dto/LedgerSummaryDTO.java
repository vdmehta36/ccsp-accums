package com.ccsp.accums.ledger.summary.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ccsp.common.dto.ICommonDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Vaibhav
 * DTO class for Accumulation Header
 *
 */
/**
 * @author vamehta
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({"id", "planId", "networkTier", "unitOfMeasure", "maxVisit"})
public class LedgerSummaryDTO  implements ICommonDTO {

	/**
	 * serialization
	 */
	private static final long serialVersionUID = 4325638042231113322L;

	private Long id;
	
	private String subscriberId;
	
	private String memberId;
	
	private Long planId;
		
	@NotNull(message = "AccumTypeName cannot be empty")
	private String accumType;
	
	@XmlElement(name="accumulation")
	@JsonProperty("accumulation")
	private Double amount;
	
	@XmlElement(name="network")
	@JsonProperty("network")
	private String networkCode;
	
	private String networkTier;
	
	private String unitOfMeasure;
	
	@XmlElement(name="limit")
	@JsonProperty("limit")
	private Double maxAmount;
	
	private Integer maxVisit;
	
	@JsonFormat(pattern = "MM/dd/yyyy")
	private Date effectiveDate;
	
	@JsonFormat(pattern = "MM/dd/yyyy")
	private Date endDate;

	/**
	 * Default constructor.
	 */
	public LedgerSummaryDTO() { }

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public String getSubscriberId() {
		return subscriberId;
	}

	/**
	 * @param subscriberID
	 */
	public void setSubscriberId(String subscriberID) {
		this.subscriberId = subscriberID;
	}

	/**
	 * 
	 * @return
	 */
	public String getMemberId() {
		return memberId;
	}

	/**
	 * 
	 * @param memberId
	 */
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	/**
	 * @return
	 */
	public Long getPlanId() {
		return planId;
	}

	/**
	 * @param planID
	 */
	public void setPlanId(Long planID) {
		this.planId = planID;
	}
	/**
	 * @return
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return
	 */
	public String getNetworkCode() {
		return networkCode;
	}

	/**
	 * @param network
	 */
	public void setNetworkCode(String network) {
		this.networkCode = network;
	}

	/**
	 * @return
	 */
	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	/**
	 * @param unitOfMeasure
	 */
	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	/**
	 * @return
	 */
	public Double getMaxAmount() {
		return maxAmount;
	}

	/**
	 * @param maxAmount
	 */
	public void setMaxAmount(Double maxAmount) {
		this.maxAmount = maxAmount;
	}

	/**
	 * @return
	 */
	public Integer getMaxVisit() {
		return maxVisit;
	}

	/**
	 * @param maxVisit
	 */
	public void setMaxVisit(Integer maxVisit) {
		this.maxVisit = maxVisit;
	}

	/**
	 * @return
	 */
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	/**
	 * @param effectiveDt
	 */
	public void setEffectiveDate(Date effectiveDt) {
		this.effectiveDate = effectiveDt;
	}

	
	/**
	 * @return
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDt
	 */
	public void setEndDate(Date endDt) {
		this.endDate = endDt;
	}
	
	/**
	 * @return the networkTier
	 */
	public String getNetworkTier() {
		return networkTier;
	}

	/**
	 * @param networkTier the networkTier to set
	 */
	public void setNetworkTier(String networkTier) {
		this.networkTier = networkTier;
	}

	/**
	 * @return the accumType
	 */
	public String getAccumType() {
		return accumType;
	}

	/**
	 * @param accumType the accumType to set
	 */
	public void setAccumType(String accumType) {
		this.accumType = accumType;
	}
}

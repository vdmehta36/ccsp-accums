package com.ccsp.accums.ledger.summary.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.ccsp.accums.ledger.entry.dto.LedgerEntryDTO;
import com.ccsp.accums.ledger.entry.entity.LedgerEntryEntity;
import com.ccsp.accums.ledger.entry.repository.LedgerEntryRepository;
import com.ccsp.accums.ledger.header.dto.LedgerHeaderDTO;
import com.ccsp.accums.ledger.header.entity.LedgerHeaderEntity;
import com.ccsp.accums.ledger.header.mapper.LedgerHeaderMapper;
import com.ccsp.accums.ledger.header.repository.ILedgerHeaderRepository;
import com.ccsp.accums.ledger.summary.dto.LedgerSummaryDTO;
import com.ccsp.accums.ledger.summary.entity.LedgerSummaryEntity;
import com.ccsp.accums.ledger.summary.mapper.LedgerSummaryMapper;
import com.ccsp.accums.ledger.summary.repository.ILedgerSummaryRepository;
import com.ccsp.common.mapper.IBaseMapper;
import com.ccsp.common.service.impl.CommonServiceImpl;

import javassist.NotFoundException;

/**
 * @author Vaibhav
 *
 */
@Component
public class LedgerSummaryService extends CommonServiceImpl<LedgerSummaryDTO, LedgerSummaryEntity> {
	/**
	 * Autowiring repository layer
	 */
	@Resource
	private ILedgerSummaryRepository ledgerSummaryRepository;

	@Resource
	private ILedgerHeaderRepository ledgerHeaderRepository;
	
	@Resource
	private LedgerEntryRepository ledgerEntryRepository;
	
	private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ccsp.common.service.impl.CommonServiceImpl#getMapper()
	 */
	@Override
	public IBaseMapper<LedgerSummaryDTO, LedgerSummaryEntity> getMapper() {
		return LedgerSummaryMapper.INSTANCE;
	}

	/**
	 * Creates the provided Ledger Entry. It expects ledger header id along with
	 * other attributes.
	 * 
	 * @param dto
	 * @return T
	 */
	@Override
	public LedgerSummaryDTO create(LedgerSummaryDTO dto) {
		//convert dto to entity for persistence
		LedgerSummaryEntity ledgerSummaryEntity = getMapper().convertToEntity(dto);
		
		//update the summary with the ledgerheader details and persist it
		ledgerSummaryEntity = ledgerSummaryRepository.save(ledgerSummaryEntity);
		return getMapper().convertToDTO(ledgerSummaryEntity);
	}

	/**
	 * Maps the data from provided DTO to database entity model.
	 * 
	 * Accumulates the amount with already persisted summary entities based on unique constraint key.
	 * 
	 * Invokes the repository to persist in database by passing the entity.
	 * 
	 * @param ledgerHeaderDTO
	 */
	public void create(LedgerHeaderDTO ledgerHeaderDTO) {
		//Holds the ledger summary entity details with key as combination of unique constraint fields.
		Map<String, LedgerSummaryEntity> ledgerSummaryMap = new HashMap<String, LedgerSummaryEntity>();
		
		//fetch the existing ledgerSummary entries for the given subscriber id
		List<LedgerSummaryEntity> ledgerSummaryEntities = ledgerSummaryRepository.findBySubscriberId(ledgerHeaderDTO.getSubscriberId());
		
		//iterate the ledgerSummary entries to populate the ledgerSummaryMap with unique constraints as the key and the summary object as the value
		if(CollectionUtils.isNotEmpty(ledgerSummaryEntities)) {
			for(LedgerSummaryEntity ledgerSummaryEntity : ledgerSummaryEntities) {
				ledgerSummaryMap.put(generateKey(ledgerSummaryEntity), ledgerSummaryEntity);
			}
		}
		
		//Iterate the service lines to update the summary table with the latest amount consumed under each accumulator
		for (LedgerEntryDTO ledgerEntryDTO : ledgerHeaderDTO.getServiceLines()) {
			LedgerSummaryMapper mapper = (LedgerSummaryMapper) getMapper();
			
			//Map details from the header DTO to summary entity.
			LedgerSummaryEntity entity = mapper.convertHeaderDTOtoEntity(ledgerHeaderDTO);
			entity.setAccumType(ledgerEntryDTO.getAccumType());
			
			//Check and get if summary entity is already available with unique constraint key.
			LedgerSummaryEntity result = ledgerSummaryMap.get(generateKey(entity));
			
			//Assign the new ledger summary entity, if not already available.
			if (result == null) {
				result = entity;
				result.setAmount(ledgerEntryDTO.getAmount());
			} else { 
				//Accumulate the amounts for the existing and newly provided summary of same accum type.
				result.setAmount(result.getAmount() + ledgerEntryDTO.getAmount());
			}
			ledgerSummaryEntities.add(result);
		}
		//persist the updated summary entities
		ledgerSummaryRepository.save(ledgerSummaryEntities);
	}

	/**
	 * Persists provided list of data. Populates the required entities like header
	 * entity and primary reportable entity.
	 * 
	 * @param ledgerEntries
	 * @return
	 */
	@Override
	public List<LedgerSummaryDTO> create(List<LedgerSummaryDTO> dtoList) {
		List<LedgerSummaryEntity> summaryEntities = getMapper().convertToEntityList(dtoList);

		//persist the summary entities
		getJPARepository().save(summaryEntities);
		
		return getMapper().convertToDTOList(summaryEntities);
	}

	@Override
	public JpaRepository<LedgerSummaryEntity, Long> getJPARepository() {
		// TODO Auto-generated method stub
		return ledgerSummaryRepository;
	}

	/**
	 * @param subscriberId
	 * Update the ledgerSummary table for the given summary id
	 */
	public void updateLedgerSummary(String memberId) {
		Map<String, LedgerSummaryEntity> ledgerSummaryMap = new HashMap<String, LedgerSummaryEntity>();
		//fetch the existing summary entities for the given subscriber id
		List<LedgerSummaryEntity> ledgerSummaryEntities = ledgerSummaryRepository.findByMemberId(memberId);
		//if there are existing summary entities set the amount to 0 as we are going to calculate the cummulative amount
		if(CollectionUtils.isNotEmpty(ledgerSummaryEntities)) {
			//iterate the ledgerSummary entries to populate the ledgerSummaryMap with unique constraints as the key and the summary object as the value
			for(LedgerSummaryEntity ledgerSummaryEntity : ledgerSummaryEntities) {
				ledgerSummaryEntity.setAmount(0d);
				ledgerSummaryMap.put(generateKey(ledgerSummaryEntity), ledgerSummaryEntity);
			}
		}
		//fetch the header entries based on the subscriber id
		List<LedgerHeaderEntity> ledgerHeaderEntityList = ledgerHeaderRepository.findByMemberId(memberId);
		
		//For each header entry get the corresponding ledger entries
		for(LedgerHeaderEntity ledgerHeaderEntity : ledgerHeaderEntityList) {
			LedgerHeaderMapper headerMapper = LedgerHeaderMapper.INSTANCE;
			LedgerHeaderDTO ledgerHeaderDTO = headerMapper.convertToDTO(ledgerHeaderEntity);
			List<LedgerEntryEntity> ledgerEntryList = ledgerEntryRepository.findByledgerHeaderID(ledgerHeaderEntity.getId());
			//iterate the ledger entries to find the cummulative amount for each accumator type for the subscriber id
			for(LedgerEntryEntity ledgerEntryEntity:ledgerEntryList) {
				LedgerSummaryMapper mapper = (LedgerSummaryMapper) getMapper();
				LedgerSummaryEntity entity = mapper.convertHeaderDTOtoEntity(ledgerHeaderDTO);
				entity.setAccumType(ledgerEntryEntity.getAccumType());
				LedgerSummaryEntity result = null;
				String key = generateKey(entity);
				result = ledgerSummaryMap.get(key);
				if (result != null) {
					result.setAmount(result.getAmount() + ledgerEntryEntity.getAmount());
				} else {
					result = entity;
					result.setAmount(ledgerEntryEntity.getAmount());
				}
				ledgerSummaryMap.put(key, result);
			}
		}
		ledgerSummaryEntities = new ArrayList<>();
		for(Map.Entry<String, LedgerSummaryEntity> map : ledgerSummaryMap.entrySet()) {
			ledgerSummaryEntities.add(map.getValue());
		}
		ledgerSummaryRepository.save(ledgerSummaryEntities);
	}
	
	/**
	 * Generates the key as {member_id}_{accum_type}_{network_code}_{network_tier}
	 * @param ledgerSummaryEntity
	 * @return
	 * Generate the key based on the unique fields in the ledgersummary
	 */
	private String generateKey(LedgerSummaryEntity ledgerSummaryEntity) {
		return ledgerSummaryEntity.getMemberId().trim()+"_"+ledgerSummaryEntity.getAccumType().trim()+"_"+ledgerSummaryEntity.getNetworkCode().trim()+"_"+ledgerSummaryEntity.getNetworkTier().trim();
	}

	/**
	 * Gets Benefit balance based on member or subscriber id
	 * @param subscriberID
	 * @param memberID
	 * @return
	 */
	public List<LedgerSummaryDTO> getBenefitBalance(String subscriberID, String memberID){
		List<LedgerSummaryEntity> ledgerSummaryEntities = null;
		if (!StringUtils.isEmpty(memberID) && !StringUtils.isEmpty(subscriberID)) {
			ledgerSummaryEntities = ledgerSummaryRepository.findByMemberIdAndSubscriberId(memberID, subscriberID);
		} else if (!StringUtils.isEmpty(memberID)) {
			ledgerSummaryEntities = ledgerSummaryRepository.findByMemberId(memberID);
		} else {
			ledgerSummaryEntities = ledgerSummaryRepository.findBySubscriberId(subscriberID);
		}
		// if the ledger summary entity list is not empty then convert entity to dto and
		// return dto list
		return getMapper().convertToDTOList(ledgerSummaryEntities);
	}
	
	/**
	 * This method fetches cumulative amount from ledger entry based on member id from ledger header. 
	 * Any previous summary records are deleted and new records are created based on above member id. 
	 * @param ledgerHeaderDTO
	 */
	public void createNew(LedgerHeaderDTO ledgerHeaderDTO) {
		
		//fetch the ledgerSummary entries from ledgerHeader and ledgerEntry by the given member id with accumulation
		List<LedgerSummaryEntity> ledgerSummaryEntities =  ledgerHeaderRepository.fetchAccumulation(ledgerHeaderDTO.getMemberId());
		
		//delete existing records in ledgerSummary
		for (LedgerEntryDTO ledgerEntryDTO : ledgerHeaderDTO.getServiceLines()) {
			ledgerSummaryRepository.deleteLedgerSummary(ledgerHeaderDTO.getMemberId(), ledgerEntryDTO.getAccumType(), ledgerHeaderDTO.getNetworkCode(), ledgerHeaderDTO.getNetworkTier());
		}
		
		//save ledgerSummary entries
		ledgerSummaryRepository.save(ledgerSummaryEntities);
	}

}


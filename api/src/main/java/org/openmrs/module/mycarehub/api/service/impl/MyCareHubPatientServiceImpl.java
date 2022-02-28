package org.openmrs.module.mycarehub.api.service.impl;

import com.google.gson.JsonObject;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mycarehub.api.db.MyCareHubPatientDao;
import org.openmrs.module.mycarehub.api.rest.mapper.PatientRegistrationRequest;
import org.openmrs.module.mycarehub.api.service.MyCareHubPatientService;
import org.openmrs.module.mycarehub.api.service.MyCareHubSettingsService;
import org.openmrs.module.mycarehub.model.MyCareHubSetting;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.mycarehub.utils.Constants.CCC_NUMBER_IDENTIFIER_TYPE_UUID;
import static org.openmrs.module.mycarehub.utils.Constants.MyCareHubSettingType.PATIENT_REGISTRATIONS;
import static org.openmrs.module.mycarehub.utils.Constants._PersonAttributeType.NEXT_OF_KIN_CONTACT;
import static org.openmrs.module.mycarehub.utils.Constants._PersonAttributeType.NEXT_OF_KIN_NAME;
import static org.openmrs.module.mycarehub.utils.Constants._PersonAttributeType.NEXT_OF_KIN_RELATIONSHIP;
import static org.openmrs.module.mycarehub.utils.Constants._PersonAttributeType.TELEPHONE_CONTACT;
import static org.openmrs.module.mycarehub.utils.MyCareHubUtil.getDefaultLocationMflCode;
import static org.openmrs.module.mycarehub.utils.MyCareHubUtil.uploadPatientRegistrationRecord;

public class MyCareHubPatientServiceImpl extends BaseOpenmrsService implements MyCareHubPatientService {
	
	MyCareHubPatientDao myCareHubPatientDao;
	
	MyCareHubPatientServiceImpl(MyCareHubPatientDao myCareHubPatientDao) {
		this.myCareHubPatientDao = myCareHubPatientDao;
	}
	
	public void syncPatientData() {
		uploadNewOrUpdatedPatientDemographics();
		fetchRegisteredPatientIdentifiers();
		uploadUpdatedPatientsMedicalRecords();
	}
	
	private void uploadNewOrUpdatedPatientDemographics() {
		MyCareHubSettingsService settingsService = Context.getService(MyCareHubSettingsService.class);
		MyCareHubSetting setting = settingsService.getMyCareHubSettingByType(PATIENT_REGISTRATIONS);
		
		if (setting != null) {
			List<Patient> patients = myCareHubPatientDao.getCccPatientsCreatedOrUpdatedSinceDate(setting.getLastSyncTime());
			MyCareHubSetting latestPatientRegistrationSetting = new MyCareHubSetting(PATIENT_REGISTRATIONS, new Date());
			settingsService.saveMyCareHubSettings(latestPatientRegistrationSetting);
			
			for (Patient patient : patients) {
				//compose payload - and send individually?
				PatientRegistrationRequest registrationRequest = new PatientRegistrationRequest();
				registrationRequest.setName(patient.getFamilyName() + patient.getGivenName());
				registrationRequest.setClientType("KenyaEMR");
				registrationRequest.setCounseled("false");
				registrationRequest.setGender(patient.getGender());
				
				registrationRequest.setFacility(getDefaultLocationMflCode());
				
				PatientIdentifierType cccIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(
				    CCC_NUMBER_IDENTIFIER_TYPE_UUID);
				registrationRequest.setCccNumber(patient.getPatientIdentifier(cccIdentifierType).getIdentifier());
				
				String pattern = "yyyy-MM-dd";
				SimpleDateFormat sf = new SimpleDateFormat(pattern);
				registrationRequest.setDateOfBirth(sf.format(patient.getBirthdate()));
				registrationRequest.setBirthdateEstimated(patient.getBirthdateEstimated());
				
				registrationRequest.setEnrollmentDate(sf.format(patient.getDateCreated()));
				
				PersonAttributeType phoneNumberAttributeType = Context.getPersonService().getPersonAttributeTypeByUuid(
				    TELEPHONE_CONTACT);
				if (phoneNumberAttributeType != null) {
					PersonAttribute phoneNumberAttribute = patient.getAttribute(phoneNumberAttributeType);
					if (phoneNumberAttribute != null) {
						registrationRequest.setPhoneNumber(phoneNumberAttribute.getValue());
					}
				}
				
				JsonObject nextOfKinJsonObject = new JsonObject();
				
				PersonAttributeType nextOfKinNameAttributeType = Context.getPersonService().getPersonAttributeTypeByUuid(
				    NEXT_OF_KIN_NAME);
				if (nextOfKinNameAttributeType != null) {
					PersonAttribute nextOfKinNamePersonAttribute = patient.getAttribute(nextOfKinNameAttributeType);
					if (nextOfKinNamePersonAttribute != null) {
						nextOfKinJsonObject.addProperty("next_of_kin_name", nextOfKinNamePersonAttribute.getValue());
					}
				}
				
				PersonAttributeType nextOfKinContactAttributeType = Context.getPersonService().getPersonAttributeTypeByUuid(
				    NEXT_OF_KIN_CONTACT);
				if (nextOfKinContactAttributeType != null) {
					PersonAttribute nextOfKinContactPersonAttribute = patient.getAttribute(nextOfKinContactAttributeType);
					if (nextOfKinContactPersonAttribute != null) {
						nextOfKinJsonObject.addProperty("contacts_of_next_of_kin",
						    nextOfKinContactPersonAttribute.getValue());
					}
				}
				
				PersonAttributeType nextOfKinRelationshipAttributeType = Context.getPersonService()
				        .getPersonAttributeTypeByUuid(NEXT_OF_KIN_RELATIONSHIP);
				if (nextOfKinRelationshipAttributeType != null) {
					PersonAttribute nextOfKinRelationshipPersonAttribute = patient
					        .getAttribute(nextOfKinRelationshipAttributeType);
					if (nextOfKinRelationshipPersonAttribute != null) {
						nextOfKinJsonObject.addProperty("relationship_to_next_of_kin",
						    nextOfKinRelationshipPersonAttribute.getValue());
					}
				}
				
				registrationRequest.setNextOfKin(nextOfKinJsonObject.toString());
				uploadPatientRegistrationRecord(registrationRequest);
			}
		} else {
			MyCareHubSetting latestPatientRegistrationSetting = new MyCareHubSetting(PATIENT_REGISTRATIONS, new Date());
			settingsService.saveMyCareHubSettings(latestPatientRegistrationSetting);
		}
	}
	
	private void fetchRegisteredPatientIdentifiers() {
		
	}
	
	private void uploadUpdatedPatientsMedicalRecords() {
		
	}
}
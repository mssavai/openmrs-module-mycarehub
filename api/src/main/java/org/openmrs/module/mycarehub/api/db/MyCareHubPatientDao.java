package org.openmrs.module.mycarehub.api.db;

import org.openmrs.Patient;

import java.util.Date;
import java.util.List;

public interface MyCareHubPatientDao {
	
	List<Patient> getCccPatientsCreatedOrUpdatedSinceDate(Date lastSyncDate);
}
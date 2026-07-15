package com.LabResourceUtilizationPlatform.Entity.Enum;

public enum RoleName {
	SYSTEM_ADMIN, 			//Create -> Institute
	INSTITUTION_ADMIN,		//Create -> Department, Lab, Equipment
	DEPARTMENT_HEAD,		//Create -> Lab, Equipment
	PROFESSOR,				// Only See and book
	ASSOCIATE_PROFESSOR,	// Only See and book
	ASSISTANT_PROFESSOR,	// Only See and book
	RESEARCH_SCIENTIST,		// Only See and book
	RESEARCH_ASSOCIATE,		// Only See and book
	LAB_MANAGER,			//Create -> Equipment
	LAB_TECHNICIAN,			//Only See
	RESEARCHER,				// Only See and book
	STUDENT					// Only See and book
}

package org.openmrs.module.coreapps.fragment.controller.patientsearch;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * Fragment controller for patient search widget; sets the min # of search characters based on global property,
 * and loads last viewed patients for current user if "showLastViewedPatients" fragment config param=true
 */
public class PatientSearchWidgetFragmentController {

    public void controller(FragmentModel model, UiSessionContext sessionContext,
                           HttpServletRequest request,
                           @SpringBean("adminService") AdministrationService administrationService,
                           @FragmentParam(value = "showLastViewedPatients", required = false) Boolean showLastViewedPatients,
                           @FragmentParam(value = "initialSearchFromParameter", required = false) String searchByParam) {

        showLastViewedPatients = showLastViewedPatients != null ? showLastViewedPatients : false;

        model.addAttribute("minSearchCharacters",
                administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_MIN_SEARCH_CHARACTERS, "1"));

        model.addAttribute("searchDelayShort",
                administrationService.getGlobalProperty(CoreAppsConstants.GP_SEARCH_DELAY_SHORT, "300"));

        model.addAttribute("searchDelayLong",
                administrationService.getGlobalProperty(CoreAppsConstants.GP_SEARCH_DELAY_LONG, "1000"));

        model.addAttribute("dateFormatJS", "DD MMM YYYY");  // TODO really should be driven by global property, but currently we only have a property for the java date format
        model.addAttribute("locale", Context.getLocale().getLanguage());
        model.addAttribute("defaultLocale", new Locale(administrationService.getGlobalProperty((OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE), "en")).getLanguage());
        model.addAttribute("dateFormatter", new SimpleDateFormat(administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT),
                Context.getLocale()));
        model.addAttribute("showLastViewedPatients", showLastViewedPatients);

        String doInitialSearch = null;
        if (searchByParam != null && StringUtils.isNotEmpty(request.getParameter(searchByParam))) {
            doInitialSearch = request.getParameter(searchByParam);
        }
        model.addAttribute("doInitialSearch", doInitialSearch);

        if (showLastViewedPatients) {
            List<Patient> patients = GeneralUtils.getLastViewedPatients(sessionContext.getCurrentUser());
            model.addAttribute("lastViewedPatients", patients);
        }

    }

    public SimpleObject searchForPatientByFingerPrint(
            @RequestParam(value = "datakey", required = false)String fingerPrintInBase64,
            @SpringBean("patientService") PatientService service,
            UiUtils ui){
    	//String patientSearchPageUrl = "http://localhost:8080/openmrs/coreapps/findpatient/findPatient.page?app=patients.findPatientByFingerprint";
    	String fingerPrintPersonAttributeTypeUUID = "0fe8824e-f9f8-42fa-a919-4d2dcd00a5da";
    	Patient searchedPatient = new Patient();
    	
    	
    	String uuid ="null";
    	 System.out.println("Server reached");
    	 
    	
    	try{
    		Context.openSession();
            Context.authenticate("admin", "Admin123");
            

        	//search patient attribute: leftIndexFingerPrint
        	List<Patient> patients = Context.getPatientService().getAllPatients();
    		System.out.println("Number of Patients: "+patients.size());
        	
        	if(fingerPrintInBase64!= null){
        		for(Patient patientInstance : patients){
        			List<PersonAttribute> personAttributes = patientInstance.getActiveAttributes();
        			
        			for(PersonAttribute personAttribute: personAttributes){
        				System.out.println("Person Attribute: "+personAttribute.getValue());
        				
        					System.out.println("Person Attribute Type UUID: "+personAttribute.getAttributeType().getUuid());
        					if(personAttribute.getAttributeType().getUuid().equalsIgnoreCase(fingerPrintPersonAttributeTypeUUID)){
            					System.out.println("Person attributeType passed...");
            					//test if the base64 generated matches our stored base64 text
            					if(personAttribute.getValue() != null ){
            						System.out.println("Patient UUID: "+patientInstance.getUuid());
            						uuid = patientInstance.getUuid();
            						searchedPatient = patientInstance;
            						break;
            					}
            				}
        				
        			}//end person attribute loop
        
        			if(uuid!="null"){
        				break;
        			}
        		}//end patient loop
        	}
    	}catch(Exception e){
    		System.out.println(".......exception..................................................................");
    		e.getStackTrace();
    	}
    		finally{
    	
    		Context.closeSession();
    	}
    	String [] properties = {"uuid"};
    	SimpleObject simplePatientObject = SimpleObject.fromObject(searchedPatient, ui, properties);
    	
    	
    	return simplePatientObject;
    }
   
}

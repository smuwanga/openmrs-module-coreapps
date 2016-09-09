<%
    config.require("afterSelectedUrl")
    def breadcrumbOverride = config.breadcrumbOverride ?: ""

    ui.includeCss("uicommons", "datatables/dataTables_jui.css")
    ui.includeCss("coreapps", "patientsearch/patientSearchWidget.css")
	ui.includeCss("coreapps", "patientsearch/fontcustom_findpatient_fingerprint.css")
    ui.includeJavascript("uicommons", "datatables/jquery.dataTables.min.js")
    ui.includeJavascript("coreapps", "patientsearch/patientSearchWidget.js")
	ui.includeJavascript("coreapps", "patientsearch/patientFingerPrintSearch.js")
    ui.includeJavascript("uicommons", "moment-with-locales.min.js")
  
%>
<script type="text/javascript">

    var lastViewedPatients = [];
    <%  if (showLastViewedPatients && !doInitialSearch) {
            lastViewedPatients.each { it -> %>
    lastViewedPatients.push({uuid:"${ it.uuid }",name:"${ it.personName ? ui.escapeJs(ui.format(it.personName)) : '' }",gender:"${ it.gender }",
        age:"${ it.age ?: '' }", birthdate:"${ it.birthdate ? dateFormatter.format(it.birthdate) : '' }",
        birthdateEstimated: ${ it.birthdateEstimated }, identifier:"${ it.patientIdentifier ? ui.escapeJs(it.patientIdentifier.identifier) : '' }"});
    <%      }
        }%>
    function handlePatientRowSelection() {
        this.handle = function (row) {
            var uuid = row.uuid;
            location.href = '/' + OPENMRS_CONTEXT_PATH + emr.applyContextModel('${ ui.escapeJs(config.afterSelectedUrl) }', { patientId: uuid, breadcrumbOverride: '${ ui.escapeJs(breadcrumbOverride) }'});
        }
    }
    var handlePatientRowSelection =  new handlePatientRowSelection();
    jq(function() {
        var widgetConfig = {
            initialPatients: lastViewedPatients,
            doInitialSearch: ${ doInitialSearch ? "\"" + ui.escapeJs(doInitialSearch) + "\"" : "null" },
            minSearchCharacters: ${ config.minSearchCharacters ?: 3 },
            afterSelectedUrl: '${ ui.escapeJs(config.afterSelectedUrl) }',
            breadcrumbOverride: '${ ui.escapeJs(breadcrumbOverride) }',
            searchDelayShort: ${ searchDelayShort },
            searchDelayLong: ${ searchDelayLong },
            handleRowSelection: ${ config.rowSelectionHandler ?: "handlePatientRowSelection" },
            dateFormat: '${ dateFormatJS }',
            locale: '${ locale }',
            defaultLocale: '${ defaultLocale }',
            
            messages: {
                info: '${ ui.message("coreapps.search.info") }',
                first: '${ ui.message("coreapps.search.first") }',
                previous: '${ ui.message("coreapps.search.previous") }',
                next: '${ ui.message("coreapps.search.next") }',
                last: '${ ui.message("coreapps.search.last") }',
                noMatchesFound: '${ ui.message("coreapps.search.noMatchesFound") }',
                noData: '${ ui.message("coreapps.search.noData") }',
                recent: '${ ui.message("coreapps.search.label.recent") }',
                searchError: '${ ui.message("coreapps.search.error") }',
                identifierColHeader: '${ ui.message("coreapps.search.identifier") }',
                nameColHeader: '${ ui.message("coreapps.search.name") }',
                genderColHeader: '${ ui.message("coreapps.gender") }',
                ageColHeader: '${ ui.message("coreapps.age") }',
                birthdateColHeader: '${ ui.message("coreapps.birthdate") }'
            }
        };

        new PatientSearchWidget(widgetConfig);
    });
</script>
<script type="text/javascript">
    jq = jQuery;
    
    
    
    jq(function() {
    jq('#testAjaxButton').click(function() {
		        jq.getJSON('${ ui.actionLink("searchForPatientByFingerPrint") }',
		            {
		              'datakey': 'searchForPatientByFingerPrint'
		              
		             
		            })
		        .success(function(data) {
		           
		            window.location=".../../coreapps/clinicianfacing/patient.page?patientId=" + data.uuid;
		            
				 
		        })
		        .error(function(xhr, status, err) {
		            alert('AJAX error ' + err);
		        })
		    });
		});
	
</script>
<form method="get" id="patient-search-form" onsubmit="return false">
    <input type="text" id="patient-search" placeholder="${ ui.message("coreapps.findPatient.search.placeholder") }" autocomplete="off" <% if (doInitialSearch) { %>value="${doInitialSearch}"<% } %>/>
    <i id="patient-search-clear-button" class="small icon-remove-sign"></i>
    <i id="patient-search-finger-print-button" class="small icon-fingerprint"></i>
</form>
<div id="patient-search-finger-print" style="display:none;">
    <br>
	<button id="testAjaxButton" onclick="searchFingerPrint()"></button>
    <p>Search functionality</p>
	<applet code="org.openmrs.module.fingerprint.applet.PatientSearchApplet" archive="/openmrs/finger-print-applet.jar" width="650" height="300">
            
    </applet>
</div>
<div id="patient-search-results"></div>

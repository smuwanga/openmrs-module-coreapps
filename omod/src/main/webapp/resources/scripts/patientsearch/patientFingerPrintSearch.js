
        var patientSearchFingerPrintButton = jq('#patient-search-finger-print-button');
        var patientSearchFingerPrint = jq('#patient-search-finger-print');
		var toggleVisibility = function () {
       	     var e = document.getElementById(patientSearchFingerPrint);
             if(e.style.display == 'block')
                 e.style.display = 'none';
             else
                 e.style.display = 'block';
		}
   

		//handle the fingerprint search button
        patientSearchFingerPrintButton.click(function(){
        	alert("Search button clicked!!");
		});
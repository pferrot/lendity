var mPersonalConfigurationContextPath = "";


function initPersonConfiguration(pContextPath) {
	mPersonalConfigurationContextPath = pContextPath;
}

function updatePersonConfiguration(pTheKey, pTheValue) {
	$j.ajax({
		url: mPersonalConfigurationContextPath + '/public/personConfiguration/personConfiguration.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'update', theKey: pTheKey, theValue: pTheValue},
		success: updatePersonConfigurationResponse,
		cache: false
	});
}

function updatePersonConfigurationResponse(pData, pTextStatus, pXmlHttpRequest) {
//	var result = pData.result;
//	alert(result);
}

function deletePersonConfiguration(pTheKey) {
	$j.ajax({
		url: mPersonalConfigurationContextPath + '/public/personConfiguration/personConfiguration.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'delete', theKey: pTheKey},
		success: deletePersonConfigurationResponse,
		cache: false
	});
}

function deletePersonConfigurationResponse(pData, pTextStatus, pXmlHttpRequest) {
//	var result = pData.result;
//	alert(result);
}

/**
 * This is called when the user clicks the checkbox to show/hide the help automatically
 * and updates his preference accordingly.
 *
 * @param pCheckBox
 * @param pKey
 * @return
 */
function showHideHelpAutomaticallyClicked(pCheckBox, pKey) {
	if (pCheckBox.checked) {
		updatePersonConfiguration(pKey, 'true');
	}
	else {
		updatePersonConfiguration(pKey, 'false');
	}
}
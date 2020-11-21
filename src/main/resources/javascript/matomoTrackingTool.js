/**
 * Matomo functions to visualise the Matomo tracking results
 *
 * @author Cédric FOURNEAU (cedric.fourneau@chenconsulting.eu)
 *
 */

function matomoCheckSettings(matomoSettings) {
	if (matomoSettings.siteId === null || matomoSettings.matomoServerUrl === null ) {
		return false;
	}
	return true;
}

function matomoTrackSiteSearch(_paq, matomoSettings) {

	// If we do not track site search
	if (!matomoSettings.isTrackingSearch) {
		return false;
	}

	// Retrieve search results
	var pageContainsSearchResults = false;
	if (matomoSettings.searchResultCssSelector !== null && $(matomoSettings.searchResultCssSelector).length > 0) {
		pageContainsSearchResults = true;
	}

	// If the current page contains search results
	if (pageContainsSearchResults) {

		// Retrieve search keyword
		var searchKeyword = false;
		if (matomoSettings.searchKeywordCssSelector !== null && $(matomoSettings.searchKeywordCssSelector).length > 0) {
			// If search keyword CSS selector identifies an INPUT tag
			if ($(matomoSettings.searchKeywordCssSelector).prop("tagName") === "INPUT") {
				searchKeyword = $(matomoSettings.searchKeywordCssSelector).val();
			}
			else {
				searchKeyword = $(matomoSettings.searchKeywordCssSelector).text();
			}
		}

		// If a searchKeyword was retrieved
		if (searchKeyword) {

			//Retrieve search count
			var searchCount = false;
			if (matomoSettings.searchCountCssSelector !== null && $(matomoSettings.searchCountCssSelector).length > 0) {
				var searchCountValue = $(matomoSettings.searchCountCssSelector).text();
				// If the value is a correct number
				if (!isNaN(searchCountValue)) {
					searchCount = parseInt(searchCountValue);
				}
			}

			// Add track site search stats to matomo configuration
			// TODO: remove userID?
			//_paq.push(['setUserId', jahiaUsername]);
			_paq.push(["trackSiteSearch", searchKeyword, false, searchCount]);

			return true;
		}

	}

	return false;
}

function matomoTrackJahiaUsername(_paq, jahiaUsername /*, userDivisionName*/) {
	// Track for Matomo
	_paq.push(["setCustomVariable", 1, "Username",  jahiaUsername, "visit"]);
	/*
	if (userDivisionName !=null ) {
		_paq.push(["setCustomVariable", 5, "userDivisionName",  userDivisionName, "visit"]);
	}
	*/
}

function matomoTrackJahiaConnectionMode(_paq, jahiaConnectionMode) {
	// Track for Matomo
	_paq.push(["setCustomVariable", 2, "Jahia connection mode",  jahiaConnectionMode, "visit"]);
}

function matomoTrackJahiaLanguage(_paq, jahiaLanguage) {
	// Track for Matomo
	_paq.push(["setCustomVariable", 3, "Language", jahiaLanguage, "visit"]);
}

function matomoTrackDomain(_paq) {
	// Retrieve Domain
	var domain = document.domain;

	// Track for Matomo
	_paq.push(["setCustomVariable", 4, "domain",  domain, "visit"]);
}


function matomoInitDashboard(_paq, jahiaConnectionMode, jahiaUsername, language) {
	// Retrieve settings, they are defined in the HTML fragment
	var matomoSettings = matomoRetrieveSettings();

	// If mandatory settings are not provided, exit
	if (!matomoCheckSettings(matomoSettings)) {
		return;
	}

	// If connection mode is Studio, exit
	if (jahiaConnectionMode == "studiovisual") {
		return;
	}

	//Build Matomo server URL
	//var u=window.location.origin;
	var u = matomoSettings.matomoServerUrl;

	_paq.push(["setTrackerUrl", u+"/piwik/piwik.php"]);
	_paq.push(["setSiteId", matomoSettings.siteId]);
    _paq.push(["enableLinkTracking"]); //Allows to enable the tracking on downloads

    // If we have to track Jahia username
	if (matomoSettings.isTrackingJahiaUsername=="true") {
		matomoTrackJahiaUsername(_paq, jahiaUsername /*, userDivisionName*/);
	}

	// If we have to track Jahia connection mode
	if (matomoSettings.isTrackingJahiaConnectionMode=="true") {
		matomoTrackJahiaConnectionMode(_paq, jahiaConnectionMode);
	}

	// If we have to track Jahia language
	if (matomoSettings.isTrackingJahiaLanguage=="true") {
		matomoTrackJahiaLanguage(_paq, language);
	}

	// If we have to track domain
	if (matomoSettings.isTrackingDomain=="true") {
		matomoTrackDomain(_paq);
	}

	// Document title
	_paq.push(["setDocumentTitle", document.title]);

	// Try to detect if site search tracking can be used
	var isSiteSearchTrackingUsed = matomoTrackSiteSearch(_paq, matomoSettings);

	// If no site search tracking was used
	if (!isSiteSearchTrackingUsed) {

	// Track page view stats
		_paq.push(['setUserId', jahiaUsername]);
		_paq.push(["trackPageView"]);
	}

}
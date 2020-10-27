/**
 * Piwik functions to visualise the Piwik tracking results
 *
 * @author Cédric FOURNEAU (cedric.fourneau@chenconsulting.eu)
 *
 */

function piwikCheckSettings(piwikSettings) {
	if (piwikSettings.siteId === null || piwikSettings.piwikServerUrl === null ) {
		return false;
	}
	return true;
}

function piwikTrackSiteSearch(_paq, piwikSettings) {

	// If we do not track site search
	if (!piwikSettings.isTrackingSearch) {
		return false;
	}

	// Retrieve search results
	var pageContainsSearchResults = false;
	if (piwikSettings.searchResultCssSelector !== null && $(piwikSettings.searchResultCssSelector).length > 0) {
		pageContainsSearchResults = true;
	}

	// If the current page contains search results
	if (pageContainsSearchResults) {

		// Retrieve search keyword
		var searchKeyword = false;
		if (piwikSettings.searchKeywordCssSelector !== null && $(piwikSettings.searchKeywordCssSelector).length > 0) {
			// If search keyword CSS selector identifies an INPUT tag
			if ($(piwikSettings.searchKeywordCssSelector).prop("tagName") === "INPUT") {
				searchKeyword = $(piwikSettings.searchKeywordCssSelector).val();
			}
			else {
				searchKeyword = $(piwikSettings.searchKeywordCssSelector).text();
			}
		}

		// If a searchKeyword was retrieved
		if (searchKeyword) {

			//Retrieve search count
			var searchCount = false;
			if (piwikSettings.searchCountCssSelector !== null && $(piwikSettings.searchCountCssSelector).length > 0) {
				var searchCountValue = $(piwikSettings.searchCountCssSelector).text();
				// If the value is a correct number
				if (!isNaN(searchCountValue)) {
					searchCount = parseInt(searchCountValue);
				}
			}

			// Add track site search stats to piwik configuration
			// TODO: remove userID?
			//_paq.push(['setUserId', jahiaUsername]);
			_paq.push(["trackSiteSearch", searchKeyword, false, searchCount]);

			return true;
		}

	}

	return false;
}

function piwikTrackJahiaUsername(_paq, jahiaUsername /*, userDivisionName*/) {
	// Track for Piwik
	_paq.push(["setCustomVariable", 1, "Username",  jahiaUsername, "visit"]);
	/*
	if (userDivisionName !=null ) {
		_paq.push(["setCustomVariable", 5, "userDivisionName",  userDivisionName, "visit"]);
	}
	*/
}

function piwikTrackJahiaConnectionMode(_paq, jahiaConnectionMode) {
	// Track for Piwik
	_paq.push(["setCustomVariable", 2, "Jahia connection mode",  jahiaConnectionMode, "visit"]);
}

function piwikTrackJahiaLanguage(_paq, jahiaLanguage) {
	// Track for Piwik
	_paq.push(["setCustomVariable", 3, "Language", jahiaLanguage, "visit"]);
}

function piwikTrackDomain(_paq) {
	// Retrieve Domain
	var domain = document.domain;

	// Track for Piwik
	_paq.push(["setCustomVariable", 4, "domain",  domain, "visit"]);
}

/*
function piwikTrackisTrackingContent(_paq, jahiaUsername, isSiteSearchTrackingUsed) {
	//_paq.push(["setCustomVariable", 2, "Category", "Article", "page"]);
    // Track content view stats
    if (isSiteSearchTrackingUsed) {
        // needed if 'isSiteSearchTrackingUsed' is true (see below)
		_paq.push(['setUserId', jahiaUsername]);
		_paq.push(["trackPageView"]);
    }
    _paq.push(['trackAllContentImpressions']);
}
*/


function piwikInitDashboard(_paq, jahiaConnectionMode, jahiaUsername, /*userDivisionName,*/ language) {
	// Retrieve settings, they are defined in the HTML fragment
	var piwikSettings = piwikRetrieveSettings();

	// If mandatory settings are not provided, exit
	if (!piwikCheckSettings(piwikSettings)) {
		return;
	}

	// If connection mode is Studio, exit
	if (jahiaConnectionMode == "studiovisual") {
		return;
	}

	//Build Piwik server URL
	//var u=window.location.origin;
	var u = piwikSettings.piwikServerUrl;

	_paq.push(["setTrackerUrl", u+"/piwik/piwik.php"]);
	_paq.push(["setSiteId", piwikSettings.siteId]);
    _paq.push(["enableLinkTracking"]); //Allows to enable the tracking on downloads

    // If we have to track Jahia username
	if (piwikSettings.isTrackingJahiaUsername=="true") {
		piwikTrackJahiaUsername(_paq, jahiaUsername /*, userDivisionName*/);
	}

	// If we have to track Jahia connection mode
	if (piwikSettings.isTrackingJahiaConnectionMode=="true") {
		piwikTrackJahiaConnectionMode(_paq, jahiaConnectionMode);
	}

	// If we have to track Jahia language
	if (piwikSettings.isTrackingJahiaLanguage=="true") {
		piwikTrackJahiaLanguage(_paq, language);
	}

	// If we have to track domain
	if (piwikSettings.isTrackingDomain=="true") {
		piwikTrackDomain(_paq);
	}

	// Document title
	_paq.push(["setDocumentTitle", document.title]);

	// Try to detect if site search tracking can be used
	var isSiteSearchTrackingUsed = piwikTrackSiteSearch(_paq, piwikSettings);

	// If no site search tracking was used
	if (!isSiteSearchTrackingUsed) {

	// Track page view stats
		_paq.push(['setUserId', jahiaUsername]);
		_paq.push(["trackPageView"]);
	}

    // If we have to track contents pages
	/*
    if (piwikSettings.isTrackingContent=="true") {
        piwikTrackisTrackingContent(_paq, jahiaUsername, isSiteSearchTrackingUsed);
    }
    */

}
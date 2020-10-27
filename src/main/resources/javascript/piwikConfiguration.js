// Global piwik configuration
var _paq = _paq || [];

function piwikRetrieveSettings() {
	return piwikSettings;
}


// When DOM is ready
$(document).ready(function() {
    piwikInitDashboard(_paq, jahiaConnectionMode, jahiaUsername, language);

    jQuery.cachedScript = function( url, options ) {
        // Allow user to set any option except for dataType, cache, and url
        options = $.extend( options || {}, {
            dataType: "script",
            cache: true,
            url: url
        });
        // Use $.ajax() since it is more flexible than $.getScript
        // Return the jqXHR object so we can chain callbacks
        return jQuery.ajax( options );
    };

    // Usage
    $.cachedScript( piwikSettings.piwikServerUrl+"/piwik/piwik.js" ).done(function( script, textStatus ) {
        console.log( textStatus );
    });

});
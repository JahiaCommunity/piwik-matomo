
println(" \n"+

	"<script type=\"text/javascript\" > \n" +

		" <!-- \n" +
		"var piwikSettings = {\n" +
			"siteId: \"${siteId}\", \n" +
			"piwikServerUrl: \"${piwikServerUrl}\", \n" +
			"isTrackingSearch: \"${isTrackingSearch}\", \n" +
			"isTrackingContent: \"${isTrackingContent}\", \n" +
			"searchResultCssSelector: \"${searchResultCssSelector}\", \n" +
			"searchKeywordCssSelector: \"${searchKeywordCssSelector}\", \n" +
			"searchCountCssSelector: \"${searchCountCssSelector}\", \n" +
			"isTrackingJahiaConnectionMode: \"${isTrackingJahiaConnectionMode}\", \n" +
			"isTrackingJahiaLanguage: \"${isTrackingJahiaLanguage}\", \n" +
			"isTrackingJahiaUsername: \"${isTrackingJahiaUsername}\", \n" +
			"isTrackingDomain: \"${isTrackingDomain}\" \n" +
		"};\n" +

		"// Retrieve Jahia connection mode \n" +
		"jahiaConnectionMode = \"${jahiaConnectionMode}\"; \n" +

		"// Retrieve Jahia user name \n" +
		"jahiaUsername=\"${jahiaUsername}\"; \n" +

		"language=\"${language}\";	\n" +
	"--> \n" +
	"</script> \n"
)


println("<jahia:resource type=\"javascript\" path=\"${piwikconfiguration_path}\" resource=\"${piwikconfiguration_resource}\" title=\"\" key=\"\" insert=\"true\" />\n")
println("<jahia:resource type=\"javascript\" path=\"${piwiktrackingtool_path}\" resource=\"${piwiktrackingtool_resource}\" title=\"\" key=\"\" insert=\"true\" />\n")
println("<jahia:resource type=\"javascript\" path=\"${jquery_min_path}\" resource=\"${jquery_min_resource}\" title=\"\" key=\"\" insert=\"true\" />\n")

package org.jahiacommunity.modules.piwik.utils;


public class PiwikSettingsUtils {

	/**
	 * Settings node name
	 */
	public static final String SETTINGS_NODE_NAME = "piwik_settings";

	/**
	 * Settings node type
	 */
	public static final String SETTINGS_NODE_TYPE = "jcpwnt:piwikSettingsVariables";


	/**
	 * String Settings node's properties
	 */
	public static final String SEARCH_RESULT_CSS_SELECTOR = "searchResultCssSelector";
	public static final String SEARCH_KEYWORD_CSS_SELECTOR = "searchKeywordCssSelector";
	public static final String SEARCH_COUNT_CSS_SELECTOR = "searchCountCssSelector";
	public static final String SITE_ID = "siteId";
	public static final String PIWIK_SERVER_URL = "piwikServerUrl";

	/**
	 * Boolean Settings node's properties
	 */
	public static final String ISTRACKINGSEARCH_PROPERTY_NAME = "isTrackingSearch";
	public static final String ISTRACKINGCONTENT_PROPERTY_NAME = "isTrackingContent";
	public static final String ISTRACKINGJAHIACONNECTIONMODE_PROPERTY_NAME = "isTrackingJahiaConnectionMode";
	public static final String ISTRACKINGJAHIALANGUAGE_PROPERTY_NAME = "isTrackingJahiaLanguage";
	public static final String ISTRACKINGJAHIAUSERNAME_PROPERTY_NAME = "isTrackingJahiaUsername";
	public static final String ISTRACKINGDOMAIN_PROPERTY_NAME = "isTrackingDomain";
	public static final String TRACKING_LIVE_ONLY = "trackingLiveOnly";



	/**
	 * Cache of piwik settings
	 */
	// Cache to store the piwik settings
	public static final String SETTINGS_NAME_CACHE = "Piwik-MATOMO-Cache-Settings";
	// 0 == no limit
	public static final int SETTINGS_MAX_ELEMENTS_IN_MEMORY_CACHE = 0;
	// whether to use the disk store
	public static final boolean SETTINGS_OVERFLOW_TO_DISK_CACHE = false;
	// whether the elements in the cache are eternal, i.e. never expire
	public static final boolean SETTINGS_ETERNAL_CACHE = true;
	// useless
	public static final long SETTINGS_TIME_TO_LIVE_SECONDS_CACHE = 14400;
	// useless
	public static final long SETTINGS_TIME_TO_IDLE_SECONDS = 0;

}

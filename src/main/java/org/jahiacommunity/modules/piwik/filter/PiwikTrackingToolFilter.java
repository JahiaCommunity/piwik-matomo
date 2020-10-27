package org.jahiacommunity.modules.piwik.filter;

import org.jahiacommunity.modules.piwik.cache.PiwikCacheService;
import org.jahiacommunity.modules.piwik.utils.PiwikSettingsUtils;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.sf.ehcache.Cache;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.cache.AggregateCacheFilter;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.utils.ScriptEngineUtils;
import org.jahia.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.List;

/**
 * This class is a JAHIA filter applied on every page in the site. This filter is applied before aggregateCacheFilter and use this own caches.
 *
 * @author Cédric FOURNEAU (cedric.fourneau@chenconsulting.eu)
 *
 */
public class PiwikTrackingToolFilter extends AbstractFilter {

	private static final Logger logger = LoggerFactory.getLogger(PiwikTrackingToolFilter.class);

	private static final String JS_JQUERY_MIN_FILE = "jquery.min.js";
	private static final String JS_TRACKINGTOOL_FILE = "piwikTrackingTool.js";
	private static final String JS_CONFIGURATION_FILE = "piwikConfiguration.js";

    private Cache cacheSettings = null;

	private String template;
    private String currentSiteKey;

	private ScriptEngineUtils scriptEngineUtils;

	private String siteId = null;
	private String piwikServerUrl = null;
	private String isTrackingSearch = "false";
    private String isTrackingContent = "false";
	private String searchResultCssSelector = null;
	private String searchKeywordCssSelector = null;
	private String searchCountCssSelector = null;
	private String isTrackingJahiaConnectionMode = "false";
	private String isTrackingJahiaLanguage = "false";
	private String isTrackingJahiaUsername = "false";
	private String isTrackingDomain = "false";
	private String jahiaConnectionMode = null;
	private String jahiaUsername = null;
	private String language = null;
	private boolean trackingLiveOnly = false;


    /**
     * Constructor
     */
    public PiwikTrackingToolFilter() {
        this.cacheSettings = PiwikCacheService.getPiwikSettingsCache();
    }

    /**
     * Method executed by the filter to add the PIWIK javascript code in the page.
     */
	@Override
	public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {

        this.currentSiteKey = renderContext.getSite().getSiteKey();
		String out = previousOut;
		Source source = new Source(previousOut);
		OutputDocument outputDocument = new OutputDocument(source);

		this.jahiaConnectionMode = renderContext.getMode();
        this.jahiaUsername = renderContext.getUser().getUsername() != null ? renderContext.getUser().getUsername() : JahiaUserManagerService.GUEST_USERNAME;
		this.language = resource.getLocale().getLanguage();

		JCRNodeWrapper settingsNode = null;
        if (renderContext.getSite().hasNode(PiwikSettingsUtils.SETTINGS_NODE_NAME)) {
            settingsNode = renderContext.getSite().getNode(PiwikSettingsUtils.SETTINGS_NODE_NAME);
			resource.getDependencies().add(settingsNode.getCanonicalPath());

			siteId = lookupProperty(PiwikSettingsUtils.SITE_ID, settingsNode);
			piwikServerUrl = lookupProperty(PiwikSettingsUtils.PIWIK_SERVER_URL, settingsNode);
			isTrackingSearch = lookupProperty(PiwikSettingsUtils.ISTRACKINGSEARCH_PROPERTY_NAME, settingsNode) == "" ? "false" : "true";
			isTrackingContent = lookupProperty(PiwikSettingsUtils.ISTRACKINGCONTENT_PROPERTY_NAME, settingsNode) == "" ? "false" : "true";
			searchResultCssSelector = lookupProperty(PiwikSettingsUtils.SEARCH_RESULT_CSS_SELECTOR, settingsNode);
			searchKeywordCssSelector = lookupProperty(PiwikSettingsUtils.SEARCH_KEYWORD_CSS_SELECTOR, settingsNode);
			searchCountCssSelector = lookupProperty(PiwikSettingsUtils.SEARCH_COUNT_CSS_SELECTOR, settingsNode);
			isTrackingJahiaConnectionMode = lookupProperty(PiwikSettingsUtils.ISTRACKINGJAHIACONNECTIONMODE_PROPERTY_NAME, settingsNode) == "" ? "false" : "true";
			isTrackingJahiaLanguage = lookupProperty(PiwikSettingsUtils.ISTRACKINGJAHIALANGUAGE_PROPERTY_NAME, settingsNode) == "" ? "false" : "true";
			isTrackingJahiaUsername = lookupProperty(PiwikSettingsUtils.ISTRACKINGJAHIAUSERNAME_PROPERTY_NAME, settingsNode) == "" ? "false" : "true";
			isTrackingDomain = lookupProperty(PiwikSettingsUtils.ISTRACKINGDOMAIN_PROPERTY_NAME, settingsNode) == "" ? "false" : "true";
			trackingLiveOnly = lookupProperty(PiwikSettingsUtils.TRACKING_LIVE_ONLY, settingsNode) == "" ? false : true;
		}

		// Script de configuration des paramètres Piwik.
		// Paramètres sont configurés sur la vue jcpwnt_piwikSettings.
		String script = getResolvedTemplate(this.template);
		if (script != null) {

			List<Element> bodyElementList = source.getAllElements(HTMLElementName.BODY);
			for (Element element : bodyElementList) {
				String extension = StringUtils.substringAfterLast(template, ".");
				ScriptEngine scriptEngine = scriptEngineUtils.scriptEngine(extension);
				ScriptContext scriptContext = new PiwikTrackingToolScriptContext();
				final Bindings bindings = scriptEngine.createBindings();
				bindings.put("siteId", this.siteId);
				bindings.put("piwikServerUrl", this.piwikServerUrl);
				bindings.put("isTrackingSearch", this.isTrackingSearch);
                bindings.put("isTrackingContent", this.isTrackingContent);
				bindings.put("searchResultCssSelector", this.searchResultCssSelector);
				bindings.put("searchKeywordCssSelector", this.searchKeywordCssSelector);
				bindings.put("searchCountCssSelector", this.searchCountCssSelector);
				bindings.put("isTrackingJahiaConnectionMode", this.isTrackingJahiaConnectionMode);
				bindings.put("isTrackingJahiaLanguage", this.isTrackingJahiaLanguage);
				bindings.put("isTrackingJahiaUsername", this.isTrackingJahiaUsername);
				bindings.put("isTrackingDomain", this.isTrackingDomain);
				bindings.put("jahiaConnectionMode", this.jahiaConnectionMode);
				bindings.put("jahiaUsername", this.jahiaUsername);
				bindings.put("language", this.language);

				//TODO: check si fonctionne avec module ASSETS de JAHIA + remplacer par + générique?
				//for Scustom project, jquery url = http://localhost/modules/assets/javascript/build/jquery.min.js
				String encodedPath = URLEncoder.encode("/modules/assets/javascript/build/" + JS_JQUERY_MIN_FILE, "UTF-8");
				bindings.put("jquery_min_path", encodedPath);
				bindings.put("jquery_min_resource", JS_JQUERY_MIN_FILE);


				encodedPath = URLEncoder.encode("/modules/piwik-matomo/javascript/" + JS_TRACKINGTOOL_FILE, "UTF-8");
				bindings.put("piwiktrackingtool_path", encodedPath);
				bindings.put("piwiktrackingtool_resource", JS_TRACKINGTOOL_FILE);

				encodedPath = URLEncoder.encode("/modules/piwik-matomo/javascript/" + JS_CONFIGURATION_FILE, "UTF-8");
				bindings.put("piwikconfiguration_path", encodedPath);
				bindings.put("piwikconfiguration_resource", JS_CONFIGURATION_FILE);

				scriptContext.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
				scriptEngine.eval(script, scriptContext);
				StringWriter writer = (StringWriter) scriptContext.getWriter();
				final String piwikSettingsScript = writer.toString();

				if(!trackingLiveOnly || renderContext.getMode().equals("live")){
		               if (StringUtils.isNotBlank(piwikSettingsScript)) {
                        outputDocument.replace(element.getEndTag().getBegin() - 1, element.getEndTag().getBegin(),
                                "\n" + AggregateCacheFilter.removeCacheTags(piwikSettingsScript) + "\n");
		                }
				}
				// logger.error("Fake Error for test : " +
				// element.getEndTag().toString() + " " + outputDocument);

				break; // avoid to loop if for any reasons multiple body in the
						// page
			}
			out = outputDocument.toString().trim();
		}
		return out;
	}

    /**
     * This method is called by JAHIA and allows to verify the following condition. The PIWIK filter will be executed only on sites with PIWIK modules installed.
     */
	@Override
	public boolean areConditionsMatched(RenderContext renderContext, Resource resource) {
		return super.areConditionsMatched(renderContext, resource) && renderContext.getSite().getAllInstalledModules().contains("piwik");
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	protected String getResolvedTemplate(String template) throws IOException {
		String resolvedTemplate = null;
		if (resolvedTemplate == null) {
			resolvedTemplate = WebUtils.getResourceAsString(template);
			if (resolvedTemplate == null) {
				logger.warn("Unable to lookup template at {}", template);
			}
		}
		return resolvedTemplate;
	}

	class PiwikTrackingToolScriptContext extends SimpleScriptContext {
		private Writer writer = null;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Writer getWriter() {
			if (writer == null) {
				writer = new StringWriter();
			}
			return writer;
		}
	}

    public void setScriptEngineUtils(ScriptEngineUtils scriptEngineUtils) {
        this.scriptEngineUtils = scriptEngineUtils;
    }

    /**
     * This method looks for the property with "propertyName" in the specified node "nodeSettingsPiwik" and put its value in cache "cacheSettings". If property's value is null, empty string ("") is
     * put in cache.
     *
     * @param propertyName
     *            : the name of the property looked for
     * @param nodeSettingsPiwik
     *            : the node where the property must be found
     * @return String : property's value
     */
    private String lookupProperty(String propertyName, JCRNodeWrapper nodeSettingsPiwik) {
        // Check if the value is in the cache
        String cacheKey = propertyName + currentSiteKey;
		if (cacheSettings == null) {
			this.cacheSettings = PiwikCacheService.getPiwikSettingsCache();
		}
        net.sf.ehcache.Element elem = cacheSettings.get(cacheKey);
        if (elem == null) {
            // If it is not in the cache we get the value from the JCR and put
            // the found value in the cache, or if the found value == null put
            // empty string ("") in cache
            String value = nodeSettingsPiwik.getPropertyAsString(propertyName);
            if (value == null) {
                elem = new net.sf.ehcache.Element(cacheKey, "");
            } else {
                elem = new net.sf.ehcache.Element(cacheKey, value);
            }
            cacheSettings.put(elem);
        }
        return elem.getObjectValue().toString();
    }

}

package org.jahiacommunity.modules.piwik.action;

import org.jahiacommunity.modules.piwik.cache.MatomoCacheService;
import org.jahiacommunity.modules.piwik.utils.PiwikSettingsUtils;
import net.sf.ehcache.Cache;
import org.jahia.api.Constants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPublicationService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.PublicationInfo;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class used when the publish button in the page piwikSettings is pressed
 *
 * @author Cédric FOURNEAU (cedric.fourneau@chenconsulting.eu)
 *
 */
public class PublishMatomoSettingsAction extends Action {

    private static final Logger logger = LoggerFactory.getLogger(PublishMatomoSettingsAction.class);

	private JCRPublicationService publicationService;

    // Cache which keep the settings values
    private Cache cacheSettings;

    public PublishMatomoSettingsAction() {
        this.cacheSettings = MatomoCacheService.getPiwikSettingsCache();
    }

    /**
     * Action launched when the publish button in the page piwikSettings is pressed
     */
	@Override
	public ActionResult doExecute(HttpServletRequest request,
			RenderContext renderContext, Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters,
			URLResolver urlResolver) throws Exception {

		JCRSiteNode site = renderContext.getSite();
        String currentSiteKey = site.getSiteKey();
		JCRNodeWrapper settingsNode;
		if (site.hasNode(PiwikSettingsUtils.SETTINGS_NODE_NAME)) {
			settingsNode = site.getNode(PiwikSettingsUtils.SETTINGS_NODE_NAME);
            List<PublicationInfo> tree = publicationService.getPublicationInfo(settingsNode.getIdentifier(), null, true, true, true, Constants.EDIT_WORKSPACE,
                    Constants.LIVE_WORKSPACE);
            publicationService.publishByInfoList(tree, Constants.EDIT_WORKSPACE, Constants.LIVE_WORKSPACE, false, new ArrayList<String>());
            // Flush the cache to take account of values modifications
            cacheSettings.remove(PiwikSettingsUtils.ISTRACKINGDOMAIN_PROPERTY_NAME+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.ISTRACKINGJAHIACONNECTIONMODE_PROPERTY_NAME+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.ISTRACKINGJAHIALANGUAGE_PROPERTY_NAME+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.ISTRACKINGJAHIAUSERNAME_PROPERTY_NAME+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.ISTRACKINGSEARCH_PROPERTY_NAME+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.ISTRACKINGCONTENT_PROPERTY_NAME+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.SEARCH_COUNT_CSS_SELECTOR+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.SEARCH_KEYWORD_CSS_SELECTOR+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.SEARCH_RESULT_CSS_SELECTOR+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.SITE_ID+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.PIWIK_SERVER_URL+currentSiteKey);
            cacheSettings.remove(PiwikSettingsUtils.TRACKING_LIVE_ONLY+currentSiteKey);
        }else{
            logger.warn("The "+PiwikSettingsUtils.SETTINGS_NODE_NAME+" was not found. It's mandatory to save the values before publish them");
		}

		return ActionResult.OK;
	}

    /**
     * This method allows to spring to inject an instance of publicationService class
     * @param publicationService : instance of the service
     */
	public void setPublicationService(JCRPublicationService publicationService) {
		this.publicationService = publicationService;
	}

}
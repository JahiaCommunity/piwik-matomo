package org.jahiacommunity.modules.piwik.filter;

import org.jahiacommunity.modules.piwik.cache.PiwikCacheService;
import org.jahiacommunity.modules.piwik.utils.PiwikSettingsUtils;
import net.sf.ehcache.Cache;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.*;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.scripting.Script;
import org.jahia.utils.ScriptEngineUtils;
import org.jahia.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

public class PiwikTrackingContentFilter extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(PiwikTrackingContentFilter.class);

    private String template;
    private String currentSiteKey;
    private Cache cacheSettings;
    private ScriptEngineUtils scriptEngineUtils;
    private String EMPTY_DEFAULT_CATEGORY = "static";

    /**
     * Constructor
     */
    public PiwikTrackingContentFilter() {
        this.cacheSettings = PiwikCacheService.getPiwikSettingsCache();
    }

    /**
     * This method is called by JAHIA and allows to verify the following
     * condition. The PIWIK filter will be executed only on sites with PIWIK
     * modules installed.
     */
    @Override
    public boolean areConditionsMatched(RenderContext renderContext, Resource resource) {
        //rem: I don't use the 'addCondition' method and 'ExecutionCondition' implementation to check the contents/views to track for performance reasons.
        // by using '&&' (lazy) comparator, I put the most complex (or rare) condition at the end;
        // if we where implementing the 'ExecutionCondition', it will be calculated in the 'areConditionsMatched' test and could not be separated...
        return super.areConditionsMatched(renderContext, resource) && renderContext.getSite().getAllInstalledModules().contains("piwik");
    }

    private View getView(String template, Set<View> s) {
        for (View view : s) {
            if (view.getKey().equals(template)) {
                return view;
            }
        }
        return null;
    }

    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        this.currentSiteKey = renderContext.getSite().getSiteKey();
        if (isTrackingContentSetting(renderContext, currentSiteKey)) {
            JCRNodeWrapper settingsNode;
            if (renderContext.getSite().hasNode(PiwikSettingsUtils.SETTINGS_NODE_NAME)) {
                settingsNode = renderContext.getSite().getNode(PiwikSettingsUtils.SETTINGS_NODE_NAME);
                resource.getDependencies().add(settingsNode.getCanonicalPath());

                JCRNodeWrapper currentResourceNode = resource.getNode();
                //static tracking (in source code)
                try {
                    logger.debug(">>in PiwikTrackingContentFilter > in prepare");
                    //retrieve node view
                    final Script script = RenderService.getInstance().resolveScript(resource, renderContext);
                    //View view = script.getView();
                    SortedSet<View> views = RenderService.getInstance().getViewsSet(currentResourceNode.getPrimaryNodeType(), renderContext.getSite(), resource.getTemplateType());
                    View view = getView(resource.getResolvedTemplate(), views);
                    if (view != null) {
                        String viewKey = view.getKey();
                        if (logger.isDebugEnabled()) {
                            logger.debug(">>in PiwikTrackingContentFilter > view = " + view.getDisplayName() + " (" + viewKey + ")");
                        }
                        //retrieve node view "properties" file configurations:
                        Properties properties = new Properties();
                        if (script != null && view != null) {
                            properties.putAll(view.getDefaultProperties());
                            properties.putAll(view.getProperties());
                        }
                        final String trackedView = properties.getProperty("trackableView");
                        if (logger.isDebugEnabled()) {
                            logger.debug(">>in PiwikTrackingContentFilter > trackedView = " + trackedView);
                        }
                        if (!StringUtils.isEmpty(trackedView)) {
                            if (trackedView.equals("1")) {
                                final String viewType = properties.getProperty("viewType");
                                if (logger.isDebugEnabled()) {
                                    logger.debug(">>in PiwikTrackingContentFilter > viewType = " + viewType);
                                }
                                if (viewType != null && viewType.equalsIgnoreCase(viewKey)){
                                    JCRNodeWrapper article = resource.getNode();
                                    //TODO: put resource node id in name (to avoid overlapping in same page rendering...) -> not necessary
                                    chain.pushAttribute(renderContext.getRequest(), "isContentToTrack", new Boolean(true));

                                    if (!resource.hasWrapper("hidden.contentTracked")) {
                                        resource.pushWrapper("hidden.contentTracked");
                                    }
                                }
                            }
                        }
                    }
                } catch (RepositoryException rex) {
                    logger.debug(rex.getMessage());
                } catch (TemplateNotFoundException tex) {
                    logger.debug(tex.getMessage());
                }
            }
        }
        return null;
    }


    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain)
            throws Exception {
        return previousOut;
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

    class PiwikTrackingContentScriptContext extends SimpleScriptContext {
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


    private String lookupProperty(String propertyName, String currentSiteKey, RenderContext renderContext) {
        // Check if the value is in the cache
        String cacheKey = propertyName+currentSiteKey;
        if (cacheSettings == null) {
            this.cacheSettings = PiwikCacheService.getPiwikSettingsCache();
        }
        net.sf.ehcache.Element elem = cacheSettings.get(cacheKey);
        if (elem == null) {
            // If it is not in the cache we get the value from the JCR and put
            // the found value in the cache, or if the found value == null put
            // empty string ("") in cache
            try {
                if (renderContext.getSite().hasNode(PiwikSettingsUtils.SETTINGS_NODE_NAME)) {
                    JCRNodeWrapper nodeSettingsPiwik = renderContext.getSite().getNode(PiwikSettingsUtils.SETTINGS_NODE_NAME);
                    String value = nodeSettingsPiwik.getPropertyAsString(propertyName);
                    if (value == null) {
                        elem = new net.sf.ehcache.Element(cacheKey, "");
                    } else {
                        elem = new net.sf.ehcache.Element(cacheKey, value);
                    }
                    cacheSettings.put(elem);
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return elem.getObjectValue().toString();
    }


    /**
     * Test if the 'isTrackingContent' setting is set to true
     *
     */
    private boolean isTrackingContentSetting(RenderContext renderContext, String currentSiteKey) {
        // Check if the value is in the cache
        boolean isTrackingContent = (lookupProperty(PiwikSettingsUtils.ISTRACKINGCONTENT_PROPERTY_NAME, currentSiteKey, renderContext).equals("true") ? true : false);
        logger.debug(">>>>>>>>>>>>>>>>isTrackingContent="+isTrackingContent);
        return isTrackingContent;
    }
}

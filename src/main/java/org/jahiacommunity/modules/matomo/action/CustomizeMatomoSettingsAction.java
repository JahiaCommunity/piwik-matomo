package org.jahiacommunity.modules.matomo.action;

import org.jahiacommunity.modules.matomo.utils.MatomoSettingsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is used when the save button in matomoSettings page is pressed
 *
 * @author Cédric FOURNEAU (cedric.fourneau@chenconsulting.eu)
 *
 */
public class CustomizeMatomoSettingsAction extends Action {

    private static final Logger logger = LoggerFactory.getLogger(CustomizeMatomoSettingsAction.class);

    private static String settingsErrorMessage ="";

	private static Set<String> booleanProperties = new HashSet<String>();
	static {
		booleanProperties.add(MatomoSettingsUtils.ISTRACKINGSEARCH_PROPERTY_NAME);
        booleanProperties.add(MatomoSettingsUtils.ISTRACKINGCONTENT_PROPERTY_NAME);
		booleanProperties.add(MatomoSettingsUtils.ISTRACKINGJAHIACONNECTIONMODE_PROPERTY_NAME);
		booleanProperties.add(MatomoSettingsUtils.ISTRACKINGJAHIALANGUAGE_PROPERTY_NAME);
		booleanProperties.add(MatomoSettingsUtils.ISTRACKINGJAHIAUSERNAME_PROPERTY_NAME);
		booleanProperties.add(MatomoSettingsUtils.ISTRACKINGDOMAIN_PROPERTY_NAME);
		booleanProperties.add(MatomoSettingsUtils.TRACKING_LIVE_ONLY);
	}

    /**
     * Action launched when the save button in matomoSettings page is pressed
     */
	@Override
	public ActionResult doExecute(HttpServletRequest request,
			RenderContext renderContext, Resource resource,
			JCRSessionWrapper session, Map<String, List<String>> parameters,
			URLResolver urlResolver) throws Exception {

        //Initialise the error message
        this.setSettingsErrorMessage("");

		JCRSiteNode site = renderContext.getSite();
		if (parameters.keySet().contains("reset")) {
			if (site.hasNode(MatomoSettingsUtils.SETTINGS_NODE_NAME)) {
				site.getNode(MatomoSettingsUtils.SETTINGS_NODE_NAME).remove();
				session.save();
			}
			return ActionResult.OK;
		}

		JCRNodeWrapper settingsNode = null;
		if (site.hasNode(MatomoSettingsUtils.SETTINGS_NODE_NAME)) {
			settingsNode = site.getNode(MatomoSettingsUtils.SETTINGS_NODE_NAME);
		} else {
			settingsNode = site.addNode(MatomoSettingsUtils.SETTINGS_NODE_NAME, MatomoSettingsUtils.SETTINGS_NODE_TYPE);
		}

		Set<String> parametersNames = parameters.keySet();

		// Process boolean properties
		for (String checboxParameterName : booleanProperties) {
			if (!parametersNames.contains(checboxParameterName)) {
				emptySetting(settingsNode, checboxParameterName);
			}
		}

        // Process string properties
		for (String parameterName : parametersNames) {
            if ("jcrRedirectTo".equals(parameterName) || "jcrNewNodeOutputFormat".equals(parameterName) || parameterName.endsWith("_length")) {
				continue;
			}

			List<String> parameterValues = parameters.get(parameterName);
			if (parameterValues == null || parameterValues.isEmpty()) {
				continue;
			}


            if (isMultipleProperty(settingsNode, parameterName)) {
                updateMultiSetting(settingsNode, parameterName, parameterValues);
            } else {
				String parameterValue = parameterValues.get(0);

				if (StringUtils.isBlank(parameterValue)) {
					emptySetting(settingsNode, parameterName);
				} else {
					updateSetting(settingsNode, parameterName, parameterValue);
				}
			}
		}

		session.save();

		return ActionResult.OK;
	}

    /**
     * This method remove the property's value from the MATOMO settings node given in parameter
     *
     * @param node
     *            : the node where the property must be removed
     * @param propertyName
     *            : the JCR name's property which be removed
     * @throws RepositoryException
     */
	private void emptySetting(JCRNodeWrapper node, String propertyName) throws RepositoryException {
		if (node.hasProperty(propertyName)) {
			node.getProperty(propertyName).remove();
		}
	}

    /**
     * This method update the property's value from the MATOMO settings node given in parameter
     *
     * @param node
     *            : the node where the property must be updated
     * @param propertyName
     *            : the JCR name's property which be removed
     * @param propertyValue
     *            : the new property's value
     * @throws RepositoryException
     */
	private void updateSetting(JCRNodeWrapper node, String propertyName, String propertyValue) throws RepositoryException {
		node.setProperty(propertyName, propertyValue);
	}

    /**
     * This method update the property's value from the MATOMO settings node given in parameter (multi-valued)
     *
     * @param node
     *            : the node where the property must be updated
     * @param propertyName
     *            : the JCR name's property which be removed
     * @param propertyValues
     *            : the new property's values (array of String)
     * @throws RepositoryException
     */
    private void updateMultiSetting(JCRNodeWrapper node, String propertyName, List<String> propertyValues) throws RepositoryException {
        String[] values = CollectionUtils.isNotEmpty(propertyValues) ? propertyValues.toArray(new String[propertyValues.size()]) : new String[0];
        node.setProperty(propertyName, values);
    }

    /**
     * This method check if the property field is multi valued
     *
     * @param node
     *            : the node where the property must be checked
     * @param propertyName
     *            : the JCR name's property to be checked
     * @throws RepositoryException
     */
    private boolean isMultipleProperty(JCRNodeWrapper node, String propertyName) throws RepositoryException {
        boolean isMultipleValues = false;
        try {
             JCRPropertyWrapper prop = node.getProperty(propertyName);
             isMultipleValues = prop.isMultiple();
        }
        catch (RepositoryException rex){
            logger.debug("no property found ; "+rex.getMessage());
        }
        return isMultipleValues;
    }

    public String getSettingsErrorMessage() {
        return settingsErrorMessage;
    }

    public void setSettingsErrorMessage(String settingsErrorMessage) {
        CustomizeMatomoSettingsAction.settingsErrorMessage = settingsErrorMessage;
    }
}
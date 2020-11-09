<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions"%>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib"%>
<%@taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib"%>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="propertyDefinition" type="org.jahia.services.content.nodetypes.ExtendedPropertyDefinition"--%>
<%--@elvariable id="type" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<c:if test="${renderContext.mode ne 'studiovisual'}">
	<c:set var="mainResource" value="${renderContext.mainResource.node}" />

	<!-- *************************************** -->
	<!-- Retrieve settings values -->
	<!-- *************************************** -->
	<jcr:node var="settingsNode" path="${mainResource.resolveSite.path}/piwik_settings" />
	<template:addCacheDependency node="${settingsNode}"/>

	<!-- is no longer used -->
	<!--<c:set var="piwikUrl" value="" />-->
	<c:set var="siteId" value="" />
	<c:set var="piwikServerUrl" value="" />
	<c:set var="isTrackingSearch" value="${false}" />
	<c:set var="searchResultCssSelector" value="" />
	<c:set var="searchKeywordCssSelector" value="" />
	<c:set var="searchCountCssSelector" value="" />
	<c:set var="isTrackingContent" value="${false}" />
	<c:set var="isTrackingJahiaConnectionMode" value="${false}" />
	<c:set var="isTrackingJahiaLanguage" value="${false}" />
	<c:set var="isTrackingJahiaUsername" value="${false}" />
	<c:set var="isTrackingDomain" value="${false}" />
	<c:set var="trackingLiveOnly" value="${true}" />

	<c:choose>
		<c:when test="${not empty settingsNode}">

			<!-- Site ID -->
			<jcr:nodeProperty node="${settingsNode}" name="siteId" var="siteId" />

			<c:choose>
				<c:when test="${empty siteId}">
					<c:set var="siteId" value="" />
				</c:when>
				<c:otherwise>
					<c:set var="siteId" value="${siteId.string}" />
				</c:otherwise>
			</c:choose>

			<!-- Piwik/Matomo Server Url -->
			<jcr:nodeProperty node="${settingsNode}" name="piwikServerUrl" var="piwikServerUrl" />

			<c:choose>
				<c:when test="${empty piwikServerUrl}">
					<c:set var="piwikServerUrl" value="" />
				</c:when>
				<c:otherwise>
					<c:set var="piwikServerUrl" value="${piwikServerUrl.string}" />
				</c:otherwise>
			</c:choose>


			<!-- Is Tracking Search -->
			<jcr:nodeProperty node="${settingsNode}" name="isTrackingSearch" var="isTrackingSearch" />

			<c:choose>
				<c:when test="${empty isTrackingSearch}">
					<c:set var="isTrackingSearch" value="${false}" />
				</c:when>
				<c:otherwise>
					<c:set var="isTrackingSearch" value="${isTrackingSearch.boolean}" />
				</c:otherwise>
			</c:choose>

			<!-- Search result CSS selector -->
			<jcr:nodeProperty node="${settingsNode}" name="searchResultCssSelector" var="searchResultCssSelector" />

			<c:choose>
				<c:when test="${empty searchResultCssSelector}">
					<c:set var="searchResultCssSelector" value="" />
				</c:when>
				<c:otherwise>
					<c:set var="searchResultCssSelector" value="${searchResultCssSelector.string}" />
				</c:otherwise>
			</c:choose>

			<!-- Search keyword CSS selector -->
			<jcr:nodeProperty node="${settingsNode}" name="searchKeywordCssSelector" var="searchKeywordCssSelector" />

			<c:choose>
				<c:when test="${empty searchKeywordCssSelector}">
					<c:set var="searchKeywordCssSelector" value="" />
				</c:when>
				<c:otherwise>
					<c:set var="searchKeywordCssSelector" value="${searchKeywordCssSelector.string}" />
				</c:otherwise>
			</c:choose>

			<!-- Search count CSS selector -->
			<jcr:nodeProperty node="${settingsNode}" name="searchCountCssSelector" var="searchCountCssSelector" />

			<c:choose>
				<c:when test="${empty searchCountCssSelector}">
					<c:set var="searchCountCssSelector" value="" />
				</c:when>
				<c:otherwise>
					<c:set var="searchCountCssSelector" value="${searchCountCssSelector.string}" />
				</c:otherwise>
			</c:choose>

			<!-- Is Tracking Contents -->
			<%--
			<jcr:nodeProperty node="${settingsNode}" name="isTrackingContent" var="isTrackingContent" />

			<c:choose>
				<c:when test="${empty isTrackingContent}">
					<c:set var="isTrackingContent" value="${false}" />
				</c:when>
				<c:otherwise>
					<c:set var="isTrackingContent" value="${isTrackingContent.boolean}" />
				</c:otherwise>
			</c:choose>
			--%>

			<!-- Is Tracking Jahia Connection Mode -->
			<jcr:nodeProperty node="${settingsNode}" name="isTrackingJahiaConnectionMode" var="isTrackingJahiaConnectionMode" />

			<c:choose>
				<c:when test="${empty isTrackingJahiaConnectionMode}">
					<c:set var="isTrackingJahiaConnectionMode" value="${false}" />
				</c:when>
				<c:otherwise>
					<c:set var="isTrackingJahiaConnectionMode" value="${isTrackingJahiaConnectionMode.boolean}" />
				</c:otherwise>
			</c:choose>

			<!-- Is Tracking Jahia Username -->
			<jcr:nodeProperty node="${settingsNode}" name="isTrackingJahiaLanguage" var="isTrackingJahiaLanguage" />

			<c:choose>
				<c:when test="${empty isTrackingJahiaLanguage}">
					<c:set var="isTrackingJahiaLanguage" value="${false}" />
				</c:when>
				<c:otherwise>
					<c:set var="isTrackingJahiaLanguage" value="${isTrackingJahiaLanguage.boolean}" />
				</c:otherwise>
			</c:choose>

			<!-- Is Tracking Jahia Username -->
			<jcr:nodeProperty node="${settingsNode}" name="isTrackingJahiaUsername" var="isTrackingJahiaUsername" />

			<c:choose>
				<c:when test="${empty isTrackingJahiaUsername}">
					<c:set var="isTrackingJahiaUsername" value="${false}" />
				</c:when>
				<c:otherwise>
					<c:set var="isTrackingJahiaUsername" value="${isTrackingJahiaUsername.boolean}" />
				</c:otherwise>
			</c:choose>

			<!-- Is Tracking Domain -->
			<jcr:nodeProperty node="${settingsNode}" name="isTrackingDomain" var="isTrackingDomain" />

			<c:choose>
				<c:when test="${empty isTrackingDomain}">
					<c:set var="isTrackingDomain" value="${false}" />
				</c:when>
				<c:otherwise>
					<c:set var="isTrackingDomain" value="${isTrackingDomain.boolean}" />
				</c:otherwise>
			</c:choose>

			<!-- If script piwik for Tracking is integrated only in live-->
			<jcr:nodeProperty node="${settingsNode}" name="trackingLiveOnly" var="trackingLiveOnly" />

			<c:choose>
				<c:when test="${empty trackingLiveOnly}">
					<c:set var="trackingLiveOnly" value="${false}" />
				</c:when>
				<c:otherwise>
					<c:set var="trackingLiveOnly" value="${trackingLiveOnly.boolean}" />
				</c:otherwise>
			</c:choose>


		</c:when>
	</c:choose>





	<!-- *************************************** -->
	<!-- Display things starting from here -->
	<!-- *************************************** -->
	<template:addResources type="javascript" resources="workInProgress.js" />

	<fmt:message key="jcpwnt_piwikSettings.workInProgressTitle.label" var="i18nWaiting" />
	<c:set var="i18nWaiting" value="${functions:escapeJavaScript(i18nWaiting)}" />

	<!-- *************************************** -->
	<!-- Top header -->
	<!-- *************************************** -->
	<h1 id="header${mainResource.identifier}">
		<fmt:message key="jcpwnt_piwikSettings.page.title" />
	</h1>

	<form id="customize${mainResource.identifier}"
		action="<c:url value='${url.base}${renderContext.site.path}.customizeMatomoSettings.do'/>"
		method="post"
		class="box-1"
		onsubmit="workInProgress('${i18nWaiting}')">

		<!-- Site ID -->
		<div class="row-fluid">
			<div class="span3">
				<label for="siteId"><fmt:message key="jcpwnt_piwikSettings.siteId.title" /></label>
			</div>
			<div class="span9">
				<input class="input-xxlarge" type="text" id="siteId" name="siteId" value="${fn:escapeXml(siteId)}" />
			</div>
		</div>


		<!-- Piwik/Matomo Server Url -->
		<div class="row-fluid">
			<div class="span3">
				<label for="piwikServerUrl"><fmt:message key="jcpwnt_piwikSettings.piwikServerUrl.title" /></label>
			</div>
			<div class="span9">
				<input class="input-xxlarge" type="text" id="piwikServerUrl" name="piwikServerUrl" value="${fn:escapeXml(piwikServerUrl)}" />
			</div>
		</div>

		<!-- Is Tracking Search -->
		<c:set var="isTrackingSearchChecked" value="" />
		<c:if test="${isTrackingSearch}">
			<c:set var="isTrackingSearchChecked" value="checked" />
		</c:if>
		<div class="row-fluid">
			<div class="span3">
				<label for="isTrackingSearch"><fmt:message key="jcpwnt_piwikSettings.isTrackingSearch.title" /></label>
			</div>
			<div class="span9">
				<input type="checkbox" id="isTrackingSearch" name="isTrackingSearch" value="true" ${isTrackingSearchChecked} />
			</div>
		</div>

		<!-- Search result CSS Selector -->
		<div class="row-fluid">
			<div class="span3">
				<label for="searchResultCssSelector"><fmt:message key="jcpwnt_piwikSettings.searchResultCssSelector.title" /></label>
			</div>
			<div class="span9">
				<input class="input-xxlarge" type="text" id="searchResultCssSelector" name="searchResultCssSelector" value="${fn:escapeXml(searchResultCssSelector)}" />
			</div>
		</div>

		<!-- Search keyword CSS Selector -->
		<div class="row-fluid">
			<div class="span3">
				<label for="searchKeywordCssSelector"><fmt:message key="jcpwnt_piwikSettings.searchKeywordCssSelector.title" /></label>
			</div>
			<div class="span9">
				<input class="input-xxlarge" type="text" id="searchKeywordCssSelector" name="searchKeywordCssSelector" value="${fn:escapeXml(searchKeywordCssSelector)}" />
			</div>
		</div>

		<!-- Search count CSS Selector -->
		<div class="row-fluid">
			<div class="span3">
				<label for="searchCountCssSelector"><fmt:message key="jcpwnt_piwikSettings.searchCountCssSelector.title" /></label>
			</div>
			<div class="span9">
				<input class="input-xxlarge" type="text" id="searchCountCssSelector" name="searchCountCssSelector" value="${fn:escapeXml(searchCountCssSelector)}" />
			</div>
		</div>

		<!-- Is Tracking Content -->
		<%--
		<c:set var="isTrackingContentChecked" value="" />
		<c:if test="${isTrackingContent}">
			<c:set var="isTrackingContentChecked" value="checked" />
		</c:if>
		<div class="row-fluid">
			<div class="span3">
				<label for="isTrackingContent"><fmt:message key="jcpwnt_piwikSettings.isTrackingContent.title" /></label>
			</div>
			<div class="span9">
				<input type="checkbox" id="isTrackingContent" name="isTrackingContent" value="true" ${isTrackingContentChecked} />
			</div>
		</div>
		--%>

		<!-- Is Tracking Jahia Connection Mode -->
		<c:set var="isTrackingJahiaConnectionModeChecked" value="" />
		<c:if test="${isTrackingJahiaConnectionMode}">
			<c:set var="isTrackingJahiaConnectionModeChecked" value="checked" />
		</c:if>
		<div class="row-fluid">
			<div class="span3">
				<label for="isTrackingJahiaConnectionMode"><fmt:message key="jcpwnt_piwikSettings.isTrackingJahiaConnectionMode.title" /></label>
			</div>
			<div class="span9">
				<input type="checkbox" id="isTrackingJahiaConnectionMode" name="isTrackingJahiaConnectionMode" value="true" ${isTrackingJahiaConnectionModeChecked} />
			</div>
		</div>

		<!-- Is Tracking Jahia Language -->
		<c:set var="isTrackingJahiaLanguageChecked" value="" />
		<c:if test="${isTrackingJahiaLanguage}">
			<c:set var="isTrackingJahiaLanguageChecked" value="checked" />
		</c:if>
		<div class="row-fluid">
			<div class="span3">
				<label for="isTrackingJahiaLanguage"><fmt:message key="jcpwnt_piwikSettings.isTrackingJahiaLanguage.title" /></label>
			</div>
			<div class="span9">
				<input type="checkbox" id="isTrackingJahiaLanguage" name="isTrackingJahiaLanguage" value="true" ${isTrackingJahiaLanguageChecked} />
			</div>
		</div>

		<!-- Is Tracking Jahia Username -->
		<c:set var="isTrackingJahiaUsernameChecked" value="" />
		<c:if test="${isTrackingJahiaUsername}">
			<c:set var="isTrackingJahiaUsernameChecked" value="checked" />
		</c:if>
		<div class="row-fluid">
			<div class="span3">
				<label for="isTrackingJahiaUsername"><fmt:message key="jcpwnt_piwikSettings.isTrackingJahiaUsername.title" /></label>
			</div>
			<div class="span9">
				<input type="checkbox" id="isTrackingJahiaUsername" name="isTrackingJahiaUsername" value="true" ${isTrackingJahiaUsernameChecked} />
			</div>
		</div>

		<!-- Is Tracking Domain -->
		<c:set var="isTrackingDomainChecked" value="" />
		<c:if test="${isTrackingDomain}">
			<c:set var="isTrackingDomainChecked" value="checked" />
		</c:if>
		<div class="row-fluid">
			<div class="span3">
				<label for="isTrackingDomain"><fmt:message key="jcpwnt_piwikSettings.isTrackingDomain.title" /></label>
			</div>
			<div class="span9">
				<input type="checkbox" id="isTrackingDomain" name="isTrackingDomain" value="true" ${isTrackingDomainChecked} />
			</div>
		</div>

		<!-- Tracking live only -->
		<c:set var="trackingLiveOnlyChecked" value="" />
		<c:if test="${trackingLiveOnly}">
			<c:set var="trackingLiveOnlyChecked" value="checked" />
		</c:if>
		<div class="row-fluid">
			<div class="span3">
				<label for="trackingLiveOnly"><fmt:message key="jcpwnt_piwikSettings.trackingLiveOnly.title"/></label>
			</div>
			<div class="span9">
				<input type="checkbox" id="trackingLiveOnly" name="trackingLiveOnly" value="true" ${trackingLiveOnlyChecked} />
			</div>
		</div>

		<!-- *************************************** -->
		<!-- Hidden fields used by Actions -->
		<!-- *************************************** -->

		<input type="hidden" name="jcrRedirectTo" value="<c:url value='${url.base}${mainResource.path}'/>" />
		<input type="hidden" name="jcrNewNodeOutputFormat" value="<c:url value='${renderContext.mainResource.template}.html'/>">


		<!-- *************************************** -->
		<!-- Form buttons -->
		<!-- *************************************** -->
		<div class="row-fluid">
			<div class="span12">

				<!-- Submit button -->
				<button class="btn btn-primary" type="submit">
					<i class="icon-ok icon-white"></i>
					<fmt:message key='jcpwnt_piwikSettings.save.btn.label' />
				</button>


				<!-- Reset button -->
				<fmt:message key="jcpwnt_piwikSettings.reset.confirm" var="resetConfirm" />

				<button class="btn btn-danger" type="button"
					onclick="if (confirm('${resetConfirm}')) {$('#reset${mainResource.identifier}').submit()}">
					<i class="icon-refresh icon-white"></i>
					<fmt:message key='jcpwnt_piwikSettings.reset.btn.label' />
				</button>

				<!-- Publish button -->
				<button class="btn" name="publish" type="button"
					onclick="$('#publish${mainResource.identifier}').submit()">
					<i class="icon-globe"></i>
					<fmt:message key='jcpwnt_piwikSettings.publish.btn.label' />
				</button>
			</div>
		</div>

	</form>

	<!-- *************************************** -->
	<!-- Hidden form to reset values -->
	<!-- *************************************** -->
	<form id="reset${mainResource.identifier}"
		action="<c:url value='${url.base}${renderContext.site.path}.customizeMatomoSettings.do'/>"
		method="post" onsubmit="workInProgress('${i18nWaiting}')">

		<input
			type="hidden"
			name="reset"
			value="true" />
		<input
			type="hidden"
			name="jcrRedirectTo"
			value="<c:url value='${url.base}${mainResource.path}'/>" />
		<input
			type="hidden"
			name="jcrNewNodeOutputFormat"
			value="<c:url value='${renderContext.mainResource.template}.html'/>">
	</form>

	<!-- *************************************** -->
	<!-- Hidden form to publish values -->
	<!-- *************************************** -->
	<form id="publish${mainResource.identifier}"
		onsubmit="workInProgress('${i18nWaiting}')"
		method="post"
		action="<c:url value='${url.base}${renderContext.site.path}.publishMatomoSettings.do'/>">

		<input
			type="hidden"
			value="<c:url value='${url.base}${mainResource.path}'/>"
			name="jcrRedirectTo">
		<input type="hidden"
			value="<c:url value='${renderContext.mainResource.template}.html'/>"
			name="jcrNewNodeOutputFormat">
	</form>
</c:if>

<c:if test="${renderContext.mode == 'studiovisual'}">
	${fn:escapeXml(currentNode.displayableName)}
</c:if>
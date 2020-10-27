package org.jahiacommunity.modules.piwik.cache;

import org.jahiacommunity.modules.piwik.utils.PiwikSettingsUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import org.jahia.services.cache.ehcache.EhCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gives access to the different caches used in the PIWIK module
 *
 * @author Cédric FOURNEAU (cedric.fourneau@chenconsulting.eu)
 *
 */
public class PiwikCacheService {

    private static final Logger logger = LoggerFactory.getLogger(PiwikCacheService.class);

    private static EhCacheProvider ehCacheProvider;
    private static Cache cachePiwikSettings;

    /**
     * This method allows to spring to inject an instance of EhCacheProvider class and initialise caches used.
     * @param ehCacheProvider
     */
    public PiwikCacheService(EhCacheProvider ehCacheProvider) {
        PiwikCacheService.ehCacheProvider = ehCacheProvider;
        initializeCache();
    }

    /**
     * Initialise caches used in the PIWIK module
     */
    private void initializeCache() {
        try {
            if (cachePiwikSettings == null) {
                if (!ehCacheProvider.getCacheManager().cacheExists(PiwikSettingsUtils.SETTINGS_NAME_CACHE)) {
                    cachePiwikSettings = new Cache(PiwikSettingsUtils.SETTINGS_NAME_CACHE, PiwikSettingsUtils.SETTINGS_MAX_ELEMENTS_IN_MEMORY_CACHE,
                            PiwikSettingsUtils.SETTINGS_OVERFLOW_TO_DISK_CACHE, PiwikSettingsUtils.SETTINGS_ETERNAL_CACHE, PiwikSettingsUtils.SETTINGS_TIME_TO_LIVE_SECONDS_CACHE,
                            PiwikSettingsUtils.SETTINGS_TIME_TO_IDLE_SECONDS);
                    ehCacheProvider.getCacheManager().addCache(cachePiwikSettings);
                }
                cachePiwikSettings = ehCacheProvider.getCacheManager().getCache(PiwikSettingsUtils.SETTINGS_NAME_CACHE);
            }
        } catch (IllegalStateException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (CacheException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * @return cachePiwikSettings : Cache to retain the settings previously published in the live JCR
     */
    public static Cache getPiwikSettingsCache(){
        return cachePiwikSettings;
    }

}

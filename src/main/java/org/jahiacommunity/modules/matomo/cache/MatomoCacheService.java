package org.jahiacommunity.modules.matomo.cache;

import org.jahiacommunity.modules.matomo.utils.MatomoSettingsUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import org.jahia.services.cache.ehcache.EhCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gives access to the different caches used in the MATOMO module
 *
 * @author Cédric FOURNEAU (cedric.fourneau@chenconsulting.eu)
 *
 */
public class MatomoCacheService {

    private static final Logger logger = LoggerFactory.getLogger(MatomoCacheService.class);

    private static EhCacheProvider ehCacheProvider;
    private static Cache cacheMatomoSettings;

    /**
     * This method allows to spring to inject an instance of EhCacheProvider class and initialise caches used.
     * @param ehCacheProvider
     */
    public MatomoCacheService(EhCacheProvider ehCacheProvider) {
        MatomoCacheService.ehCacheProvider = ehCacheProvider;
        initializeCache();
    }

    /**
     * Initialise caches used in the MATOMO module
     */
    private void initializeCache() {
        try {
            if (cacheMatomoSettings == null) {
                if (!ehCacheProvider.getCacheManager().cacheExists(MatomoSettingsUtils.SETTINGS_NAME_CACHE)) {
                    cacheMatomoSettings = new Cache(MatomoSettingsUtils.SETTINGS_NAME_CACHE, MatomoSettingsUtils.SETTINGS_MAX_ELEMENTS_IN_MEMORY_CACHE,
                            MatomoSettingsUtils.SETTINGS_OVERFLOW_TO_DISK_CACHE, MatomoSettingsUtils.SETTINGS_ETERNAL_CACHE, MatomoSettingsUtils.SETTINGS_TIME_TO_LIVE_SECONDS_CACHE,
                            MatomoSettingsUtils.SETTINGS_TIME_TO_IDLE_SECONDS);
                    ehCacheProvider.getCacheManager().addCache(cacheMatomoSettings);
                }
                cacheMatomoSettings = ehCacheProvider.getCacheManager().getCache(MatomoSettingsUtils.SETTINGS_NAME_CACHE);
            }
        } catch (IllegalStateException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (CacheException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * @return cacheMatomoSettings : Cache to retain the settings previously published in the live JCR
     */
    public static Cache getMatomoSettingsCache(){
        return cacheMatomoSettings;
    }

}

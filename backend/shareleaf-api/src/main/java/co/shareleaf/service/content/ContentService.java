package co.shareleaf.service.content;

import co.shareleaf.model.SLContentId;
import co.shareleaf.model.SLContentMetadata;
import co.shareleaf.model.SLContentUrl;
import co.shareleaf.model.SLGenericResponse;

/**
 * @author Bizuwork Melesse
 * created on 6/12/22
 */
public interface ContentService {

    /**
     * Generate a unique content uid
     * @param url
     * @return
     */
    SLContentMetadata generateContentId(SLContentUrl url);

    /**
     * Fetch full content metadata
     * @param uid content id
     * @return
     */
    SLContentMetadata getMetadata(String uid);

    /**
     * Increment the share count of a content
     * @return
     */
    SLGenericResponse incrementShareCount(SLContentId contentId);
}

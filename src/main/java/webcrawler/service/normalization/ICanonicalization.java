package webcrawler.service.normalization;

import webcrawler.model.ParsedUrlEx;

public interface ICanonicalization {
    ParsedUrlEx canonicalize(final String parentUrl, final String relativeOrAbsUrl);
}

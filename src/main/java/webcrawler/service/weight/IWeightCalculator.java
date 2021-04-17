package webcrawler.service.weight;

import webcrawler.model.UrlInfo;

public interface IWeightCalculator {
    double calculate(UrlInfo urlInfo);
}

package webcrawler.service.weight;

import webcrawler.model.UrlInfo;

public class WeightCalculatorImpl implements IWeightCalculator{
    @Override
    public double calculate(UrlInfo urlInfo) {
        final double weightInlink = urlInfo.getInLinks().size() / 50.0 / 100.0;
        final double weightRCUrl = urlInfo.getRelevancyCountUrl() / 20.0 / 100.0;
        final double weightRCTitle = urlInfo.getRelevancyCountTitle() / 20.0 / 100.0;
        final double weightDepth = urlInfo.getDepth() / 10.0 / 100.0;
        return weightInlink + weightRCUrl + weightRCTitle + weightDepth;
    }
}

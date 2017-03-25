package org.nova.metrics;

public class CategoryMeters
{
	final private LevelMeterBox[] levelMeterBoxes;
	final private RateMeterBox[] rateMeterBoxes;
	final private CountMeterBox[] countMeterBoxes;
	final private CountAverageRateMeterBox[] countAverageMeterBoxes;
	
	public CategoryMeters(LevelMeterBox[] levelMeterBoxes,RateMeterBox[] rateMeterBoxes,CountMeterBox[] countMeterBoxes,CountAverageRateMeterBox[] countAverageMeterBoxes)
	{
		this.levelMeterBoxes=levelMeterBoxes;
		this.rateMeterBoxes=rateMeterBoxes;
		this.countMeterBoxes=countMeterBoxes;
		this.countAverageMeterBoxes=countAverageMeterBoxes;
	}

	
	public LevelMeterBox[] getLevelMeterBoxes()
	{
		return levelMeterBoxes;
	}


	public CountMeterBox[] getCountMeterBoxes()
	{
		return countMeterBoxes;
	}


	public RateMeterBox[] getRateMeterBoxes()
	{
		return rateMeterBoxes;
	}

	public CountAverageRateMeterBox[] getCountAverageMeterBoxes()
	{
		return countAverageMeterBoxes;
	}

}
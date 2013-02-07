package omr.symbol_recogntion.segmentation;

import java.awt.image.BufferedImage;
import java.util.List;

import omr.symbol_recogntion.score_metrics.ScoreMetrics;

public class Score {

	private BufferedImage score_image;
	private List<Stave> stave_list;
	private ScoreMetrics score_metrics;
	
	private String scoreImageSourceFilePath;

	//constructors /////////////////////////////////////////////////////////////////////////////////////////////////////
	public Score() {

	}

	public Score(BufferedImage image_of_score) {
		score_image = image_of_score;
	}

	public Score(BufferedImage image_of_score, List<Stave> list_of_staves) {
		score_image = image_of_score;
		stave_list = list_of_staves;
	}

	
	// get set /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String getScoreSourceFilePath() {
		return scoreImageSourceFilePath;
	}
	
	public void setScoreSourceFilePath(String score_filePath) {
		this.scoreImageSourceFilePath = score_filePath;
	}
	
	public BufferedImage getScore_image() {
		return score_image;
	}

	public void setScore_image(BufferedImage score_image) {
		this.score_image = score_image;
	}

	public List<Stave> getStave_list() {
		return stave_list;
	}

	public void setStave_list(List<Stave> stave_list) {
		this.stave_list = stave_list;
	}

	public void setScoreMetrics(ScoreMetrics score_metrics) {
		this.score_metrics = score_metrics;
		
	}

	public ScoreMetrics getScoreMetrics() {
		return this.score_metrics;
	}


}

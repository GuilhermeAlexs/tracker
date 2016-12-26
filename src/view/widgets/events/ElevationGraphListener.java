package view.widgets.events;

public interface ElevationGraphListener {
	void onGraphSelectionFinished(int selStart, int selEnd);
	void onGraphSelectionMoving(int selStart, int selEnd);
	void onGraphPredictTimeRequested();
	void onGraphClearRequest();
}

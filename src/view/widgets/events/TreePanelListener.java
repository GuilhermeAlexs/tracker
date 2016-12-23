package view.widgets.events;

public interface TreePanelListener {
	void onTreeNodeSelected(Object data, boolean isInDB);
	void onTreeNodeDeleted(Object data, boolean selectedWasAimed, boolean isInDB);
	void onTreeNodePredictRequested(Object data, boolean selectedWasAimed, boolean isInDB);
	void onTreeNodeAddedToDB(Object data, boolean selectedWasAimed);
}

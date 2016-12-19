package view.widgets.events;

public interface TreePanelListener {
	void onTreeNodeSelected(Object data, boolean isInDB);
	void onTreeNodeDeleted(Object data, boolean selectedWasRemoved, boolean isInDB);
	void onTreeNodeAddedToDB(Object data);
}

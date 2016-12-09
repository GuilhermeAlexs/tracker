package utils.listeners;

public interface KmlParseProgressListener {
	public void onPreParse(int progressTotal);
	public void onParseProgress(int progress);
	public void onParseFinish();
}

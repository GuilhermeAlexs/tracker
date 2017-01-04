package model;

import java.util.Iterator;
import java.util.List;

public class StretchIterator implements Iterator<Stretch> {
	private Iterator<TPLocation> it;
	private TPLocation lastLoc;
	private TPLocation currLoc;
	
	public StretchIterator(List<TPLocation> trail) {
		this.it = trail.iterator();
		this.lastLoc = null;
		this.currLoc = it.next();
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public Stretch next() {
		lastLoc = currLoc;
		currLoc = it.next();

		if(lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_INVALID)){
			while(it.hasNext()){
				lastLoc = currLoc;
				currLoc = it.next();
				
				if(!currLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_INVALID))
					break;
			}
		}
		
		return new Stretch(lastLoc, currLoc);
	}
	
	@Override
	public void remove() {
	}
}

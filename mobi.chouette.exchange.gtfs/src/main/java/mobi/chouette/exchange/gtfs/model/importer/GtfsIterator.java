package mobi.chouette.exchange.gtfs.model.importer;

import java.nio.ByteBuffer;

public interface GtfsIterator {

	public abstract void dispose();

	public abstract boolean hasNext();

	public abstract Boolean next();

	public abstract void remove();

	public abstract String getValue();

	public abstract String getValue(int index);

	public abstract int getFieldCount();

	public abstract String getCode();

	public abstract int getPosition();

	public abstract void setPosition(int position);

	public abstract ByteBuffer getBuffer();

	public abstract void setByteBuffer(ByteBuffer buffer);

}
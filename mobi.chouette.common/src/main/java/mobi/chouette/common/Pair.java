package mobi.chouette.common;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Pair<L, R> implements Serializable {

	private static final long serialVersionUID = -1L;

	@Getter
	public final L left;
	@Getter
	public final R right;

	public static <L, R> Pair<L, R> of(final L left, final R right) {
		return new Pair<L, R>(left, right);
	}

	public Pair(final L left, final R right) {
		super();
		this.left = left;
		this.right = right;
	}

}

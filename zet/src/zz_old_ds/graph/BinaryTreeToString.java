/**
 * BinaryTreeToString.java
 * Created: 24.07.2012, 17:39:12
 */
package zz_old_ds.graph;

import zz_old_de.tu_berlin.math.coga.datastructure.searchtree.BinaryTree;
import org.zetool.graph.Node;
import org.zetool.container.mapping.IdentifiableMapping;
import java.util.LinkedList;
import java.util.Queue;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BinaryTreeToString {

	private BinaryTree bt;
	private IdentifiableMapping d;

	public static String format( BinaryTree bt ) {
		BinaryTreeToString btts = new BinaryTreeToString( bt );
		return btts.formatTree();
	}

	public static String format( BinaryTree bt, IdentifiableMapping d ) {
		BinaryTreeToString btts = new BinaryTreeToString( bt, d );
		return btts.formatTree();
	}

	private BinaryTreeToString( BinaryTree bt ) {
		this.bt = bt;
	}

	private BinaryTreeToString( BinaryTree bt, IdentifiableMapping d ) {
		this.bt = bt;
		this.d = d;
	}

	private String formatTree() {
		if (bt.isEmpty())
			return "EMPTY";
		int dist = 2;
		int height = bt.getHeight();
		Queue<ToStringTripel> ordered = getOrderNodes();
		int maxLength = getMaxStringLength(ordered);
		int subTreeLength = (int) Math.pow(2, height - 1) * (maxLength + 4 + dist);
		int offset = Math.max(0, (int) Math.pow(2, height - getMostLeftRow() - 1) * (maxLength + 4 + dist) - maxLength / 2 - 3);

		StringBuilder builder = new StringBuilder();
		int maxNodesInLine = 1;
		Queue<ToStringTripel> line;
		while (!ordered.isEmpty()) {
			line = new LinkedList<>();
			line.add(ordered.poll());
			while (!ordered.isEmpty() && line.peek().row == ordered.peek().row)
				line.add(ordered.poll());
			builder.append(getNodeLine(maxLength, subTreeLength, maxNodesInLine, line, offset));
			maxNodesInLine *= 2;
			subTreeLength /= 2;
		}
		return builder.toString();
	}

	private Queue<ToStringTripel> getOrderNodes( ) {
		Queue<ToStringTripel> strings = new LinkedList<>();
		strings.add(new ToStringTripel(bt.getRoot(), 0, 0));
		Queue<ToStringTripel> ordered = new LinkedList<>();
		orderNodes(strings, ordered);
		return ordered;
	}

	private void orderNodes(Queue<ToStringTripel> strings, Queue<ToStringTripel> ordered) {
		if (strings.isEmpty())
			return;
		ToStringTripel node = strings.poll();
		ordered.add(node);
		if (node.hasLeft())
			strings.add(node.getLeft());
		if (node.hasRight())
			strings.add(node.getRight());
		orderNodes(strings, ordered);
	}

	private int getMaxStringLength(Queue<ToStringTripel> ordered) {
		int maxLength = 0;
		for (ToStringTripel node : ordered)
			maxLength = Math.max(maxLength, node.getLength());
		return maxLength % 2 == 0 ? maxLength : maxLength + 1;
	}

	private int getMostLeftRow() {
		int mostLeftRow = 0;
		Node node = bt.getRoot();
		while ( bt.getLeft( node ) != null ) {
			node = bt.getLeft( node );
			mostLeftRow++;
		}
		return mostLeftRow;
	}

	private String getNodeLine(int maxLength, int subTreeLength, int maxNodes, Queue<ToStringTripel> line, int offset) {
		StringBuilder builder = new StringBuilder();
		StringBuilder overUnder = new StringBuilder();
		StringBuilder nodeInfos = new StringBuilder();
		StringBuilder children1 = new StringBuilder();
		StringBuilder children2 = new StringBuilder();
		for (int i = 0; i < maxNodes; i++)
			if (line.isEmpty() || i != line.peek().column) {
				String spaces = getSignString(2 * subTreeLength, " ");
				overUnder.append(spaces);
				nodeInfos.append(spaces);
				children1.append(spaces);
				children2.append(spaces);
			} else {
				overUnder.append(getOverUnderLine(maxLength, subTreeLength));
				nodeInfos.append(getNodeInfo(maxLength, subTreeLength, line.peek().toString()));
				String[] childrenInfos = getChildrenStrings(maxLength, subTreeLength, line.peek().node);
				children1.append(childrenInfos[0]);
				children2.append(childrenInfos[1]);
				line.poll();
			}
		builder.append(overUnder.toString().substring(offset) + "\n");
		builder.append(nodeInfos.toString().substring(offset) + "\n");
		builder.append(overUnder.toString().substring(offset) + "\n");
		builder.append(children1.toString().substring(offset) + "\n");
		builder.append(children2.toString().substring(offset) + "\n");
		return builder.toString();
	}

	private String[] getChildrenStrings(int maxLength, int subTreeLength, Node node) {
		StringBuilder builder1 = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		builder1.append(getSignString(subTreeLength / 2, " "));
		builder2.append(getSignString(subTreeLength / 2, " "));
		if (bt.getLeft( node ) != null ) {
			builder1.append(" ");
			builder2.append("/");
			builder1.append(getSignString(subTreeLength / 2 - 3, "_"));
			builder1.append("/ ");
			builder2.append(getSignString(subTreeLength / 2 - 1, " "));
		} else {
			builder1.append(getSignString(subTreeLength / 2, " "));
			builder2.append(getSignString(subTreeLength / 2, " "));
		}
		if ( bt.getRight( node ) != null ) {
			builder1.append(" \\");
			builder2.append(getSignString(subTreeLength / 2 - 1, " "));
			builder1.append(getSignString(subTreeLength / 2 - 3, "_"));
			builder1.append(" ");
			builder2.append("\\");
		} else {
			builder1.append(getSignString(subTreeLength / 2, " "));
			builder2.append(getSignString(subTreeLength / 2, " "));
		}
		builder1.append(getSignString(subTreeLength / 2, " "));
		builder2.append(getSignString(subTreeLength / 2, " "));
		return new String[] { builder1.toString(), builder2.toString() };
	}

	private String getOverUnderLine(int maxLength, int subTreeLength) {
		StringBuilder builder = new StringBuilder();
		builder.append(getSignString(subTreeLength - maxLength / 2 - 2, " "));
		builder.append(getSignString(maxLength + 4, "#"));
		builder.append(getSignString(subTreeLength - maxLength / 2 - 2, " "));
		return builder.toString();
	}

	private String getNodeInfo(int maxLength, int subTreeLength, String nodeInfo) {
		StringBuilder builder = new StringBuilder();
		builder.append(getSignString(subTreeLength - maxLength / 2 - 2, " "));
		builder.append("# ");
		builder.append(getSignString((int) Math.ceil((maxLength - nodeInfo.length()) / 2.), " "));
		builder.append(nodeInfo);
		builder.append(getSignString((int) Math.floor((maxLength - nodeInfo.length()) / 2.), " "));
		builder.append(" #");
		builder.append(getSignString(subTreeLength - maxLength / 2 - 2, " "));
		return builder.toString();
	}

	private String getSignString(int subTreeLength, String sign) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < subTreeLength; i++)
			builder.append(sign);
		return builder.toString();
	}

	private class ToStringTripel {
		private Node node;
		private int row;
		private int column;

		private ToStringTripel( Node node, int row, int column) {
			this.node = node;
			this.row = row;
			this.column = column;
		}

		private int getLength() {
			return toString().length();
		}

		private boolean hasLeft() {
			return bt.getLeft( node ) != null;
		}

		private boolean hasRight() {
			return bt.getRight( node ) != null;
		}

		private ToStringTripel getLeft() {
			return new ToStringTripel(bt.getLeft( node ), row + 1, 2 * column);
		}

		private ToStringTripel getRight() {
			return new ToStringTripel(bt.getRight( node ), row + 1, 2 * column + 1);
		}

		@Override
		public String toString() {
			//if(node.getData() == null)
			//	return "";
			//if (node.getData().toString().equals("" + (char)10))
			//	return "\\n";
			//if (node.getData().toString().equals("" + (char)13))
			//	return "\\vt";
			//String[] str = node.getData().toString().split("" + (char)10);
			String[] str = node.toString().split("" + (char)10 );
			if( d != null ) {
				str = d.get( node ).toString().split("" + (char)10 );
			}


			StringBuilder builder = new StringBuilder();
			builder.append(str[0]);
			for (int i = 1; i < str.length; i++)
				builder.append(" \\n " + str[i]);
			str = builder.toString().split("" + (char)13);
			builder = new StringBuilder();
			builder.append(str[0]);
			for (int i = 1; i < str.length; i++)
				builder.append(" \\vt " + str[i]);
			return builder.toString();
		}
	}
}

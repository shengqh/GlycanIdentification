package edu.iu.informatics.omics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlycanNode {
	private IMonosaccharide node;

	private List<GlycanNode> nonReduncingNodes = new ArrayList<GlycanNode>();

	private GlycanNode reducingNode;

	private int reducingNodeConnectionType;

	public IMonosaccharide getNode() {
		return node;
	}

	public void setNode(IMonosaccharide node) {
		this.node = node;
	}

	public GlycanNode getReducingNode() {
		return reducingNode;
	}

	public void setReducingNode(GlycanNode reducingNode) {
		this.reducingNode = reducingNode;
	}

	public List<GlycanNode> getNonReduncingNodes() {
		return Collections.unmodifiableList(nonReduncingNodes);
	}

	public void addNonReducingNode(GlycanNode addNode, int connectionType) {
		if (!nonReduncingNodes.contains(addNode)) {
			nonReduncingNodes.add(addNode);
			addNode.setReducingNode(this);
			addNode.setReducingNodeConnectionType(connectionType);
		}
	}

	public void removeNonReducingNode(GlycanNode removeNode) {
		if (nonReduncingNodes.contains(removeNode)) {
			nonReduncingNodes.remove(removeNode);
			removeNode.setReducingNode(null);
			removeNode.setReducingNodeConnectionType(0);
		}
	}

	public int getReducingNodeConnectionType() {
		return reducingNodeConnectionType;
	}

	public void setReducingNodeConnectionType(int reducingNodeConnectionType) {
		this.reducingNodeConnectionType = reducingNodeConnectionType;
	}
}

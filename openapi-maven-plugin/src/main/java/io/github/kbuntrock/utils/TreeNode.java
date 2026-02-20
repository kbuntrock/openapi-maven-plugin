package io.github.kbuntrock.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TreeNode<T> {
	private final T value;
	private final List<TreeNode<T>> children = new ArrayList<>();
	private TreeNode<T> parent;

	public TreeNode(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public TreeNode<T> addChild(T childValue) {
		TreeNode<T> child = new TreeNode<>(childValue);
		child.parent = this;
		children.add(child);
		return child;
	}

	public void visitDepthFirst(Consumer<TreeNode<T>> visitor) {
		visitor.accept(this);
		for(TreeNode<T> c : children) {
			c.visitDepthFirst(visitor);
		}
	}
}
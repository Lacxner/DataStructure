package org.gzy.tree;

import org.gzy.queue.queue.LightQueue;
import org.gzy.tree.printer.BinaryTreeInfo;

import java.util.function.Consumer;

/**
 * 二叉树的抽象类
 * @author GaoZiYang
 * @since 2021年08月23日 00:09:48
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class AbstractBinaryTree<E> implements BinaryTree, BinaryTreeInfo {
    /**
     * 元素数量
     */
    protected int size;
    /**
     * 根节点
     */
    protected Node<E> root;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * 元素节点
     * @param <E> 元素类型
     */
    protected static class Node<E> {
        /**
         * 节点元素
         */
        protected E element;

        /**
         * 左节点
         */
        protected Node<E> left;

        /**
         * 右节点
         */
        protected Node<E> right;

        /**
         * 父节点
         */
        protected Node<E> parent;

        protected Node(E e, Node<E> parent) {
            this.element = e;
            this.parent = parent;
        }

        /**
         * 判断是否为叶子节点
         * @return 是否为叶子节点
         */
        protected boolean isLeaf() {
            return left == null && right == null;
        }

        /**
         * 判断是否为父节点的左子节点
         * @return 是否为父节点的左子节点
         */
        protected boolean isLeftChild() {
            return parent != null && this == parent.left;
        }

        /**
         * 判断是否为父节点的右子节点
         * @return 是否为父节点的右子节点
         */
        protected boolean isRightChild() {
            return parent != null && this == parent.right;
        }

        /**
         * 获取兄弟节点
         * @return 兄弟节点
         */
        protected Node<E> sibling() {
            if (isLeftChild()) {
                return parent.right;
            } else if (isRightChild()) {
                return parent.left;
            } else {
                return null;
            }
        }

        /**
         * 获取叔叔节点，即父节点的兄弟节点
         * @return 叔叔节点
         */
        protected Node<E> uncle() {
            if (parent == null) return null;
            return parent.sibling();
        }

        @Override
        public String toString() {
            return element.toString();
        }
    }

    /**
     * 元素非空检测
     * @param e 要检测的元素
     */
    protected void checkElementNotNull(E e) {
        if (e == null) throw new IllegalArgumentException("元素不能为空！");
    }

    @Override
    public int height() {
        return height(root);
    }

    private int height(Node<E> node) {
        if (node == null) return 0;
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    @Override
    public boolean isComplete() {
        if (root == null) return false;

        LightQueue<Node<E>> lightQueue = new LightQueue<>();
        lightQueue.offer(root);
        // 是否只能存在叶子节点
        boolean onlyLeaf = false;
        while (!lightQueue.isEmpty()) {
            Node<E> node = lightQueue.poll();
            // 判断后续的节点是否均为叶子节点
            if (onlyLeaf && !node.isLeaf()) {
                return false;
            }

            if (node.left != null) {
                lightQueue.offer(node);
            } else if (node.right != null) {
                return false;
            }

            if (node.right != null) {
                lightQueue.offer(node.right);
            } else {
                onlyLeaf = true;
            }
        }
        return true;
    }

    /**
     * 获取前驱节点
     * @return 前驱节点
     */
    protected Node<E> predecessor(Node<E> node) {
        if (node == null) return null;

        // 从左子树中寻找前驱节点
        Node<E> p = node.left;
        if (p != null) {
            while (p.right != null) {
                p = p.right;
            }
            return p;
        }

        // 从父节点中寻找前驱节点
        while (node.isLeftChild()) {
            node = node.parent;
        }
        return node.parent;
    }

    /**
     * 获取后继节点
     * @return 后继节点
     */
    protected Node<E> successor(Node<E> node) {
        if (node == null) return null;

        // 从左子树中寻找后继节点
        Node<E> p = node.right;
        if (p != null) {
            while (p.left != null) {
                p = p.left;
            }
            return p;
        }

        // 从父节点中寻找后继节点
        while (node.isRightChild()) {
            node = node.parent;
        }
        return node.parent;
    }


    /**
     * 前序遍历
     */
    public void preorderTraversal(Consumer<E> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("遍历处理器不能为空！");
        }
        preorderTraversal(root, consumer);
    }

    private void preorderTraversal(Node<E> node, Consumer<E> consumer) {
        if (node == null) return;

        consumer.accept(node.element);
        preorderTraversal(node.left, consumer);
        preorderTraversal(node.right, consumer);
    }

    /**
     * 中序遍历
     */
    public void inorderTraversal(Consumer<E> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("遍历处理器不能为空！");
        }
        inorderTraversal(root, consumer);
    }

    private void inorderTraversal(Node<E> node, Consumer<E> consumer) {
        if (node == null) return;

        inorderTraversal(node.left, consumer);
        consumer.accept(node.element);
        inorderTraversal(node.right, consumer);
    }

    /**
     * 后序遍历
     */
    public void postorderTraversal(Consumer<E> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("遍历处理器不能为空！");
        }
        postorderTraversal(root, consumer);
    }

    private void postorderTraversal(Node<E> node, Consumer<E> consumer) {
        if (node == null) return;

        postorderTraversal(node.left, consumer);
        postorderTraversal(node.right, consumer);
        consumer.accept(node.element);
    }

    /**
     * 层序遍历
     */
    public void levelOrderTraversal(Consumer<E> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("遍历处理器不能为空！");
        }
        if (root == null) return;

        LightQueue<Node<E>> queue = new LightQueue<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node<E> head = queue.poll();
            Node<E> left = head.left, right = head.right;
            if (left != null) {
                queue.offer(left);
            }
            if (right != null) {
                queue.offer(right);
            }
            consumer.accept(head.element);
        }
    }

    @Override
    public Object root() {
        return root;
    }

    @Override
    public Object left(Object node) {
        return ((Node<E>) node).left;
    }

    @Override
    public Object right(Object node) {
        return ((Node<E>) node).right;
    }

    @Override
    public Object string(Object node) {
        return node;
    }
}

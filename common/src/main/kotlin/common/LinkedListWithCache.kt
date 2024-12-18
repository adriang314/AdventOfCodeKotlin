package common

class LinkedListWithCache<E> {
    private val map = mutableMapOf<E, Node<E>>()
    private var size = 0
    private var head: Node<E>? = null
    private var tail: Node<E>? = null

    private class Node<E>(
        var prev: Node<E>?,
        var element: E,
        var next: Node<E>?
    )

    fun forEach(action: (E) -> Unit) {
        var currElement = head
        while (currElement != null) {
            val nextElement = currElement.next
            action(currElement.element)
            currElement = nextElement
        }
    }

    fun getBefore(element: E, offset: Int): E {
        var node = map[element]!!

        repeat(offset) {
            node = node.prev ?: tail!!
        }

        return node.element
    }

    fun getAfter(element: E, offset: Int): E {
        var node = map[element]!!

        repeat(offset) {
            node = node.next ?: head!!
        }

        return node.element
    }

    fun getFirst() = head?.element

    fun getLast() = tail?.element

    fun removeFirst() = unlinkHead()

    fun removeLast() = unlinkTail()

    fun addFirst(element: E) = linkHead(element)

    fun addLast(element: E) = linkTail(element)

    fun add(element: E) = linkTail(element)

    fun remove(element: E) = unlink(map[element]!!)

    fun size() = size

    fun addBefore(element: E, beforeElement: E) = linkBefore(element, map[beforeElement]!!)

    fun addAfter(element: E, afterElement: E) {
        val afterElementNode = map[afterElement]!!
        if (afterElementNode.next != null)
            linkBefore(element, afterElementNode.next!!)
        else
            linkTail(element)
    }

    operator fun contains(element: E) = map.containsKey(element)

    private fun linkHead(element: E) {
        val h = head
        val newNode = Node(null, element, h)
        head = newNode
        if (h == null) {
            tail = newNode
        } else {
            h.prev = newNode
        }
        size++
        map[element] = newNode
    }

    private fun linkTail(element: E) {
        val t = tail
        val newNode = Node(t, element, null)
        tail = newNode
        if (t == null) {
            head = newNode
        } else {
            t.next = newNode
        }
        size++
        map[element] = newNode
    }

    private fun linkBefore(element: E, successor: Node<E>) {
        val predecessor = successor.prev
        val newNode = Node(predecessor, element, successor)
        successor.prev = newNode
        if (predecessor == null) {
            head = newNode
        } else {
            predecessor.next = newNode
        }
        size++
        map[element] = newNode
    }

    private fun unlinkHead() {
        head?.let {
            val next = it.next
            it.next = null
            head = next
            if (next == null) {
                tail = null
            } else {
                next.prev = null
            }
            size--
        }
    }

    private fun unlinkTail() {
        tail?.let {
            val prev = it.prev
            it.prev = null
            tail = prev
            if (prev == null) {
                head = null
            } else {
                prev.next = null
            }
            size--
        }
    }

    private fun unlink(curr: Node<E>): E {
        val element = curr.element
        val next = curr.next
        val prev = curr.prev

        if (prev == null) {
            head = next
        } else {
            prev.next = next
            curr.prev = null
        }

        if (next == null) {
            tail = prev
        } else {
            next.prev = prev
            curr.next = null
        }

        size--
        return element
    }

    override fun toString(): String {
        var curr = head
        if (curr == null) return "[]"
        else {
            val sb = StringBuilder()
            sb.append('[')
            while (curr != null) {
                sb.append(curr.element)
                curr = curr.next
                if (curr?.element == null) {
                    sb.append(']')
                } else {
                    sb.append(',').append(' ')
                }
            }
            return sb.toString()
        }
    }
}
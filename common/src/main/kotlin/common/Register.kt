package common

/**
 * Register class for storing values in registers
 */
class Register(vararg values: Int) {

    constructor(size: Int) : this(*IntArray(size) { 0 })

    private val items = values

    /**
     * Read value from register
     *
     * @param regId register id
     */
    fun read(regId: Int) = items[regId]

    /**
     * Store value in register
     *
     * @param value value to store
     * @param regId register id
     */
    fun store(value: Int, regId: Int) {
        items[regId] = value
    }

    /**
     * Copy register
     */
    fun copy() = Register(items.size).also { it.items.indices.forEach { i -> it.items[i] = items[i] } }

    /*
     * Convert register to string
     */
    override fun toString(): String {
        return items.joinToString(prefix = "[", postfix = "]", separator = " ")
    }

    /**
     * Check if two registers are equal
     *
     * @param other register
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Register) return false
        if (!items.contentEquals(other.items)) return false
        return true
    }

    /**
     * Get hash code of register
     */
    override fun hashCode(): Int {
        return items.contentHashCode()
    }
}
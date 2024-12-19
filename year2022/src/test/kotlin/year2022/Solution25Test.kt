package year2022

import org.junit.jupiter.api.Test
import year2022.SolutionDay25.*
import kotlin.test.assertEquals

class Solution25Test {

    @Test
    fun testDecode() {
        assertEquals(SnafuNumber("1=1"), SnafuNumber.from(16L))
    }

    @Test
    fun test() {
        assertEquals(1, SnafuNumber("1").decimalNumber)
        assertEquals(2, SnafuNumber("2").decimalNumber)
        assertEquals(3, SnafuNumber("1=").decimalNumber)
        assertEquals(4, SnafuNumber("1-").decimalNumber)
        assertEquals(5, SnafuNumber("10").decimalNumber)
        assertEquals(6, SnafuNumber("11").decimalNumber)
        assertEquals(7, SnafuNumber("12").decimalNumber)
        assertEquals(8, SnafuNumber("2=").decimalNumber)
        assertEquals(9, SnafuNumber("2-").decimalNumber)
        assertEquals(10, SnafuNumber("20").decimalNumber)
        assertEquals(11, SnafuNumber("21").decimalNumber)
        assertEquals(12, SnafuNumber("22").decimalNumber)
        assertEquals(13, SnafuNumber("1==").decimalNumber)
        assertEquals(14, SnafuNumber("1=-").decimalNumber)
        assertEquals(15, SnafuNumber("1=0").decimalNumber)
        assertEquals(16, SnafuNumber("1=1").decimalNumber)
        assertEquals(17, SnafuNumber("1=2").decimalNumber)
        assertEquals(18, SnafuNumber("1-=").decimalNumber)
        assertEquals(19, SnafuNumber("1--").decimalNumber)
        assertEquals(20, SnafuNumber("1-0").decimalNumber)
        assertEquals(21, SnafuNumber("1-1").decimalNumber)
        assertEquals(22, SnafuNumber("1-2").decimalNumber)
        assertEquals(23, SnafuNumber("10=").decimalNumber)
        assertEquals(24, SnafuNumber("10-").decimalNumber)
        assertEquals(25, SnafuNumber("100").decimalNumber)
        assertEquals(26, SnafuNumber("101").decimalNumber)
        assertEquals(27, SnafuNumber("102").decimalNumber)
        assertEquals(28, SnafuNumber("11=").decimalNumber)
        assertEquals(29, SnafuNumber("11-").decimalNumber)
        assertEquals(30, SnafuNumber("110").decimalNumber)
        assertEquals(31, SnafuNumber("111").decimalNumber)
        assertEquals(32, SnafuNumber("112").decimalNumber)
        assertEquals(33, SnafuNumber("12=").decimalNumber)
        assertEquals(34, SnafuNumber("12-").decimalNumber)
        assertEquals(35, SnafuNumber("120").decimalNumber)
        assertEquals(36, SnafuNumber("121").decimalNumber)
        assertEquals(37, SnafuNumber("122").decimalNumber)
        assertEquals(38, SnafuNumber("2==").decimalNumber)
        assertEquals(39, SnafuNumber("2=-").decimalNumber)
        assertEquals(40, SnafuNumber("2=0").decimalNumber)
        assertEquals(41, SnafuNumber("2=1").decimalNumber)
        assertEquals(42, SnafuNumber("2=2").decimalNumber)
        assertEquals(43, SnafuNumber("2-=").decimalNumber)
        assertEquals(44, SnafuNumber("2--").decimalNumber)
        assertEquals(45, SnafuNumber("2-0").decimalNumber)
        assertEquals(46, SnafuNumber("2-1").decimalNumber)
        assertEquals(47, SnafuNumber("2-2").decimalNumber)
        assertEquals(48, SnafuNumber("20=").decimalNumber)
        assertEquals(49, SnafuNumber("20-").decimalNumber)
        assertEquals(50, SnafuNumber("200").decimalNumber)
        assertEquals(51, SnafuNumber("201").decimalNumber)
        assertEquals(52, SnafuNumber("202").decimalNumber)
        assertEquals(53, SnafuNumber("21=").decimalNumber)
        assertEquals(54, SnafuNumber("21-").decimalNumber)
        assertEquals(55, SnafuNumber("210").decimalNumber)
        assertEquals(56, SnafuNumber("211").decimalNumber)
        assertEquals(57, SnafuNumber("212").decimalNumber)
        assertEquals(58, SnafuNumber("22=").decimalNumber)
        assertEquals(59, SnafuNumber("22-").decimalNumber)
        assertEquals(60, SnafuNumber("220").decimalNumber)
        assertEquals(61, SnafuNumber("221").decimalNumber)
        assertEquals(62, SnafuNumber("222").decimalNumber)
        assertEquals(63, SnafuNumber("1===").decimalNumber)
        assertEquals(64, SnafuNumber("1==-").decimalNumber)
        assertEquals(65, SnafuNumber("1==0").decimalNumber)
        assertEquals(66, SnafuNumber("1==1").decimalNumber)
        assertEquals(67, SnafuNumber("1==2").decimalNumber)
        assertEquals(68, SnafuNumber("1=-=").decimalNumber)
        assertEquals(125, SnafuNumber("1000").decimalNumber)
        assertEquals(198, SnafuNumber("2=0=").decimalNumber)
        assertEquals(312, SnafuNumber("2222").decimalNumber)
        assertEquals(353, SnafuNumber("1=-1=").decimalNumber)
        assertEquals(625, SnafuNumber("10000").decimalNumber)
        assertEquals(906, SnafuNumber("12111").decimalNumber)
        assertEquals(1257, SnafuNumber("20012").decimalNumber)
        assertEquals(1747, SnafuNumber("1=-0-2").decimalNumber)
        assertEquals(2022, SnafuNumber("1=11-2").decimalNumber)
        assertEquals(3125, SnafuNumber("100000").decimalNumber)
        assertEquals(12345, SnafuNumber("1-0---0").decimalNumber)
        assertEquals(314159265, SnafuNumber("1121-1110-1=0").decimalNumber)
    }
}
import org.junit.jupiter.api.Test
import solution.SolutionDay12.Line
import kotlin.test.assertEquals

class Solution12Test {

    @Test
    fun test(){

//        assertEquals(1, Line("# 1").result)
//        assertEquals(0, Line(". 1").result)
//        assertEquals(1, Line("? 1").result)
//        assertEquals(1, Line("?# 1").result)
//        assertEquals(1, Line("?? 2").result)
//        assertEquals(2, Line("?? 1").result)
//        assertEquals(1, Line("?.? 1,1").result)
//        assertEquals(2, Line("?? 1").result)
//
//
//        //assertEquals(1, Line("?.? 1,1").result)
//        assertEquals(2, Line("??.? 1,1").result)
//        assertEquals(3, Line("???? 1,1").result)


       // assertEquals(1, Line("???.###????.### 1,1,3,1,1,3").result)
//        assertEquals(4, Line(".??..??...?##. 1,1,3").result)
//        assertEquals(1, Line("?#?#?#?#?#?#?#? 1,3,1,6").result)
//        assertEquals(1, Line("????.#...#... 4,1,1").result)
//        assertEquals(4, Line("????.######..#####. 1,6,5").result)
//        assertEquals(10, Line("?###???????? 3,2,1").result)
        assertEquals(4, Line(".??..??...?##.? 1,1,3").result)
        assertEquals(15, Line("?###????????? 3,2,1").result)
        assertEquals(1, Line("???.### 1,1,3").result)
        assertEquals(1, Line("????.### 1,1,3").result)
        assertEquals(1, Line("???.###? 1,1,3").result)




    }
}
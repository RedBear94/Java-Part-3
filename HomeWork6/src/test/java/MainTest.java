import com.company.Main;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MainTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {new int[] {0, 0, 0 , 4, 3, 5}, new int[] {3, 5}, true, new int[] {1, 3, 5}},
                {new int[] {1, 1, 2, 4, 2, 2, 3}, new int[] {2, 2, 3}, true, new int[] {8, 1, 6}},
                {new int[] {1, 2, 4, 4, 2, 3, 1, 1, 7}, new int[] {2, 3, 1, 1, 7}, true, new int[] {2, 4, 5}},
        });
    }

    private int[] arrayInt;
    private int[] result;
    private int[] arrayTestIncludeFourOrOne;
    private boolean resultTestArray;

    public MainTest(int[] arrayInt, int[] result, boolean resultTestArray, int[] arrayTestIncludeFourOrOne) {
        this.arrayInt = arrayInt;
        this.result = result;
        this.resultTestArray = resultTestArray;
        this.arrayTestIncludeFourOrOne = arrayTestIncludeFourOrOne;
    }

    private Main main;

    @Test
    public void testModifyArray() {
        Assert.assertArrayEquals(result, main.modifyArray(arrayInt));
    }

    @Test(expected = RuntimeException.class)
    public void testException() throws RuntimeException{
        Assert.assertArrayEquals(new int[] {0}, Main.modifyArray(new int[] {0}));
    }

    @Test
    public void testMethodFindOneOrFour(){
        Assert.assertEquals(resultTestArray, Main.arrayIncludeOneOrFour(arrayTestIncludeFourOrOne));
    }
}

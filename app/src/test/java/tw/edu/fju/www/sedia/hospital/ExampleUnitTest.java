package tw.edu.fju.www.sedia.hospital;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static final String[] Hospital_Division2 = {"心臟內科", "精神科", "腎臟科", "呼吸胸腔科", "新陳代謝科", "家醫科", "胃腸肝膽科", "腫瘤科", "兒童內科"};

    @Test
    public void test() {
        Stream.of(Hospital_Division2).forEach(value -> {
            StringBuilder item = new StringBuilder("<item>");
            item.append(value).append("</item>");
            System.out.println(item.toString());

            assertEquals(5, 5);
        });
    }
}
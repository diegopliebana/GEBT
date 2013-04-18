package ch.idsia.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: Jul 25, 2009
 * Time: 3:26:43 PM
 * Package: ch.idsia.utils
 */
public class MathX
{
    public static void show(char el) {
        System.out.print("block (" + Integer.valueOf(el) + ") :");
        for (int i = 0;i < 16; ++i)
            System.out.print((el << i ) + " ");
        System.out.println("");
    }

}

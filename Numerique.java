package fonction;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Numerique {
   double min() default Double.MIN_VALUE;
   double max() default Double.MAX_VALUE;
   String message() default "tsy numerique ilay donner na tsy ao anatin' io valeur omenao io ilay donner";
}

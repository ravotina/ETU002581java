package fonction;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
   String message() default "tsy maitsy misy donner ao";
}

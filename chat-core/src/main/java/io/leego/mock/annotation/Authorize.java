package io.leego.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying a method access-control authorization.
 *
 * @author Leego Yih
 */
@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Authorize.List.class)
public @interface Authorize {

    /** Determines if the authentication has a particular role. */
    String role() default "";

    /** Determines if the authentication has any of the specified roles. */
    String[] anyRole() default {};

    /** Determines if the authentication has a particular authority. */
    String authority() default "";

    /** Determines if the authentication has any of the specified authorities. */
    String[] anyAuthority() default {};

    /** Always grants access. */
    boolean permitAll() default false;

    /** Always denies access. */
    boolean denyAll() default false;

    @Inherited
    @Documented
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Authorize[] value();
    }
}

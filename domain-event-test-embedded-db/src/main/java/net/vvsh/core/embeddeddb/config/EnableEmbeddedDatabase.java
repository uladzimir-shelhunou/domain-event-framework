package net.vvsh.core.embeddeddb.config;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureEmbeddedDatabase
public @interface EnableEmbeddedDatabase {

    @AliasFor(annotation = AutoConfigureEmbeddedDatabase.class)
    AutoConfigureEmbeddedDatabase.DatabaseProvider provider() default AutoConfigureEmbeddedDatabase.DatabaseProvider.DEFAULT;

    @AliasFor(annotation = AutoConfigureEmbeddedDatabase.class)
    AutoConfigureEmbeddedDatabase.DatabaseType type() default AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES;

    @AliasFor(annotation = AutoConfigureEmbeddedDatabase.class)
    AutoConfigureEmbeddedDatabase.RefreshMode refresh() default AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
}
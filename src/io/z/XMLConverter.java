package io.z;

import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.thoughtworks.xstream.converters.Converter;

/** This annotation is used to denote the XStream converter that is associated 
 * to a certain class. We do not use the XStream annotation "XStreamConverter",
 * because we need to pass arguments to the converter's constructor, which is 
 * not possible when using "XStreamConverter".
 *
 * @author Timon Kelter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface XMLConverter {
    Class<? extends ReflectionConverter> value();
}
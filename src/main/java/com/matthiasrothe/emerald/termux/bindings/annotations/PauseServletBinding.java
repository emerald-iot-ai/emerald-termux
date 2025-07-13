package com.matthiasrothe.emerald.termux.bindings.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

@Retention(RUNTIME)
@Target(FIELD)
@BindingAnnotation
public @interface PauseServletBinding {

}

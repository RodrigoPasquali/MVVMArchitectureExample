package com.example.mvvmarchitectureexample.di;

import androidx.lifecycle.ViewModel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.MapKey;

@Documented //Para documentar con javadoc
@Target({ElementType.METHOD}) //Lugares donde se aplica esta anotacion(en este caso en metodos)
@Retention(RetentionPolicy.RUNTIME) //Se chequea en tiempo de ejecucion
@MapKey //Ofrece la clave especifica
public @interface ViewModelKey {
    Class<? extends ViewModel> value();
}

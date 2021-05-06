package hr.fer.zemris.ooup.lab3;

import hr.fer.zemris.ooup.lab3.model.Animal;

import java.lang.reflect.Constructor;

public class AnimalFactory {
    public static Animal newInstance(String animalKind, String name) throws Exception {
        Class<Animal> clazz = null;
        clazz = (Class<Animal>) Class.forName("hr.fer.zemris.ooup.lab3.model.plugins."+animalKind);
        Constructor<?> ctr = clazz.getConstructor(String.class);
        return (Animal) ctr.newInstance(name);
    }
}
